package com.mashang.rechargewithdraw.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.mashang.common.exception.BizError;
import com.mashang.common.exception.BizException;
import com.mashang.common.utils.IdUtils;
import com.mashang.common.utils.ThreadPoolUtils;
import com.mashang.common.valid.ParamValid;
import com.mashang.common.vo.PageResult;
import com.mashang.constants.Constant;
import com.mashang.income.domain.IncomeRecord;
import com.mashang.income.repo.IncomeRecordRepo;
import com.mashang.mastercontrol.domain.RechargeSetting;
import com.mashang.mastercontrol.repo.RechargeSettingRepo;
import com.mashang.rechargewithdraw.domain.RechargeChannel;
import com.mashang.rechargewithdraw.domain.RechargeOrder;
import com.mashang.rechargewithdraw.domain.ServiceProviderRechargeChannel;
import com.mashang.rechargewithdraw.param.BankCardRechargeParam;
import com.mashang.rechargewithdraw.param.FastRechargeParam;
import com.mashang.rechargewithdraw.param.FastRechargeSubmitPaymentInfoParam;
import com.mashang.rechargewithdraw.param.RechargeOrderQueryCondParam;
import com.mashang.rechargewithdraw.param.UsdtRechargeParam;
import com.mashang.rechargewithdraw.repo.RechargeChannelRepo;
import com.mashang.rechargewithdraw.repo.RechargeOrderRepo;
import com.mashang.rechargewithdraw.repo.ServiceProviderRechargeChannelRepo;
import com.mashang.rechargewithdraw.vo.RechargeOrderVO;
import com.mashang.useraccount.domain.AccountChangeLog;
import com.mashang.useraccount.domain.UserAccount;
import com.mashang.useraccount.repo.AccountChangeLogRepo;
import com.mashang.useraccount.repo.UserAccountRepo;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.WeightRandom;
import cn.hutool.core.lang.WeightRandom.WeightObj;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class RechargeService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private RechargeOrderRepo rechargeOrderRepo;

	@Autowired
	private UserAccountRepo userAccountRepo;

	@Autowired
	private AccountChangeLogRepo accountChangeLogRepo;

	@Autowired
	private RechargeSettingRepo rechargeSettingRepo;

	@Autowired
	private RechargeChannelRepo rechargeChannelRepo;

	@Autowired
	private ServiceProviderRechargeChannelRepo serviceProviderRechargeChannelRepo;

	@Autowired
	private IncomeRecordRepo incomeRecordRepo;

	@Lock(keys = "'updateRechargeOrderState_' + #id")
	@Transactional
	public void fastRechargeApplyCancelOrder(@NotBlank String id, @NotBlank String serviceProviderId) {
		RechargeOrder order = rechargeOrderRepo.findByIdAndServiceProviderId(id, serviceProviderId);
		if (order == null) {
			throw new BizException("订单不存在");
		}
		if (!Constant.充值订单状态_审核中.equals(order.getOrderState())) {
			throw new BizException("审核中的订单才能发起申诉");
		}
		order.setOrderState(Constant.充值订单状态_申诉中);
		rechargeOrderRepo.save(order);
	}

	@Transactional
	public void fastRechargeConfirmToPaid(@NotBlank String id, @NotBlank String serviceProviderId) {
		RechargeOrder order = rechargeOrderRepo.findByIdAndServiceProviderId(id, serviceProviderId);
		if (order == null) {
			throw new BizException("订单不存在");
		}
		if (!Constant.充值订单状态_审核中.equals(order.getOrderState())) {
			throw new BizException("审核中的订单才能确认已支付");
		}
		confirmToPaid(id);
	}

	@Lock(keys = "'updateRechargeOrderState_' + #id")
	@Transactional
	public void confirmToPaid(@NotBlank String id) {
		RechargeOrder order = rechargeOrderRepo.getOne(id);
		if (!(Constant.充值订单状态_审核中.equals(order.getOrderState()) || Constant.充值订单状态_申诉中.equals(order.getOrderState()))) {
			throw new BizException("只有审核中,申诉中的订单才能确认已支付");
		}

		order.paid();
		rechargeOrderRepo.save(order);
		ThreadPoolUtils.getRechargeSettlementPool().schedule(() -> {
			redisTemplate.opsForList().leftPush(Constant.充值订单_已支付订单单号, order.getOrderNo());
		}, 1, TimeUnit.SECONDS);
	}

	@Lock(keys = "'rechargeOrderSettlement_' + #orderNo")
	@Transactional
	public void rechargeOrderSettlement(String orderNo) {
		RechargeOrder order = rechargeOrderRepo.findByOrderNo(orderNo);
		if (order == null) {
			throw new BizException(BizError.充值订单不存在);
		}
		if (!Constant.充值订单状态_已支付.equals(order.getOrderState())) {
			return;
		}
		if (order.getSettlementTime() != null) {
			return;
		}

		double serviceProviderIncome = 0d;
		if (Constant.充值方式_快速充值.equals(order.getRechargeWay())) {
			UserAccount serviceProvider = order.getServiceProvider();
			serviceProvider.setServiceProviderFreezeAmount(
					NumberUtil.round(serviceProvider.getServiceProviderFreezeAmount() - order.getRechargeAmount(), 2)
							.doubleValue());
			userAccountRepo.save(serviceProvider);

			RechargeSetting rechargeSetting = rechargeSettingRepo.findTopByOrderByLatelyUpdateTime();
			Double serviceProviderRechargeIncomeRate = rechargeSetting.getServiceProviderRechargeIncomeRate();
			if (serviceProviderRechargeIncomeRate != null && serviceProviderRechargeIncomeRate > 0) {
				serviceProviderIncome = NumberUtil
						.round(order.getRechargeAmount() * serviceProviderRechargeIncomeRate * 0.01, 2).doubleValue();
				IncomeRecord serviceProviderIncomeRecord = IncomeRecord.build(order.getId(), Constant.收益类型_代充收益,
						serviceProviderIncome, order.getServiceProviderId());
				incomeRecordRepo.save(serviceProviderIncomeRecord);
				ThreadPoolUtils.getRechargeSettlementPool().schedule(() -> {
					redisTemplate.opsForList().leftPush(Constant.收益记录ID, serviceProviderIncomeRecord.getId());
				}, 1, TimeUnit.SECONDS);
			}
		}

		order.setServiceProviderIncome(serviceProviderIncome);
		order.settlement();
		rechargeOrderRepo.save(order);
		UserAccount userAccount = order.getUserAccount();
		double cashDeposit = userAccount.getCashDeposit() + order.getRechargeAmount();
		userAccount.setCashDeposit(NumberUtil.round(cashDeposit, 2).doubleValue());
		userAccountRepo.save(userAccount);
		accountChangeLogRepo.save(AccountChangeLog.buildWithRecharge(userAccount, order));
	}

	@Transactional(readOnly = true)
	public void rechargeOrderAutoSettlement() {
		List<RechargeOrder> orders = rechargeOrderRepo.findByOrderStateAndSettlementTimeIsNull(Constant.充值订单状态_已支付);
		for (RechargeOrder order : orders) {
			redisTemplate.opsForList().leftPush(Constant.充值订单_已支付订单单号, order.getOrderNo());
		}
	}

	@Lock(keys = "'fastRechargeSubmitPaymentInfo_' + #param.userAccountId")
	@Transactional
	public void fastRechargeSubmitPaymentInfo(FastRechargeSubmitPaymentInfoParam param) {
		RechargeOrder order = rechargeOrderRepo.findByIdAndUserAccountId(param.getRechargeOrderId(),
				param.getUserAccountId());
		if (!Constant.充值方式_快速充值.equals(order.getRechargeWay())) {
			throw new BizException("充值方式异常");
		}
		if (!Constant.充值订单状态_审核中.equals(order.getOrderState())) {
			throw new BizException("只有审核中的订单才能提交付款信息");
		}
		if (StrUtil.isNotBlank(order.getDepositor())) {
			throw new BizException("已提交付款信息,不能重复提交");
		}
		order.setDepositor(param.getDepositor());
		order.setDepositTime(param.getDepositTime());
		rechargeOrderRepo.save(order);
	}

	@Lock(keys = "'fastRechargeDispatchOrder_' + #orderKey")
	@Transactional
	public void fastRechargeDispatchOrder(String orderKey) {
		String orderKeyError = orderKey + "_ERROR";
		String orderKeyOrderId = orderKey + "_ORDER_ID";
		String orderValue = redisTemplate.opsForValue().get(orderKey);
		String[] split = orderValue.split("_");
		String userAccountId = split[0];
		Double rechargeAmount = Double.parseDouble(split[1]);
		FastRechargeParam param = new FastRechargeParam();
		param.setUserAccountId(userAccountId);
		param.setRechargeAmount(rechargeAmount);
		List<UserAccount> serviceProviders = userAccountRepo
				.findByStateAndServiceProviderAmountGreaterThanEqualAndDeletedFlagIsFalse(Constant.账号状态_启用,
						param.getRechargeAmount());
		if (CollectionUtil.isEmpty(serviceProviders)) {
			redisTemplate.opsForValue().set(orderKeyError, "暂无可用的通道", 30L, TimeUnit.SECONDS);
			return;
		}
		List<String> serviceProviderIds = new ArrayList<>();
		for (UserAccount serviceProvider : serviceProviders) {
			if (serviceProvider.getId().equals(param.getUserAccountId())) {
				continue;
			}
			serviceProviderIds.add(serviceProvider.getId());
		}
		List<ServiceProviderRechargeChannel> rechargeChannels = serviceProviderRechargeChannelRepo
				.findByUserAccountIdInAndDeletedFlagIsFalse(serviceProviderIds);
		if (CollectionUtil.isEmpty(rechargeChannels)) {
			redisTemplate.opsForValue().set(orderKeyError, "暂无可用的银行卡", 30L, TimeUnit.SECONDS);
			return;
		}
		Map<String, List<ServiceProviderRechargeChannel>> accountRechargeChannelMap = new HashMap<>();
		for (ServiceProviderRechargeChannel rechargeChannel : rechargeChannels) {
			if (accountRechargeChannelMap.get(rechargeChannel.getUserAccountId()) == null) {
				accountRechargeChannelMap.put(rechargeChannel.getUserAccountId(), new ArrayList<>());
			}
			accountRechargeChannelMap.get(rechargeChannel.getUserAccountId()).add(rechargeChannel);
		}
		List<WeightObj<UserAccount>> weightObjs = new ArrayList<>();
		for (UserAccount serviceProvider : serviceProviders) {
			WeightObj<UserAccount> weightObj = new WeightObj<>(serviceProvider,
					serviceProvider.getServiceProviderAmount());
			weightObjs.add(weightObj);
		}
		WeightRandom<UserAccount> weightRandom = RandomUtil.weightRandom(weightObjs);
		for (int i = 0; i < serviceProviders.size(); i++) {
			UserAccount serviceProvider = weightRandom.next();
			double serviceProviderAmount = NumberUtil
					.round(serviceProvider.getServiceProviderAmount() - param.getRechargeAmount(), 2).doubleValue();
			if (serviceProviderAmount < 0) {
				continue;
			}
			List<ServiceProviderRechargeChannel> accountRechargeChannels = accountRechargeChannelMap
					.get(serviceProvider.getId());
			if (CollectionUtil.isEmpty(accountRechargeChannels)) {
				continue;
			}
			Collections.shuffle(accountRechargeChannels);
			ServiceProviderRechargeChannel rechargeChannel = accountRechargeChannels.get(0);

			RechargeOrder rechargeOrder = param.convertToPo();
			rechargeOrder.setServiceProviderId(serviceProvider.getId());
			rechargeOrder.setOpenAccountBank(rechargeChannel.getOpenAccountBank());
			rechargeOrder.setAccountHolder(rechargeChannel.getAccountHolder());
			rechargeOrder.setBankCardAccount(rechargeChannel.getBankCardAccount());
			rechargeOrderRepo.save(rechargeOrder);

			serviceProvider.setServiceProviderAmount(serviceProviderAmount);
			serviceProvider.setServiceProviderFreezeAmount(
					NumberUtil.round(serviceProvider.getServiceProviderFreezeAmount() + param.getRechargeAmount(), 2)
							.doubleValue());
			userAccountRepo.save(serviceProvider);
			redisTemplate.opsForValue().set(orderKeyError, "success", 30L, TimeUnit.SECONDS);
			redisTemplate.opsForValue().set(orderKeyOrderId, rechargeOrder.getId(), 30L, TimeUnit.SECONDS);
			break;
		}
	}

	@ParamValid
	@Transactional(readOnly = true)
	public Map<String, String> getFastRechargeOrderId(String orderKey) {
		Map<String, String> map = new HashMap<>();
		String errorValue = redisTemplate.opsForValue().get(orderKey + "_ERROR");
		String orderId = redisTemplate.opsForValue().get(orderKey + "_ORDER_ID");
		// 未派单
		if (StrUtil.isBlank(errorValue)) {
			map.put("result", "pending");
		}
		// 匹配成功
		else if ("success".equals(errorValue)) {
			map.put("result", "success");
			map.put("orderId", orderId);
		}
		// 匹配不到
		else {
			map.put("result", "fail");
			map.put("msg", errorValue);
		}
		return map;
	}

	@Lock(keys = "'fastRecharge_' + #param.userAccountId")
	@ParamValid
	@Transactional
	public String fastRecharge(FastRechargeParam param) {
		RechargeSetting rechargeSetting = rechargeSettingRepo.findTopByOrderByLatelyUpdateTime();
		List<RechargeOrder> orders = rechargeOrderRepo.findByOrderStateAndUserAccountId(Constant.充值订单状态_审核中,
				param.getUserAccountId());
		if (rechargeSetting.getCantContinuousSubmit() && CollectionUtil.isNotEmpty(orders)) {
			throw new BizException("你有充值订单未处理完,请联系客服人员");
		}
		if (param.getRechargeAmount() < rechargeSetting.getRechargeLowerLimit()) {
			throw new BizException(BizError.参数异常.getCode(),
					"最低充值金额不能小于" + new DecimalFormat("###################.###########")
							.format(rechargeSetting.getRechargeLowerLimit()));
		}
		if (param.getRechargeAmount() > rechargeSetting.getRechargeUpperLimit()) {
			throw new BizException(BizError.参数异常.getCode(),
					"最高充值金额不能大于" + new DecimalFormat("###################.###########")
							.format(rechargeSetting.getRechargeUpperLimit()));
		}
		String orderKey = "FAST_RECHARGE_TMP_KEY_" + IdUtils.getId();
		redisTemplate.opsForValue().set(orderKey,
				param.getUserAccountId() + "_"
						+ new DecimalFormat("###################.###########").format(param.getRechargeAmount()),
				30L, TimeUnit.SECONDS);
		redisTemplate.opsForList().leftPush(Constant.快速充值派单ID, orderKey);
		return orderKey;
	}

	@ParamValid
	@Transactional
	public void usdtRecharge(UsdtRechargeParam param) {
		RechargeSetting rechargeSetting = rechargeSettingRepo.findTopByOrderByLatelyUpdateTime();
		Double usdtCnyExchangeRate = rechargeSetting.getUsdtCnyExchangeRate();
		if (usdtCnyExchangeRate <= 0) {
			throw new BizException("USDT汇率异常");
		}
		RechargeChannel rechargeChannel = rechargeChannelRepo.getOne(param.getRechargeChannelId());
		List<RechargeOrder> orders = rechargeOrderRepo.findByOrderStateAndUserAccountId(Constant.充值订单状态_审核中,
				param.getUserAccountId());
		if (rechargeSetting.getCantContinuousSubmit() && CollectionUtil.isNotEmpty(orders)) {
			throw new BizException("你有充值订单未处理完,请联系客服人员");
		}
		if (param.getRechargeAmount() < rechargeSetting.getRechargeLowerLimit()) {
			throw new BizException(BizError.参数异常.getCode(),
					"最低充值金额不能小于" + new DecimalFormat("###################.###########")
							.format(rechargeSetting.getRechargeLowerLimit()));
		}
		if (param.getRechargeAmount() > rechargeSetting.getRechargeUpperLimit()) {
			throw new BizException(BizError.参数异常.getCode(),
					"最高充值金额不能大于" + new DecimalFormat("###################.###########")
							.format(rechargeSetting.getRechargeUpperLimit()));
		}

		double usdtQuantity = NumberUtil.round(param.getRechargeAmount() / usdtCnyExchangeRate, 4).doubleValue();
		RechargeOrder rechargeOrder = param.convertToPo();
		rechargeOrder.setAddressType(rechargeChannel.getAddressType());
		rechargeOrder.setAddress(rechargeChannel.getAddress());
		rechargeOrder.setUsdtCnyExchangeRate(usdtCnyExchangeRate);
		rechargeOrder.setUsdtQuantity(usdtQuantity);
		rechargeOrderRepo.save(rechargeOrder);
	}

	@ParamValid
	@Transactional
	public void bankCardRecharge(BankCardRechargeParam param) {
		RechargeChannel rechargeChannel = rechargeChannelRepo.getOne(param.getRechargeChannelId());
		RechargeSetting rechargeSetting = rechargeSettingRepo.findTopByOrderByLatelyUpdateTime();
		List<RechargeOrder> orders = rechargeOrderRepo.findByOrderStateAndUserAccountId(Constant.充值订单状态_审核中,
				param.getUserAccountId());
		if (rechargeSetting.getCantContinuousSubmit() && CollectionUtil.isNotEmpty(orders)) {
			throw new BizException("你有充值订单未处理完,请联系客服人员");
		}
		if (param.getRechargeAmount() < rechargeSetting.getRechargeLowerLimit()) {
			throw new BizException(BizError.参数异常.getCode(),
					"最低充值金额不能小于" + new DecimalFormat("###################.###########")
							.format(rechargeSetting.getRechargeLowerLimit()));
		}
		if (param.getRechargeAmount() > rechargeSetting.getRechargeUpperLimit()) {
			throw new BizException(BizError.参数异常.getCode(),
					"最高充值金额不能大于" + new DecimalFormat("###################.###########")
							.format(rechargeSetting.getRechargeUpperLimit()));
		}
		RechargeOrder rechargeOrder = param.convertToPo();
		rechargeOrder.setOpenAccountBank(rechargeChannel.getOpenAccountBank());
		rechargeOrder.setAccountHolder(rechargeChannel.getAccountHolder());
		rechargeOrder.setBankCardAccount(rechargeChannel.getBankCardAccount());
		rechargeOrderRepo.save(rechargeOrder);
	}

	public Specification<RechargeOrder> buildRechargeOrderQueryCond(RechargeOrderQueryCondParam param) {
		Specification<RechargeOrder> spec = new Specification<RechargeOrder>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<RechargeOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getUserAccountId())) {
					predicates.add(builder.equal(root.get("userAccountId"), param.getUserAccountId()));
				}
				if (StrUtil.isNotBlank(param.getRechargeWay())) {
					predicates.add(builder.equal(root.get("rechargeWay"), param.getRechargeWay()));
				}
				if (StrUtil.isNotBlank(param.getServiceProviderId())) {
					predicates.add(builder.equal(root.get("serviceProviderId"), param.getServiceProviderId()));
				}
				if (StrUtil.isNotBlank(param.getServiceProviderUserName())) {
					predicates.add(builder.equal(root.join("serviceProvider", JoinType.INNER).get("userName"),
							param.getServiceProviderUserName()));
				}
				if (StrUtil.isNotBlank(param.getOrderState())) {
					predicates.add(builder.equal(root.get("orderState"), param.getOrderState()));
				}
				if (StrUtil.isNotBlank(param.getBankCardInfo())) {
					Predicate or = builder.or(
							builder.like(root.get("bankCardAccount"), "%" + param.getBankCardInfo() + "%"),
							builder.like(root.get("accountHolder"), "%" + param.getBankCardInfo() + "%"),
							builder.like(root.get("address"), "%" + param.getBankCardInfo() + "%"));
					Predicate and = builder.and(or);
					predicates.add(and);
				}
				if (StrUtil.isNotBlank(param.getDepositorInfo())) {
					Predicate or = builder.or(builder.like(root.get("depositor"), "%" + param.getDepositorInfo() + "%"),
							builder.like(root.get("tradeId"), "%" + param.getDepositorInfo() + "%"));
					Predicate and = builder.and(or);
					predicates.add(and);
				}
				if (param.getSubmitStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.beginOfDay(param.getSubmitStartTime())));
				}
				if (param.getSubmitEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.endOfDay(param.getSubmitEndTime())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

	@Transactional(readOnly = true)
	public PageResult<RechargeOrderVO> findTop5TodoRechargeOrderByPage() {
		RechargeOrderQueryCondParam param = new RechargeOrderQueryCondParam();
		param.setPageNum(1);
		param.setPageSize(5);
		param.setOrderState(Constant.充值订单状态_审核中);
		param.setRechargeWay(Constant.充值方式_普通充值);

		Specification<RechargeOrder> spec = buildRechargeOrderQueryCond(param);
		Page<RechargeOrder> result = rechargeOrderRepo.findAll(spec,
				PageRequest.of(0, 5, Sort.by(Sort.Order.desc("submitTime"))));
		PageResult<RechargeOrderVO> pageResult = new PageResult<>(RechargeOrderVO.convertFor(result.getContent()), 1, 5,
				result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public List<RechargeOrderVO> findRechargeOrder(RechargeOrderQueryCondParam param) {
		Specification<RechargeOrder> spec = buildRechargeOrderQueryCond(param);
		List<RechargeOrder> result = rechargeOrderRepo.findAll(spec, Sort.by(Sort.Order.desc("submitTime")));
		return RechargeOrderVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public PageResult<RechargeOrderVO> findRechargeOrderByPage(RechargeOrderQueryCondParam param) {
		Specification<RechargeOrder> spec = buildRechargeOrderQueryCond(param);
		Page<RechargeOrder> result = rechargeOrderRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("submitTime"))));
		PageResult<RechargeOrderVO> pageResult = new PageResult<>(RechargeOrderVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public List<RechargeOrderVO> findWaitConfirmFastRechargeOrder(@NotBlank String serviceProviderId) {
		List<RechargeOrder> orders = rechargeOrderRepo.findByOrderStateAndServiceProviderId(Constant.充值订单状态_审核中,
				serviceProviderId);
		return RechargeOrderVO.convertFor(orders);
	}

	@Transactional
	public void fastRechargeCancelOrder(@NotBlank String id, @NotBlank String note, @NotBlank String userAccountId) {
		RechargeOrder order = rechargeOrderRepo.findByIdAndUserAccountId(id, userAccountId);
		if (order == null) {
			throw new BizException("订单不存在");
		}
		if (!Constant.充值订单状态_审核中.equals(order.getOrderState())) {
			throw new BizException("只有审核中的充值订单才能取消");
		}
		cancelOrder(id, note);
	}

	@Lock(keys = "'updateRechargeOrderState_' + #id")
	@Transactional
	public void cancelOrder(@NotBlank String id, @NotBlank String note) {
		RechargeOrder order = rechargeOrderRepo.getOne(id);
		if (!(Constant.充值订单状态_审核中.equals(order.getOrderState()) || Constant.充值订单状态_申诉中.equals(order.getOrderState()))) {
			throw new BizException("只有审核中,申诉中的充值订单才能取消");
		}
		order.setOrderState(Constant.充值订单状态_支付失败);
		order.setDealTime(new Date());
		order.setNote(note);
		rechargeOrderRepo.save(order);

		if (Constant.充值方式_快速充值.equals(order.getRechargeWay())) {
			UserAccount serviceProvider = order.getServiceProvider();
			serviceProvider.setServiceProviderFreezeAmount(
					NumberUtil.round(serviceProvider.getServiceProviderFreezeAmount() - order.getRechargeAmount(), 2)
							.doubleValue());
			serviceProvider.setServiceProviderAmount(NumberUtil
					.round(serviceProvider.getServiceProviderAmount() + order.getRechargeAmount(), 2).doubleValue());
			userAccountRepo.save(serviceProvider);
		}
	}

	@Transactional(readOnly = true)
	public RechargeOrderVO findRechargeOrderById(@NotBlank String id) {
		RechargeOrder rechargeOrder = rechargeOrderRepo.getOne(id);
		return RechargeOrderVO.convertFor(rechargeOrder);
	}

}
