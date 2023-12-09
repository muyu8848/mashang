package com.mashang.distributepayout.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

import com.mashang.common.exception.BizException;
import com.mashang.common.utils.ThreadPoolUtils;
import com.mashang.common.valid.ParamValid;
import com.mashang.common.vo.PageResult;
import com.mashang.constants.Constant;
import com.mashang.distributepayout.domain.DistributePayoutOrder;
import com.mashang.distributepayout.param.DistributePayoutOrderQueryCondParam;
import com.mashang.distributepayout.param.SubmitPaymentVoucherParam;
import com.mashang.distributepayout.repo.DistributePayoutOrderRepo;
import com.mashang.distributepayout.vo.DistributePayoutOrderSummaryVO;
import com.mashang.distributepayout.vo.DistributePayoutOrderVO;
import com.mashang.distributepayout.vo.WaitReceivingDistributePayoutOrderVO;
import com.mashang.income.domain.IncomeRecord;
import com.mashang.income.repo.IncomeRecordRepo;
import com.mashang.mastercontrol.domain.MerchantSettlementSetting;
import com.mashang.mastercontrol.repo.MerchantSettlementSettingRepo;
import com.mashang.merchant.domain.MerchantSettlementRecord;
import com.mashang.merchant.repo.MerchantSettlementRecordRepo;
import com.mashang.useraccount.domain.AccountChangeLog;
import com.mashang.useraccount.domain.UserAccount;
import com.mashang.useraccount.repo.AccountChangeLogRepo;
import com.mashang.useraccount.repo.UserAccountRepo;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class DistributePayoutService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private DistributePayoutOrderRepo distributePayoutOrderRepo;

	@Autowired
	private MerchantSettlementRecordRepo merchantSettlementRecordRepo;

	@Autowired
	private UserAccountRepo userAccountRepo;

	@Autowired
	private AccountChangeLogRepo accountChangeLogRepo;

	@Autowired
	private MerchantSettlementSettingRepo merchantSettlementSettingRepo;

	@Autowired
	private IncomeRecordRepo incomeRecordRepo;

	public Specification<DistributePayoutOrder> buildQueryCond(DistributePayoutOrderQueryCondParam param) {
		Specification<DistributePayoutOrder> spec = new Specification<DistributePayoutOrder>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<DistributePayoutOrder> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getReceivedAccountId())) {
					predicates.add(builder.equal(root.get("receivedAccountId"), param.getReceivedAccountId()));
				}
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMerchantSettlementRecordId())) {
					predicates.add(builder.equal(root.get("merchantSettlementRecordId"),
							param.getMerchantSettlementRecordId()));
				}
				if (StrUtil.isNotBlank(param.getMerchantNum())) {
					predicates.add(builder.equal(root.join("merchantSettlementRecord", JoinType.INNER)
							.join("merchant", JoinType.INNER).get("userName"), param.getMerchantNum()));
				}
				if (StrUtil.isNotBlank(param.getOrderState())) {
					predicates.add(builder.equal(root.get("orderState"), param.getOrderState()));
				}
				if (StrUtil.isNotBlank(param.getBankCardInfo())) {
					Predicate or = builder.or(
							builder.like(root.get("bankCardAccount"), "%" + param.getBankCardInfo() + "%"),
							builder.like(root.get("accountHolder"), "%" + param.getBankCardInfo() + "%"));
					Predicate and = builder.and(or);
					predicates.add(and);
				}
				if (param.getMinAmount() != null) {
					predicates.add(builder.ge(root.get("amount"), param.getMinAmount()));
				}
				if (param.getMaxAmount() != null) {
					predicates.add(builder.le(root.get("amount"), param.getMaxAmount()));
				}
				if (StrUtil.isNotBlank(param.getReceiverUserName())) {
					predicates.add(builder.like(root.join("receivedAccount", JoinType.INNER).get("userName"),
							"%" + param.getReceiverUserName() + "%"));
				}
				if (param.getCreateTimeStart() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.beginOfDay(param.getCreateTimeStart())));
				}
				if (param.getCreateTimeEnd() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.endOfDay(param.getCreateTimeEnd())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

	@Transactional(readOnly = true)
	public DistributePayoutOrderSummaryVO orderSummary(DistributePayoutOrderQueryCondParam param) {
		Double successAmount = 0d;
		Long successOrderNum = 0l;
		Double memberIncome = 0d;
		Specification<DistributePayoutOrder> spec = buildQueryCond(param);
		List<DistributePayoutOrder> orders = distributePayoutOrderRepo.findAll(spec);
		for (DistributePayoutOrder order : orders) {
			if (!(Constant.下发代付订单状态_支付成功.equals(order.getOrderState()))) {
				continue;
			}
			successAmount += order.getAmount();
			successOrderNum++;
			memberIncome += order.getMemberIncome();
		}
		successAmount = NumberUtil.round(successAmount, 2).doubleValue();
		return DistributePayoutOrderSummaryVO.build(successAmount, successOrderNum, memberIncome);
	}

	@Transactional(readOnly = true)
	public PageResult<DistributePayoutOrderVO> findTop5TodoDistributePayoutOrderByPage() {
		DistributePayoutOrderQueryCondParam param = new DistributePayoutOrderQueryCondParam();
		param.setPageNum(1);
		param.setPageSize(5);
		param.setOrderState(Constant.下发代付订单状态_待确认);

		Specification<DistributePayoutOrder> spec = buildQueryCond(param);
		Page<DistributePayoutOrder> result = distributePayoutOrderRepo.findAll(spec,
				PageRequest.of(0, 5, Sort.by(Sort.Order.desc("receivedTime"))));
		PageResult<DistributePayoutOrderVO> pageResult = new PageResult<>(
				DistributePayoutOrderVO.convertFor(result.getContent()), 1, 5, result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public PageResult<DistributePayoutOrderVO> findDistributePayoutOrderByPage(
			DistributePayoutOrderQueryCondParam param) {
		Specification<DistributePayoutOrder> spec = buildQueryCond(param);
		Page<DistributePayoutOrder> result = distributePayoutOrderRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<DistributePayoutOrderVO> pageResult = new PageResult<>(
				DistributePayoutOrderVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public void orderAutoSettlement() {
		List<DistributePayoutOrder> orders = distributePayoutOrderRepo
				.findByOrderStateAndSettlementTimeIsNull(Constant.下发代付订单状态_支付成功);
		for (DistributePayoutOrder order : orders) {
			redisTemplate.opsForList().leftPush(Constant.下发代付订单_已支付订单单号, order.getOrderNo());
		}
	}

	@Lock(keys = "'distributePayoutOrderSettlement_' + #orderNo")
	@Transactional
	public void distributePayoutOrderSettlement(String orderNo) {
		DistributePayoutOrder order = distributePayoutOrderRepo.findByOrderNo(orderNo);
		if (order == null) {
			throw new BizException("订单不存在");
		}
		if (!Constant.下发代付订单状态_支付成功.equals(order.getOrderState())) {
			return;
		}
		if (order.getSettlementTime() != null) {
			return;
		}

		double memberIncome = 0d;
		MerchantSettlementSetting merchantSettlementSetting = merchantSettlementSettingRepo
				.findTopByOrderByLatelyUpdateTime();
		Double distributePayoutIncomeRate = merchantSettlementSetting.getDistributePayoutIncomeRate();
		if (distributePayoutIncomeRate != null && distributePayoutIncomeRate > 0) {
			memberIncome = NumberUtil.round(order.getAmount() * distributePayoutIncomeRate * 0.01, 2).doubleValue();

			IncomeRecord receiveOrderIncomeRecord = IncomeRecord.build(order.getId(), Constant.收益类型_下发代付收益,
					memberIncome, order.getReceivedAccountId());
			incomeRecordRepo.save(receiveOrderIncomeRecord);
			ThreadPoolUtils.getPayForAnotherPool().schedule(() -> {
				redisTemplate.opsForList().leftPush(Constant.收益记录ID, receiveOrderIncomeRecord.getId());
			}, 1, TimeUnit.SECONDS);
		}
		order.setSettlementTime(new Date());
		order.setMemberIncome(memberIncome);
		distributePayoutOrderRepo.save(order);

		UserAccount userAccount = order.getReceivedAccount();
		double cashDeposit = userAccount.getCashDeposit() + order.getAmount();
		userAccount.setCashDeposit(NumberUtil.round(cashDeposit, 2).doubleValue());
		userAccountRepo.save(userAccount);
		accountChangeLogRepo.save(AccountChangeLog.buildWithDistributePayout(userAccount, order));
	}

	@Transactional(readOnly = true)
	public DistributePayoutOrderVO getOrderInfo(String id, String userAccountId) {
		DistributePayoutOrder order = distributePayoutOrderRepo.findByIdAndReceivedAccountId(id, userAccountId);
		if (order == null) {
			throw new BizException("无权操作");
		}
		return getOrderInfo(id);
	}

	@Transactional(readOnly = true)
	public DistributePayoutOrderVO getOrderInfo(String id) {
		DistributePayoutOrder order = distributePayoutOrderRepo.getOne(id);
		return DistributePayoutOrderVO.convertFor(order);
	}

	@ParamValid
	@Transactional
	public void memberCancelOrder(@NotBlank String id, @NotBlank String note, @NotBlank String userAccountId) {
		DistributePayoutOrder order = distributePayoutOrderRepo.findByIdAndReceivedAccountId(id, userAccountId);
		if (order == null) {
			throw new BizException("无权操作");
		}
		if (!Constant.下发代付订单状态_待支付.equals(order.getOrderState())) {
			throw new BizException("只有待支付的订单才能取消");
		}
		cancelOrder(id, note, userAccountId);
	}

	@ParamValid
	@Transactional
	public void confirmToPaid(@NotBlank String id, @NotBlank String dealAccountId) {
		DistributePayoutOrder order = distributePayoutOrderRepo.getOne(id);
		if (!(Constant.下发代付订单状态_待支付.equals(order.getOrderState())
				|| Constant.下发代付订单状态_待确认.equals(order.getOrderState()))) {
			throw new BizException("只有待支付，待确认的订单才能确认已支付");
		}
		order.paid(dealAccountId);
		distributePayoutOrderRepo.save(order);
		MerchantSettlementRecord merchantSettlementRecord = order.getMerchantSettlementRecord();
		if (merchantSettlementRecord != null && Constant.商户结算状态_下发处理中.equals(merchantSettlementRecord.getState())) {
			merchantSettlementRecord.confirmCredited();
			merchantSettlementRecordRepo.save(merchantSettlementRecord);
		}
		ThreadPoolUtils.getPayForAnotherPool().schedule(() -> {
			redisTemplate.opsForList().leftPush(Constant.下发代付订单_已支付订单单号, order.getOrderNo());
		}, 1, TimeUnit.SECONDS);
	}

	@ParamValid
	@Transactional
	public void cancelOrder(@NotBlank String id, @NotBlank String note, @NotBlank String dealAccountId) {
		DistributePayoutOrder order = distributePayoutOrderRepo.getOne(id);
		if (!(Constant.下发代付订单状态_待接单.equals(order.getOrderState()) || Constant.下发代付订单状态_待支付.equals(order.getOrderState())
				|| Constant.下发代付订单状态_待确认.equals(order.getOrderState()))) {
			throw new BizException("只有待接单,待支付,待确认的订单才能取消");
		}
		order.fail(note, dealAccountId);
		distributePayoutOrderRepo.save(order);
		MerchantSettlementRecord merchantSettlementRecord = order.getMerchantSettlementRecord();
		if (merchantSettlementRecord != null && (Constant.商户结算状态_下发待处理.equals(merchantSettlementRecord.getState())
				|| Constant.商户结算状态_下发处理中.equals(merchantSettlementRecord.getState()))) {
			merchantSettlementRecord.resetToPendingState();
			merchantSettlementRecordRepo.save(merchantSettlementRecord);
		}
	}

	@Lock(keys = "'distributePayoutSubmitPaymentVoucher_' + #param.userAccountId")
	@ParamValid
	@Transactional
	public void submitPaymentVoucher(SubmitPaymentVoucherParam param) {
		DistributePayoutOrder order = distributePayoutOrderRepo.findByIdAndReceivedAccountId(param.getId(),
				param.getUserAccountId());
		if (!Constant.下发代付订单状态_待支付.equals(order.getOrderState())) {
			throw new BizException("只有待支付的订单才能上传打款凭证,");
		}
		if (StrUtil.isNotBlank(order.getPaymentVoucherId())) {
			throw new BizException("已上传打款凭证,不能重复上传");
		}
		order.setPaymentVoucherId(param.getPaymentVoucherId());
		order.setDepositor(param.getDepositor());
		order.setOrderState(Constant.下发代付订单状态_待确认);
		distributePayoutOrderRepo.save(order);
	}

	@Lock(keys = "'distributePayoutReceiveOrder_' + #id")
	@ParamValid
	@Transactional
	public String receiveOrder(@NotBlank String id, @NotBlank String userAccountId) {
		DistributePayoutOrder order = distributePayoutOrderRepo.getOne(id);
		if (!Constant.下发代付订单状态_待接单.equals(order.getOrderState())) {
			throw new BizException("该订单已被接");
		}
		String merchantSettlementRecordId = order.getMerchantSettlementRecordId();
		List<DistributePayoutOrder> cancelOrders = distributePayoutOrderRepo
				.findByMerchantSettlementRecordIdAndReceivedAccountId(merchantSettlementRecordId, userAccountId);
		if (CollectionUtil.isNotEmpty(cancelOrders)) {
			throw new BizException("你已取消过该笔订单,不能再接了");
		}

		order.setReceivedTime(new Date());
		order.setReceivedAccountId(userAccountId);
		order.setOrderState(Constant.下发代付订单状态_待支付);
		distributePayoutOrderRepo.save(order);

		MerchantSettlementRecord merchantSettlementRecord = order.getMerchantSettlementRecord();
		if (merchantSettlementRecord != null) {
			merchantSettlementRecord.memberReceiveOrder(order.getId());
			merchantSettlementRecordRepo.save(merchantSettlementRecord);
		}
		return order.getId();
	}

	@Transactional(readOnly = true)
	public List<WaitReceivingDistributePayoutOrderVO> findWaitReceivingOrder(@NotBlank String userAccountId) {
		List<DistributePayoutOrder> orders = distributePayoutOrderRepo
				.findByOrderStateOrderByCreateTimeAsc(Constant.下发代付订单状态_待接单);
		return WaitReceivingDistributePayoutOrderVO.convertFor(orders);
	}

}
