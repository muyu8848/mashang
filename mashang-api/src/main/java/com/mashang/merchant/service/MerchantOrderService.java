package com.mashang.merchant.service;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
import com.mashang.common.utils.ThreadPoolUtils;
import com.mashang.common.valid.ParamValid;
import com.mashang.common.vo.PageResult;
import com.mashang.constants.Constant;
import com.mashang.gatheringcode.domain.GatheringCode;
import com.mashang.gatheringcode.repo.GatheringCodeRepo;
import com.mashang.income.domain.IncomeRecord;
import com.mashang.income.repo.IncomeRecordRepo;
import com.mashang.mastercontrol.domain.ReceiveOrderRiskSetting;
import com.mashang.mastercontrol.domain.ReceiveOrderSetting;
import com.mashang.mastercontrol.repo.ReceiveOrderRiskSettingRepo;
import com.mashang.mastercontrol.repo.ReceiveOrderSettingRepo;
import com.mashang.mastercontrol.repo.SystemSettingRepo;
import com.mashang.merchant.domain.AccountWaitConfirmOrderNum;
import com.mashang.merchant.domain.ActualIncomeRecord;
import com.mashang.merchant.domain.FreezeRecord;
import com.mashang.merchant.domain.GatheringChannel;
import com.mashang.merchant.domain.GatheringChannelRate;
import com.mashang.merchant.domain.Merchant;
import com.mashang.merchant.domain.MerchantAccountChangeLog;
import com.mashang.merchant.domain.MerchantOrder;
import com.mashang.merchant.domain.QueueRecord;
import com.mashang.merchant.param.ManualStartOrderParam;
import com.mashang.merchant.param.MerchantOrderQueryCondParam;
import com.mashang.merchant.param.MyReceiveOrderRecordQueryCondParam;
import com.mashang.merchant.param.StartOrderParam;
import com.mashang.merchant.repo.AccountWaitConfirmOrderNumRepo;
import com.mashang.merchant.repo.ActualIncomeRecordRepo;
import com.mashang.merchant.repo.FreezeRecordRepo;
import com.mashang.merchant.repo.GatheringChannelRateRepo;
import com.mashang.merchant.repo.GatheringChannelRepo;
import com.mashang.merchant.repo.MerchantAccountChangeLogRepo;
import com.mashang.merchant.repo.MerchantOrderRepo;
import com.mashang.merchant.repo.MerchantRepo;
import com.mashang.merchant.repo.QueueRecordRepo;
import com.mashang.merchant.vo.DispatchOrderTipVO;
import com.mashang.merchant.vo.MerchantOrderDetailsVO;
import com.mashang.merchant.vo.MerchantOrderSummaryVO;
import com.mashang.merchant.vo.MerchantOrderVO;
import com.mashang.merchant.vo.MerchantOrderWithMerchantVO;
import com.mashang.merchant.vo.MyWaitConfirmOrderVO;
import com.mashang.merchant.vo.OrderGatheringCodeVO;
import com.mashang.merchant.vo.OrderInfoVO;
import com.mashang.merchant.vo.RealTimeQueueRecordVO;
import com.mashang.merchant.vo.ReceiveOrderRecordVO;
import com.mashang.merchant.vo.StartOrderIpBlackListVO;
import com.mashang.merchant.vo.StartOrderRealNameBlackListVO;
import com.mashang.merchant.vo.StartOrderSuccessVO;
import com.mashang.useraccount.domain.AccountChangeLog;
import com.mashang.useraccount.domain.AccountReceiveOrderChannel;
import com.mashang.useraccount.domain.UserAccount;
import com.mashang.useraccount.repo.AccountChangeLogRepo;
import com.mashang.useraccount.repo.AccountReceiveOrderChannelRepo;
import com.mashang.useraccount.repo.UserAccountRepo;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

@Validated
@Slf4j
@Service
public class MerchantOrderService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private MerchantOrderRepo merchantOrderRepo;

	@Autowired
	private MerchantRepo merchantRepo;

	@Autowired
	private UserAccountRepo userAccountRepo;

	@Autowired
	private GatheringCodeRepo gatheringCodeRepo;

	@Autowired
	private AccountChangeLogRepo accountChangeLogRepo;

	@Autowired
	private ReceiveOrderSettingRepo receiveOrderSettingRepo;

	@Autowired
	private ReceiveOrderRiskSettingRepo receiveOrderRiskSettingRepo;

	@Autowired
	private AccountReceiveOrderChannelRepo accountReceiveOrderChannelRepo;

	@Autowired
	private GatheringChannelRateRepo gatheringChannelRateRepo;

	@Autowired
	private GatheringChannelRepo gatheringChannelRepo;

	@Autowired
	private ActualIncomeRecordRepo actualIncomeRecordRepo;

	@Autowired
	private FreezeRecordRepo freezeRecordRepo;

	@Autowired
	private QueueRecordRepo queueRecordRepo;

	@Autowired
	private SystemSettingRepo systemSettingRepo;

	@Autowired
	private MerchantAccountChangeLogRepo merchantAccountChangeLogRepo;

	@Autowired
	private AccountWaitConfirmOrderNumRepo accountWaitConfirmOrderNumRepo;

	@Autowired
	private IncomeRecordRepo incomeRecordRepo;

	@Transactional(readOnly = true)
	public List<StartOrderRealNameBlackListVO> findStartOrderRealNameBlackList(String realName) {
		String keyPrefix = "START_ORDER_BLACK_LIST_REAL_NAME_";
		List<StartOrderRealNameBlackListVO> vos = new ArrayList<>();
		Set<String> keys = redisTemplate.keys(keyPrefix + (StrUtil.isNotBlank(realName) ? realName : "") + "*");
		for (String key : keys) {
			String realNameTmp = key.split(keyPrefix)[1];
			String createTime = redisTemplate.opsForValue().get(key);
			vos.add(StartOrderRealNameBlackListVO.convertFor(realNameTmp, createTime));
		}
		return vos;
	}

	@Transactional
	public void delStartOrderRealNameBlackList(String realName) {
		redisTemplate.delete("START_ORDER_BLACK_LIST_REAL_NAME_" + realName);
	}

	@Transactional
	public void addStartOrderRealNameBlackList(String realName) {
		redisTemplate.opsForValue().set("START_ORDER_BLACK_LIST_REAL_NAME_" + realName,
				DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
	}

	@Transactional(readOnly = true)
	public List<StartOrderIpBlackListVO> findStartOrderIpBlackList(String ipAddr) {
		String keyPrefix = "START_ORDER_BLACK_LIST_IP_";
		List<StartOrderIpBlackListVO> vos = new ArrayList<>();
		Set<String> keys = redisTemplate.keys(keyPrefix + (StrUtil.isNotBlank(ipAddr) ? ipAddr : "") + "*");
		for (String key : keys) {
			String ipAddrTmp = key.split(keyPrefix)[1];
			String createTime = redisTemplate.opsForValue().get(key);
			vos.add(StartOrderIpBlackListVO.convertFor(ipAddrTmp, createTime));
		}
		return vos;
	}

	@Transactional
	public void delStartOrderIpBlackList(String ipAddr) {
		redisTemplate.delete("START_ORDER_BLACK_LIST_IP_" + ipAddr);
	}

	@Transactional
	public void addStartOrderIpBlackList(String ipAddr) {
		redisTemplate.opsForValue().set("START_ORDER_BLACK_LIST_IP_" + ipAddr,
				DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
	}

	@Transactional
	public void setPayer(@NotBlank String orderNo, @NotBlank String payerName) {
		MerchantOrder order = merchantOrderRepo.findTopByOrderNoOrderBySubmitTimeDesc(orderNo);
		if (order == null) {
			throw new BizException("商户订单不存在");
		}
		if (StrUtil.isBlank(order.getPayerName()) && Constant.商户订单状态_等待接单.equals(order.getOrderState())) {
			order.setPayerName(payerName);
			merchantOrderRepo.save(order);
			ThreadPoolUtils.getDispatchOrderPool().schedule(() -> {
				redisTemplate.opsForList().leftPush(Constant.派单订单ID, order.getId());
			}, 200, TimeUnit.MILLISECONDS);
		}
	}

	@Transactional(readOnly = true)
	public MerchantOrderSummaryVO merchantOrderSummary(MerchantOrderQueryCondParam param) {
		Double successAmount = 0d;
		Long successOrderNum = 0l;
		Double merchantAgentIncome = 0d;
		Double merchantIncome = 0d;
		Double handlingFee = 0d;
		Double merchantAgentHandlingFee = 0d;
		Double memberIncome = 0d;
		Double memberTeamTotalIncome = 0d;
		Specification<MerchantOrder> spec = buildQueryCond(param);
		List<MerchantOrder> orders = merchantOrderRepo.findAll(spec);
		for (MerchantOrder order : orders) {
			if (!(Constant.商户订单状态_已支付.equals(order.getOrderState())
					|| Constant.商户订单状态_补单.equals(order.getOrderState()))) {
				continue;
			}
			successAmount += order.getGatheringAmount();
			successOrderNum++;
			merchantAgentIncome += order.getHandlingFee() - order.getMerchantAgentHandlingFee();
			merchantIncome += order.getGatheringAmount() - order.getHandlingFee();
			handlingFee += order.getHandlingFee();
			merchantAgentHandlingFee += order.getMerchantAgentHandlingFee();
			memberIncome += order.getMemberIncome();
			memberTeamTotalIncome += order.getMemberTeamTotalIncome();
		}
		successAmount = NumberUtil.round(successAmount, 2).doubleValue();
		merchantAgentIncome = NumberUtil.round(merchantAgentIncome, 2).doubleValue();
		merchantIncome = NumberUtil.round(merchantIncome, 2).doubleValue();
		handlingFee = NumberUtil.round(handlingFee, 2).doubleValue();
		merchantAgentHandlingFee = NumberUtil.round(merchantAgentHandlingFee, 2).doubleValue();
		memberIncome = NumberUtil.round(memberIncome, 2).doubleValue();
		memberTeamTotalIncome = NumberUtil.round(memberTeamTotalIncome, 2).doubleValue();
		return MerchantOrderSummaryVO.build(successAmount, successOrderNum, merchantAgentIncome, merchantIncome,
				handlingFee, merchantAgentHandlingFee, memberIncome, memberTeamTotalIncome);
	}

	@Transactional
	public void memberConfirmToPaidWithUnconfirmedAutoFreeze(@NotBlank String orderId, @NotBlank String accountId,
			String payerBankCardTail) {
		MerchantOrder merchantOrder = merchantOrderRepo.findByIdAndReceivedAccountId(orderId, accountId);
		if (merchantOrder == null) {
			throw new BizException(BizError.订单不存在);
		}
		confirmToPaidWithUnconfirmedAutoFreeze(orderId, accountId, payerBankCardTail);
	}

	@Lock(keys = "'confirmToPaidWithUnconfirmedAutoFreeze_' + #orderId")
	@Transactional
	public void confirmToPaidWithUnconfirmedAutoFreeze(@NotBlank String orderId, @NotBlank String dealAccountId,
			String payerBankCardTail) {
		MerchantOrder merchantOrder = merchantOrderRepo.findById(orderId).orElse(null);
		if (merchantOrder == null) {
			throw new BizException(BizError.订单不存在);
		}
		if (!Constant.商户订单状态_未确认超时取消.equals(merchantOrder.getOrderState())) {
			throw new BizException(BizError.订单状态为未确认超时取消才能转为确认已支付);
		}
		long orderCount = merchantOrderRepo.countByOrderNo(merchantOrder.getOrderNo());
		if (orderCount >= 2) {
			throw new BizException("该订单已进行补单操作,不能再次确认");
		}
		merchantOrder.confirmToPaid(dealAccountId);
		merchantOrder.setPayerBankCardTail(payerBankCardTail);
		merchantOrderRepo.save(merchantOrder);

		FreezeRecord freezeRecord = freezeRecordRepo.findTopByMerchantOrderId(merchantOrder.getId());
		if (freezeRecord.getDealTime() == null) {
			releaseFreezeOrder(freezeRecord.getId());
		}

		UserAccount userAccount = userAccountRepo.getOne(merchantOrder.getReceivedAccountId());
		Double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() - merchantOrder.getGatheringAmount(), 2)
				.doubleValue();
		if (cashDeposit < 0) {
			throw new BizException(BizError.业务异常.getCode(), "余额不足");
		}
		userAccount.setCashDeposit(cashDeposit);
		userAccountRepo.save(userAccount);
		accountChangeLogRepo.save(AccountChangeLog.buildWithReceiveOrderDeduction(userAccount, merchantOrder, false));

		receiveOrderSettlement(merchantOrder);
	}

	@Transactional
	public void customerServiceConfirmToPaid(@NotBlank String orderId, String note, @NotBlank String dealAccountId) {
		MerchantOrder merchantOrder = merchantOrderRepo.findById(orderId).orElse(null);
		if (merchantOrder == null) {
			throw new BizException(BizError.订单不存在);
		}
		if (!Constant.商户订单状态_申诉中.equals(merchantOrder.getOrderState())) {
			throw new BizException(BizError.业务异常.getCode(), "订单状态为申诉中才能转为确认已支付");
		}
		merchantOrder.confirmToPaid(dealAccountId);
		merchantOrder.setNote(note);
		merchantOrderRepo.save(merchantOrder);
		receiveOrderSettlement(merchantOrder);
	}

	@Transactional(readOnly = true)
	public OrderInfoVO getOrderInfo(@NotBlank String merchantOrderNo, @NotBlank String merchantNum,
			@NotBlank String sign) {
		Merchant merchant = merchantRepo.findByUserNameAndDeletedFlagIsFalse(merchantNum);
		if (merchant == null) {
			throw new BizException(BizError.商户未接入);
		}
		String key = merchantNum + merchantOrderNo + merchant.getSecretKey();
		if (!new Digester(DigestAlgorithm.MD5).digestHex(key).equals(sign)) {
			throw new BizException(BizError.签名不正确);
		}

		MerchantOrder merchantOrder = merchantOrderRepo.findTopByMerchantOrderNoOrderBySubmitTimeDesc(merchantOrderNo);
		if (merchantOrder == null) {
			throw new BizException(BizError.订单不存在);
		}
		OrderInfoVO vo = OrderInfoVO.build(merchantOrderNo, merchantOrder.getOrderState(), merchantNum,
				merchantOrder.getGatheringAmount());
		vo.setPayerName(merchantOrder.getPayerName());
		vo.setPayerBankCardTail(merchantOrder.getPayerBankCardTail());
		return vo;
	}

	@Transactional(readOnly = true)
	public List<RealTimeQueueRecordVO> realTimeQueueRecord() {
		List<QueueRecord> queueRecords = queueRecordRepo.findByUsedIsFalseOrderByQueueTime();
		return RealTimeQueueRecordVO.convertFor(queueRecords);
	}

	@Transactional(readOnly = true)
	public Integer getQueueRanking(String userAccountId) {
		List<QueueRecord> queueRecords = queueRecordRepo.findByUsedIsFalseOrderByQueueTime();
		for (int i = 0; i < queueRecords.size(); i++) {
			QueueRecord queueRecord = queueRecords.get(i);
			if (queueRecord.getUserAccountId().equals(userAccountId)) {
				return i + 1;
			}
		}
		return 1;
	}

	@Transactional
	public void memberSupplementOrder(@NotBlank String id,
			@NotNull @DecimalMin(value = "0", inclusive = true) Double amount, @NotBlank String accountId) {
		MerchantOrder merchantOrder = merchantOrderRepo.findByIdAndReceivedAccountId(id, accountId);
		if (merchantOrder == null) {
			throw new BizException(BizError.订单不存在);
		}
		supplementOrder(id, amount, "", true, accountId);
	}

	@Lock(keys = "'supplementOrder_' + #id")
	@Transactional
	public void supplementOrder(@NotBlank String id,
			@NotNull @DecimalMin(value = "0", inclusive = true) Double gatheringAmount, String payerBankCardTail,
			@NotNull Boolean releaseFreezeOrderFlag, @NotBlank String dealAccountId) {
		MerchantOrder invalidOrder = merchantOrderRepo.getOne(id);
		long orderCount = merchantOrderRepo.countByOrderNo(invalidOrder.getOrderNo());
		if (orderCount >= 2) {
			throw new BizException(BizError.该订单已进行补单操作);
		}
		if (!(Constant.商户订单状态_取消订单退款.equals(invalidOrder.getOrderState())
				|| Constant.商户订单状态_未确认超时取消.equals(invalidOrder.getOrderState()))) {
			throw new BizException(BizError.只有未确认超时取消及取消订单退款的订单才能补单);
		}
		if (releaseFreezeOrderFlag) {
			FreezeRecord freezeRecord = freezeRecordRepo.findTopByMerchantOrderId(id);
			releaseFreezeOrder(freezeRecord.getId());
		}
		UserAccount userAccount = userAccountRepo.getOne(invalidOrder.getReceivedAccountId());
		Double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() - gatheringAmount, 2).doubleValue();
		if (cashDeposit < 0) {
			throw new BizException("余额不足,无法补单");
		}

		userAccount.setCashDeposit(cashDeposit);
		userAccountRepo.save(userAccount);
		MerchantOrder merchantOrder = invalidOrder.supplementOrder();
		merchantOrder.setPayerBankCardTail(payerBankCardTail);
		merchantOrder.setGatheringAmount(gatheringAmount);
		merchantOrder.setFloatAmount(0d);
		merchantOrder.confirmToPaid(dealAccountId);
		merchantOrder.setOrderState(Constant.商户订单状态_补单);
		merchantOrderRepo.save(merchantOrder);
		accountChangeLogRepo.save(AccountChangeLog.buildWithReceiveOrderDeduction(userAccount, merchantOrder, true));
		receiveOrderSettlement(merchantOrder);
	}

	@Transactional
	public void dispatchOrderTipMarkRead(@NotBlank String userAccountId) {
		List<QueueRecord> queueRecords = queueRecordRepo.findByUserAccountIdAndUsedTrueAndMarkReadFalse(userAccountId);
		for (QueueRecord queueRecord : queueRecords) {
			queueRecord.setMarkRead(true);
			queueRecordRepo.save(queueRecord);
		}
	}

	@Transactional(readOnly = true)
	public DispatchOrderTipVO dispatchOrderTip(@NotBlank String userAccountId) {
		QueueRecord queueRecord = queueRecordRepo.findTopByUserAccountIdAndUsedIsTrueAndMarkReadIsFalse(userAccountId);
		if (queueRecord == null) {
			return null;
		}
		return DispatchOrderTipVO.build(queueRecord.getId(), queueRecord.getNote());
	}

	@Transactional(readOnly = true)
	public void releaseFreezeOrder() {
		Date now = new Date();
		List<FreezeRecord> freezeRecords = freezeRecordRepo.findByDealTimeIsNullAndUsefulTimeLessThan(now);
		for (FreezeRecord freezeRecord : freezeRecords) {
			redisTemplate.opsForList().leftPush(Constant.冻结记录ID, freezeRecord.getId());
		}
	}

	@Lock(keys = "'releaseFreezeOrder_' + #freezeRecordId")
	@Transactional
	public void releaseFreezeOrder(@NotBlank String freezeRecordId) {
		FreezeRecord freezeRecord = freezeRecordRepo.getOne(freezeRecordId);
		if (freezeRecord.getDealTime() != null) {
			return;
		}
		freezeRecord.setDealTime(new Date());
		freezeRecordRepo.save(freezeRecord);
		MerchantOrder merchantOrder = freezeRecord.getMerchantOrder();
		UserAccount userAccount = merchantOrder.getReceivedAccount();
		Double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() + merchantOrder.getGatheringAmount(), 2)
				.doubleValue();
		double freezeAmount = NumberUtil.round(userAccount.getFreezeAmount() - merchantOrder.getGatheringAmount(), 2)
				.doubleValue();
		userAccount.setFreezeAmount(freezeAmount);
		userAccount.setCashDeposit(cashDeposit);
		userAccountRepo.save(userAccount);
		accountChangeLogRepo.save(AccountChangeLog.buildWithReleaseFreezeAmount(userAccount, merchantOrder));
	}

	@Transactional
	public void freezeOrder() {
		ReceiveOrderSetting receiveOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (!receiveOrderSetting.getFreezeMode()) {
			return;
		}
		DateTime time = DateUtil.offset(new Date(), DateField.MINUTE,
				-receiveOrderSetting.getUnconfirmedAutoFreezeDuration());
		List<MerchantOrder> merchantOrders = merchantOrderRepo
				.findByOrderStateAndReceivedTimeLessThan(Constant.商户订单状态_已接单, time);
		for (MerchantOrder merchantOrder : merchantOrders) {
			merchantOrder.setOrderState(Constant.商户订单状态_未确认超时取消);
			merchantOrderRepo.save(merchantOrder);
			FreezeRecord freezeRecord = FreezeRecord.build(merchantOrder.getReceivedAccountId(), merchantOrder.getId(),
					receiveOrderSetting.getFreezeEffectiveDuration());
			freezeRecordRepo.save(freezeRecord);
			UserAccount receivedAccount = merchantOrder.getReceivedAccount();
			double freezeAmount = NumberUtil
					.round(receivedAccount.getFreezeAmount() + merchantOrder.getGatheringAmount(), 2).doubleValue();
			receivedAccount.setFreezeAmount(freezeAmount);
			userAccountRepo.save(receivedAccount);
		}
	}

	@Transactional(readOnly = true)
	public MerchantOrderDetailsVO findMerchantOrderDetailsById(@NotBlank String orderId) {
		MerchantOrderDetailsVO vo = MerchantOrderDetailsVO.convertFor(merchantOrderRepo.getOne(orderId));
		return vo;
	}

	@Lock(keys = "'cancelOrderRefund_' + #orderId")
	@Transactional
	public void cancelOrderRefund(@NotBlank String orderId, @NotBlank String dealAccountId) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(orderId);
		if (Constant.商户订单状态_取消订单退款.equals(merchantOrder.getOrderState())) {
			return;
		}
		if (!(Constant.商户订单状态_已接单.equals(merchantOrder.getOrderState())
				|| Constant.商户订单状态_申诉中.equals(merchantOrder.getOrderState()))) {
			throw new BizException(BizError.业务异常.getCode(), "只有已接单或申诉中的订单才能进行取消订单退款操作");
		}
		UserAccount userAccount = merchantOrder.getReceivedAccount();
		Double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() + merchantOrder.getGatheringAmount(), 2)
				.doubleValue();
		userAccount.setCashDeposit(cashDeposit);
		userAccountRepo.save(userAccount);
		merchantOrder.cancelOrderRefund(dealAccountId);
		merchantOrderRepo.save(merchantOrder);
		accountChangeLogRepo.save(AccountChangeLog.buildWithCancelOrderRefund(userAccount, merchantOrder));
	}

	@Transactional
	public void playerConfirmToPaid(@NotBlank String orderNo) {
		MerchantOrder order = merchantOrderRepo.findTopByOrderNoOrderBySubmitTimeDesc(orderNo);
		if (order == null) {
			return;
		}
		if (Constant.商户订单状态_已接单.equals(order.getOrderState())) {
			updateNoteInner(order.getId(), orderNo.substring(orderNo.length() - 4));
		}
	}

	@Transactional(readOnly = true)
	public OrderGatheringCodeVO getOrderGatheringCode(@NotBlank String orderNo) {
		MerchantOrder order = merchantOrderRepo.findTopByOrderNoOrderBySubmitTimeDesc(orderNo);
		if (order == null) {
			log.error("订单不存在;orderNo:{}", orderNo);
			throw new BizException(BizError.订单不存在);
		}
		OrderGatheringCodeVO vo = OrderGatheringCodeVO.convertFor(order);
		return vo;
	}

	@Transactional
	public void userConfirmToPaid(@NotBlank String userAccountId, @NotBlank String orderId, String payerBankCardTail) {
		MerchantOrder merchantOrder = merchantOrderRepo.findByIdAndReceivedAccountId(orderId, userAccountId);
		if (merchantOrder == null) {
			throw new BizException(BizError.订单不存在);
		}
		if (!Constant.商户订单状态_已接单.equals(merchantOrder.getOrderState())) {
			throw new BizException(BizError.订单状态为已接单才能转为确认已支付);
		}
		merchantOrder.confirmToPaid(userAccountId);
		merchantOrder.setPayerBankCardTail(payerBankCardTail);
		merchantOrderRepo.save(merchantOrder);
		receiveOrderSettlement(merchantOrder);
	}

	@Transactional
	public void receiveOrderSettlement(MerchantOrder merchantOrder) {
		generateIncome(merchantOrder, merchantOrder.getReceivedAccount());
		generateActualIncomeRecord(merchantOrder);
		ThreadPoolUtils.getPaidMerchantOrderPool().schedule(() -> {
			redisTemplate.opsForList().leftPush(Constant.商户订单ID, merchantOrder.getId());
		}, 1, TimeUnit.SECONDS);
	}

	public void generateActualIncomeRecord(MerchantOrder merchantOrder) {
		GatheringChannelRate merchantChannel = gatheringChannelRateRepo
				.findByMerchantIdAndChannelId(merchantOrder.getMerchantId(), merchantOrder.getGatheringChannelId());
		double rate = merchantChannel.getRate();
		double merchantAgentRate = rate;
		double handlingFee = NumberUtil.round(merchantOrder.getGatheringAmount() * merchantChannel.getRate() * 0.01, 2)
				.doubleValue();
		double merchantAgentHandlingFee = handlingFee;
		double merchantIncome = NumberUtil
				.round(merchantOrder.getGatheringAmount() * (100 - merchantChannel.getRate()) * 0.01, 2).doubleValue();
		ActualIncomeRecord merchantIncomeRecord = ActualIncomeRecord.build(merchantIncome, merchantOrder.getId(),
				merchantOrder.getMerchantId());
		actualIncomeRecordRepo.save(merchantIncomeRecord);

		Merchant merchant = merchantOrder.getMerchant();
		Merchant superior = merchant.getInviter();
		while (superior != null) {
			GatheringChannelRate merchantRate = gatheringChannelRateRepo.findByMerchantIdAndChannelId(merchant.getId(),
					merchantOrder.getGatheringChannelId());
			GatheringChannelRate superiorRate = gatheringChannelRateRepo.findByMerchantIdAndChannelId(superior.getId(),
					merchantOrder.getGatheringChannelId());
			if (superiorRate == null) {
				log.error("上级账号没有开通该代收通道,无法获得收益;下级账号id:{},上级账号id:{},接单通道:{}", merchant.getId(), superior.getId(),
						merchantOrder.getGatheringChannel().getChannelCode());
				break;
			}
			double spread = NumberUtil.round(merchantRate.getRate() - superiorRate.getRate(), 2).doubleValue();
			if (spread < 0) {
				log.error("订单费率异常,下级账号的费率不能小于上级账号;下级账号id:{},上级账号id:{}", merchant.getId(), superior.getId());
				break;
			}
			merchantAgentRate = superiorRate.getRate();
			merchantAgentHandlingFee = NumberUtil
					.round(merchantOrder.getGatheringAmount() * superiorRate.getRate() * 0.01, 2).doubleValue();
			double merchantAgentIncome = NumberUtil.round(merchantOrder.getGatheringAmount() * spread * 0.01, 2)
					.doubleValue();
			ActualIncomeRecord merchantAgentIncomeRecord = ActualIncomeRecord.build(merchantAgentIncome,
					merchantOrder.getId(), superior.getId());
			actualIncomeRecordRepo.save(merchantAgentIncomeRecord);

			merchant = superior;
			superior = superior.getInviter();
		}
		merchantOrder.setRate(rate);
		merchantOrder.setMerchantAgentRate(merchantAgentRate);
		merchantOrder.setHandlingFee(handlingFee);
		merchantOrder.setMerchantAgentHandlingFee(merchantAgentHandlingFee);
		merchantOrderRepo.save(merchantOrder);
	}

	public void generateIncome(MerchantOrder order, UserAccount receivedAccount) {
		double memberIncome = NumberUtil.round(order.getGatheringAmount() * order.getRebate() * 0.01, 2).doubleValue();
		double memberTeamTotalIncome = memberIncome;
		IncomeRecord receiveOrderIncomeRecord = IncomeRecord.build(order.getId(), Constant.收益类型_代收收益, memberIncome,
				receivedAccount.getId());
		incomeRecordRepo.save(receiveOrderIncomeRecord);

		UserAccount userAccount = receivedAccount;
		UserAccount superior = receivedAccount.getInviter();
		while (superior != null) {
			// 管理员账号没有返点
			if (Constant.账号类型_管理员.equals(superior.getAccountType())) {
				break;
			}
			AccountReceiveOrderChannel userAccountRebate = accountReceiveOrderChannelRepo
					.findByUserAccountIdAndChannelId(userAccount.getId(), order.getGatheringChannelId());
			AccountReceiveOrderChannel superiorRebate = accountReceiveOrderChannelRepo
					.findByUserAccountIdAndChannelId(superior.getId(), order.getGatheringChannelId());
			if (superiorRebate == null) {
				log.error("上级账号没有开通该接单通道,无法获得返点;下级账号id:{},上级账号id:{},接单通道:{}", userAccount.getId(), superior.getId(),
						order.getGatheringChannel().getChannelCode());
				break;
			}
			double rebate = NumberUtil.round(superiorRebate.getRebate() - userAccountRebate.getRebate(), 4)
					.doubleValue();
			if (rebate < 0) {
				log.error("订单返点异常,下级账号的返点不能大于上级账号;下级账号id:{},上级账号id:{}", userAccount.getId(), superior.getId());
				break;
			}
			memberTeamTotalIncome = NumberUtil.round(order.getGatheringAmount() * superiorRebate.getRebate() * 0.01, 4)
					.doubleValue();
			double teamIncome = NumberUtil.round(order.getGatheringAmount() * rebate * 0.01, 4).doubleValue();
			IncomeRecord teamIncomeRecord = IncomeRecord.build(order.getId(), Constant.收益类型_代收团队收益, teamIncome,
					superior.getId());
			incomeRecordRepo.save(teamIncomeRecord);

			userAccount = superior;
			superior = superior.getInviter();
		}
		order.setMemberIncome(memberIncome);
		order.setMemberTeamTotalIncome(memberTeamTotalIncome);
		merchantOrderRepo.save(order);
	}

	@Transactional(readOnly = true)
	public void actualIncomeRecordAutoSettlement() {
		List<ActualIncomeRecord> actualIncomeRecords = actualIncomeRecordRepo
				.findBySettlementTimeIsNullAndAvailableFlagTrue();
		for (ActualIncomeRecord actualIncomeRecord : actualIncomeRecords) {
			redisTemplate.opsForList().leftPush(Constant.实收金额记录ID, actualIncomeRecord.getId());
		}
	}

	@Transactional(readOnly = true)
	public void noticeActualIncomeRecordSettlement(@NotBlank String orderId) {
		List<ActualIncomeRecord> records = actualIncomeRecordRepo.findByMerchantOrderIdAndAvailableFlagTrue(orderId);
		for (ActualIncomeRecord record : records) {
			redisTemplate.opsForList().leftPush(Constant.实收金额记录ID, record.getId());
		}
	}

	@Transactional
	public void actualIncomeRecordSettlement(@NotBlank String actualIncomeRecordId) {
		ActualIncomeRecord actualIncomeRecord = actualIncomeRecordRepo.getOne(actualIncomeRecordId);
		if (actualIncomeRecord.getSettlementTime() != null) {
			log.warn("当前的实收金额记录已结算,无法重复结算;id:{}", actualIncomeRecordId);
			return;
		}
		if (!actualIncomeRecord.getAvailableFlag()) {
			return;
		}
		actualIncomeRecord.setSettlementTime(new Date());
		actualIncomeRecordRepo.save(actualIncomeRecord);
		Merchant merchant = merchantRepo.getOne(actualIncomeRecord.getMerchantId());
		double withdrawableAmount = merchant.getWithdrawableAmount() + actualIncomeRecord.getActualIncome();
		merchant.setWithdrawableAmount(NumberUtil.round(withdrawableAmount, 2).doubleValue());
		merchantRepo.save(merchant);
		merchantAccountChangeLogRepo.save(MerchantAccountChangeLog.buildWithPaidOrderActualIncome(merchant.getId(),
				actualIncomeRecord.getActualIncome(), merchant.getWithdrawableAmount(),
				actualIncomeRecord.getMerchantOrderId()));
	}

	@Transactional(readOnly = true)
	public List<MyWaitConfirmOrderVO> findMyWaitConfirmOrder(@NotBlank String userAccountId) {
		ReceiveOrderSetting receiveOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		Integer unconfirmedAutoFreezeDuration = receiveOrderSetting.getUnconfirmedAutoFreezeDuration();
		return MyWaitConfirmOrderVO
				.convertFor(merchantOrderRepo.findByOrderStateInAndReceivedAccountIdOrderBySubmitTimeDesc(
						Arrays.asList(Constant.商户订单状态_已接单), userAccountId), unconfirmedAutoFreezeDuration);
	}

	@Transactional(readOnly = true)
	public MyWaitConfirmOrderVO findMyWaitConfirmOrderById(@NotBlank String id) {
		ReceiveOrderSetting receiveOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		Integer unconfirmedAutoFreezeDuration = receiveOrderSetting.getUnconfirmedAutoFreezeDuration();
		return MyWaitConfirmOrderVO.convertFor(merchantOrderRepo.getOne(id), unconfirmedAutoFreezeDuration);
	}

	public Specification<MerchantOrder> buildQueryCond(MerchantOrderQueryCondParam param) {
		Specification<MerchantOrder> spec = new Specification<MerchantOrder>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMerchantId())) {
					predicates.add(builder.equal(root.get("merchantId"), param.getMerchantId()));
				}
				if (StrUtil.isNotBlank(param.getHigherLevelMerchantId())) {
					predicates.add(builder.equal(root.join("merchant", JoinType.INNER).get("inviterId"),
							param.getHigherLevelMerchantId()));
				}
				if (StrUtil.isNotBlank(param.getMerchantNum())) {
					predicates.add(builder.equal(root.join("merchant", JoinType.INNER).get("userName"),
							param.getMerchantNum()));
				}
				if (StrUtil.isNotBlank(param.getMerchantOrderNo())) {
					predicates.add(builder.equal(root.get("merchantOrderNo"), param.getMerchantOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getChannelId())) {
					predicates.add(builder.equal(root.get("gatheringChannelId"), param.getChannelId()));
				}
				if (param.getMinAmount() != null) {
					predicates.add(builder.ge(root.get("gatheringAmount"), param.getMinAmount()));
				}
				if (param.getMaxAmount() != null) {
					predicates.add(builder.le(root.get("gatheringAmount"), param.getMaxAmount()));
				}
				if (StrUtil.isNotBlank(param.getOrderState())) {
					predicates.add(builder.equal(root.get("orderState"), param.getOrderState()));
				}
				if (StrUtil.isNotBlank(param.getReceiverUserName())) {
					predicates.add(builder.like(root.join("receivedAccount", JoinType.INNER).get("userName"),
							"%" + param.getReceiverUserName() + "%"));
				}
				if (param.getSubmitStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.beginOfDay(param.getSubmitStartTime())));
				}
				if (param.getSubmitEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.endOfDay(param.getSubmitEndTime())));
				}
				if (param.getReceiveOrderStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("receivedTime").as(Date.class),
							DateUtil.beginOfDay(param.getReceiveOrderStartTime())));
				}
				if (param.getReceiveOrderEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("receivedTime").as(Date.class),
							DateUtil.endOfDay(param.getReceiveOrderEndTime())));
				}
				if (StrUtil.isNotBlank(param.getPayNoticeState())) {
					predicates.add(builder.equal(root.get("noticeState"), param.getPayNoticeState()));
				}
				if (StrUtil.isNotBlank(param.getGatheringCodeDetailInfo())) {
					Predicate or = builder.or(
							builder.like(root.join("gatheringCode", JoinType.INNER).get("payee"),
									"%" + param.getGatheringCodeDetailInfo() + "%"),
							builder.like(root.join("gatheringCode", JoinType.INNER).get("realName"),
									"%" + param.getGatheringCodeDetailInfo() + "%"),
							builder.like(root.join("gatheringCode", JoinType.INNER).get("account"),
									"%" + param.getGatheringCodeDetailInfo() + "%"));
					Predicate and = builder.and(or);
					predicates.add(and);
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

	@Transactional(readOnly = true)
	public List<MerchantOrderVO> findMerchantOrder(MerchantOrderQueryCondParam param) {
		Specification<MerchantOrder> spec = buildQueryCond(param);
		List<MerchantOrder> result = merchantOrderRepo.findAll(spec, Sort.by(Sort.Order.desc("submitTime")));
		String homePageUrl = systemSettingRepo.findTopByOrderByLatelyUpdateTime().getHomePageUrl();
		return MerchantOrderVO.convertFor(result, homePageUrl);
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantOrderVO> findMerchantOrderByPage(MerchantOrderQueryCondParam param) {
		Specification<MerchantOrder> spec = buildQueryCond(param);
		Page<MerchantOrder> result = merchantOrderRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("submitTime"))));
		String homePageUrl = systemSettingRepo.findTopByOrderByLatelyUpdateTime().getHomePageUrl();
		PageResult<MerchantOrderVO> pageResult = new PageResult<>(
				MerchantOrderVO.convertFor(result.getContent(), homePageUrl), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public List<MerchantOrderWithMerchantVO> findMerchantOrderWithMerchant(MerchantOrderQueryCondParam param) {
		Specification<MerchantOrder> spec = buildQueryCond(param);
		List<MerchantOrder> result = merchantOrderRepo.findAll(spec, Sort.by(Sort.Order.desc("submitTime")));
		String homePageUrl = systemSettingRepo.findTopByOrderByLatelyUpdateTime().getHomePageUrl();
		return MerchantOrderWithMerchantVO.convertFor(result, homePageUrl);
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantOrderWithMerchantVO> findMerchantOrderByPageWithMerchant(
			MerchantOrderQueryCondParam param) {
		Specification<MerchantOrder> spec = buildQueryCond(param);
		Page<MerchantOrder> result = merchantOrderRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("submitTime"))));
		String homePageUrl = systemSettingRepo.findTopByOrderByLatelyUpdateTime().getHomePageUrl();
		PageResult<MerchantOrderWithMerchantVO> pageResult = new PageResult<>(
				MerchantOrderWithMerchantVO.convertFor(result.getContent(), homePageUrl), param.getPageNum(),
				param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public PageResult<ReceiveOrderRecordVO> findMyReceiveOrderRecordByPage(MyReceiveOrderRecordQueryCondParam param) {
		Specification<MerchantOrder> spec = new Specification<MerchantOrder>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getReceivedAccountId())) {
					predicates.add(builder.equal(root.get("receivedAccountId"), param.getReceivedAccountId()));
				}
				if (StrUtil.isNotBlank(param.getMerchantOrderNo())) {
					predicates.add(builder.equal(root.get("merchantOrderNo"), param.getMerchantOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getPayState())) {
					if ("paid".equals(param.getPayState())) {
						predicates.add(root.get("orderState").in(Constant.商户订单已支付状态集合));
					} else if ("unPaid".equals(param.getPayState())) {
						predicates.add(root.get("orderState").in(Constant.商户订单未支付状态集合));
					}
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<MerchantOrder> result = merchantOrderRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("receivedTime"))));
		PageResult<ReceiveOrderRecordVO> pageResult = new PageResult<>(
				ReceiveOrderRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void cancelOrderWithMerchant(@NotBlank String id, @NotBlank String merchantId) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(id);
		if (!merchantOrder.getMerchantId().equals(merchantId)) {
			throw new BizException(BizError.无权取消订单);
		}
		cancelOrder(id);
	}

	/**
	 * 取消订单
	 * 
	 * @param id
	 */
	@Transactional
	public void cancelOrder(@NotBlank String id) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(id);
		if (!Constant.商户订单状态_等待接单.equals(merchantOrder.getOrderState())) {
			throw new BizException(BizError.只有等待接单状态的商户订单才能取消);
		}
		merchantOrder.setOrderState(Constant.商户订单状态_人工取消);
		merchantOrder.setDealTime(new Date());
		merchantOrderRepo.save(merchantOrder);
	}

	@Transactional
	public void orderTimeoutDeal() {
		Date now = new Date();
		List<MerchantOrder> orders = merchantOrderRepo.findByOrderStateAndUsefulTimeLessThan(Constant.商户订单状态_等待接单, now);
		for (MerchantOrder order : orders) {
			order.setDealTime(now);
			order.setOrderState(Constant.商户订单状态_超时取消);
			merchantOrderRepo.save(order);
		}
	}

	public void noticeDispatchOrder() {
		Map<String, String> orderIdMap = new HashMap<>();
		List<String> orderIds = redisTemplate.opsForList().range(Constant.派单订单ID, 0, 50);
		for (String orderId : orderIds) {
			orderIdMap.put(orderId, orderId);
		}
		List<MerchantOrder> merchantOrders = merchantOrderRepo.findByOrderState(Constant.商户订单状态_等待接单);
		for (MerchantOrder merchantOrder : merchantOrders) {
			if (orderIdMap.get(merchantOrder.getId()) != null) {
				continue;
			}
			redisTemplate.opsForList().leftPush(Constant.派单订单ID, merchantOrder.getId());
		}
	}

	@Lock(keys = "'dispatchOrder_' + #orderId")
	@Transactional
	public void dispatchOrder(@NotBlank String orderId) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(orderId);
		if (!Constant.商户订单状态_等待接单.equals(merchantOrder.getOrderState())) {
			return;
		}
		ReceiveOrderSetting receiveOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		ReceiveOrderRiskSetting receiveOrderRisk = receiveOrderRiskSettingRepo.findTopByOrderByLatelyUpdateTime();
		String channelCode = merchantOrder.getGatheringChannel().getChannelCode();
		String payerName = merchantOrder.getPayerName();
		if (Constant.收款通道_银行卡.equals(channelCode) || Constant.收款通道_支付宝转卡.equals(channelCode)
				|| Constant.收款通道_微信转卡.equals(channelCode) || Constant.收款通道_财付通转卡.equals(channelCode)) {
			if (StrUtil.isBlank(payerName)) {
				return;
			}
			String blackListIp = redisTemplate.opsForValue().get("START_ORDER_BLACK_LIST_REAL_NAME_" + payerName);
			if (StrUtil.isNotBlank(blackListIp)) {
				return;
			}
			List<MerchantOrder> sameRealNameOrders = merchantOrderRepo.findByPayerNameAndOrderState(payerName,
					Constant.商户订单状态_已接单);
			if (CollectionUtil.isNotEmpty(sameRealNameOrders)
					&& sameRealNameOrders.size() >= receiveOrderRisk.getSameRealNameOrderNum()) {
				return;
			}
		}
		long currentTimeMillis = System.currentTimeMillis();
		List<String> queueAccountIds = new ArrayList<>();
		Set<String> keys = redisTemplate.keys("DISPATCH_ORDER_ACCOUNT_*");
		for (String key : keys) {
			queueAccountIds.add(key.split("DISPATCH_ORDER_ACCOUNT_")[1]);
		}
		if (CollectionUtil.isEmpty(queueAccountIds)) {
			queueAccountIds.add("NON_EXISTENT_ID");
		}
		List<QueueRecord> queueRecords = queueRecordRepo
				.findByUserAccountIdNotInAndUsedIsFalseOrderByQueueTime(queueAccountIds);
		for (Iterator<QueueRecord> iterator = queueRecords.iterator(); iterator.hasNext();) {
			QueueRecord queueRecord = iterator.next();
			if (receiveOrderSetting.getCashDepositMinimumRequire() != null) {
				if (queueRecord.getUserAccount().getCashDeposit() < receiveOrderSetting
						.getCashDepositMinimumRequire()) {
					iterator.remove();
					continue;
				}
			}
			Double cashDeposit = NumberUtil
					.round(queueRecord.getUserAccount().getCashDeposit() - merchantOrder.getGatheringAmount(), 2)
					.doubleValue();
			if (cashDeposit - receiveOrderSetting.getCashPledge() < 0) {
				iterator.remove();
			}
		}
		queueAccountIds = getQueueAccountId(queueRecords);
		if (CollectionUtil.isEmpty(queueAccountIds)) {
			return;
		}
		List<AccountWaitConfirmOrderNum> accountWaitConfirmOrderNums = accountWaitConfirmOrderNumRepo
				.findByIdInAndWaitConfirmOrderNumIsGreaterThanEqual(queueAccountIds,
						receiveOrderRisk.getWaitConfirmOrderUpperLimit());
		if (CollectionUtil.isNotEmpty(accountWaitConfirmOrderNums)) {
			for (Iterator<QueueRecord> iterator = queueRecords.iterator(); iterator.hasNext();) {
				QueueRecord queueRecord = iterator.next();
				for (AccountWaitConfirmOrderNum accountWaitConfirmOrderNum : accountWaitConfirmOrderNums) {
					if (queueRecord.getUserAccountId().equals(accountWaitConfirmOrderNum.getId())) {
						iterator.remove();
						break;
					}
				}
			}
		}
		queueAccountIds = getQueueAccountId(queueRecords);
		if (CollectionUtil.isEmpty(queueAccountIds)) {
			return;
		}
		// 获取配了通道的账号
		List<AccountReceiveOrderChannel> receiveOrderChannels = accountReceiveOrderChannelRepo
				.findByUserAccountIdInAndChannelId(queueAccountIds, merchantOrder.getGatheringChannelId());
		queueAccountIds = new ArrayList<>();
		for (AccountReceiveOrderChannel receiveOrderChannel : receiveOrderChannels) {
			queueAccountIds.add(receiveOrderChannel.getUserAccountId());
		}
		if (CollectionUtil.isEmpty(queueAccountIds)) {
			return;
		}
		Date now = new Date();
		List<GatheringCode> gatheringCodes = gatheringCodeRepo
				.findByUserAccountIdInAndGatheringChannelIdAndStateAndInUseTrueAndDeletedFlagFalse(queueAccountIds,
						merchantOrder.getGatheringChannelId(), Constant.收款码状态_正常);
		if (CollectionUtil.isEmpty(gatheringCodes)) {
			return;
		}
		List<GatheringCode> tmpGatheringCodes = new ArrayList<>();
		if (CollectionUtil.isEmpty(gatheringCodes)) {
			return;
		}
		tmpGatheringCodes = new ArrayList<>();
		for (GatheringCode gatheringCode : gatheringCodes) {
			if (gatheringCode.getMinAmount() == null) {
				tmpGatheringCodes.add(gatheringCode);
				continue;
			}
			if (merchantOrder.getGatheringAmount() >= gatheringCode.getMinAmount()
					&& merchantOrder.getGatheringAmount() <= gatheringCode.getMaxAmount()) {
				tmpGatheringCodes.add(gatheringCode);
			}
		}
		gatheringCodes = tmpGatheringCodes;
		if (CollectionUtil.isEmpty(gatheringCodes)) {
			return;
		}
		tmpGatheringCodes = new ArrayList<>();
		for (GatheringCode gatheringCode : gatheringCodes) {
			boolean hitFlag = true;
			if (gatheringCode.getUsage()
					.getTodayPaidOrderNum() >= gatheringCode.getEverydayTradeCount()) {
				hitFlag = false;
			}
			if (gatheringCode.getUsage()
					.getTodayTradeAmount() >= gatheringCode.getEverydayTradeAmount()) {
				hitFlag = false;
			}
			if (hitFlag) {
				tmpGatheringCodes.add(gatheringCode);
			}
		}
		gatheringCodes = tmpGatheringCodes;
		if (CollectionUtil.isEmpty(gatheringCodes)) {
			return;
		}
		tmpGatheringCodes = new ArrayList<>();
		// 同一收款方式禁止接重复金额的订单
		if (receiveOrderRisk.getBanReceiveRepeatOrder()) {
			List<MerchantOrder> sameAmountOrders = merchantOrderRepo
					.findByOrderStateAndReceivedAccountIdInAndGatheringAmountOrderBySubmitTimeDesc(Constant.商户订单状态_已接单,
							queueAccountIds, merchantOrder.getGatheringAmount());
			if (CollectionUtil.isNotEmpty(sameAmountOrders)) {
				for (GatheringCode gatheringCode : gatheringCodes) {
					boolean repeatFlag = false;
					for (MerchantOrder sameAmountOrder : sameAmountOrders) {
						if (sameAmountOrder.getGatheringCodeId().equals(gatheringCode.getId())) {
							repeatFlag = true;
							break;
						}
					}
					if (!repeatFlag) {
						tmpGatheringCodes.add(gatheringCode);
					}
				}
				gatheringCodes = tmpGatheringCodes;
			}
		}
		if (CollectionUtil.isEmpty(gatheringCodes)) {
			return;
		}
		Map<String, List<GatheringCode>> accountGatheringCodeMap = new HashMap<>();
		for (GatheringCode gatheringCode : gatheringCodes) {
			if (accountGatheringCodeMap.get(gatheringCode.getUserAccountId()) == null) {
				accountGatheringCodeMap.put(gatheringCode.getUserAccountId(), new ArrayList<>());
			}
			accountGatheringCodeMap.get(gatheringCode.getUserAccountId()).add(gatheringCode);
		}

		for (QueueRecord queueRecord : queueRecords) {
			List<GatheringCode> userAccountGatheringCodes = accountGatheringCodeMap.get(queueRecord.getUserAccountId());
			if (CollectionUtil.isEmpty(userAccountGatheringCodes)) {
				continue;
			}
			Collections.shuffle(userAccountGatheringCodes);
			GatheringCode gatheringCode = userAccountGatheringCodes.get(0);
			redisTemplate.opsForValue().set("DISPATCH_ORDER_ACCOUNT_" + queueRecord.getUserAccountId(),
					queueRecord.getUserAccountId(), 4400L, TimeUnit.MILLISECONDS);

			UserAccount userAccount = queueRecord.getUserAccount();
			Double cashDeposit = NumberUtil.round(userAccount.getCashDeposit() - merchantOrder.getGatheringAmount(), 2)
					.doubleValue();
			if (cashDeposit < 0) {
				continue;
			}
			AccountReceiveOrderChannel receiveOrderChannel = accountReceiveOrderChannelRepo
					.findByUserAccountIdAndChannelId(userAccount.getId(), merchantOrder.getGatheringChannelId());
			List<MyWaitConfirmOrderVO> waitConfirmOrders = findMyWaitConfirmOrder(userAccount.getId());
			// 浮动金额
			Double floatAmount = 0d;
			if (receiveOrderRisk.getFloatAmountMode()) {
				List<MyWaitConfirmOrderVO> sameAmountOrders = new ArrayList<>();
				for (MyWaitConfirmOrderVO waitConfirmOrder : waitConfirmOrders) {
					if (waitConfirmOrder.getGatheringCodeId().equals(gatheringCode.getId()) && waitConfirmOrder
							.getGatheringAmount().compareTo(merchantOrder.getGatheringAmount()) == 0) {
						sameAmountOrders.add(waitConfirmOrder);
					}
				}
				if (CollectionUtil.isNotEmpty(sameAmountOrders)) {
					int retryCount = 0;
					boolean retryFlag = true;
					while (retryFlag) {
						boolean sameFloatAmountFlag = false;
						floatAmount = (new Random().nextInt(
								receiveOrderRisk.getMaxFloatAmount() + 1 - receiveOrderRisk.getMinFloatAmount())
								+ receiveOrderRisk.getMinFloatAmount()) * 0.01;
						if ("down".equals(receiveOrderRisk.getFloatAmountDirection())) {
							floatAmount = -floatAmount;
						}
						for (MyWaitConfirmOrderVO sameAmountOrder : sameAmountOrders) {
							if (sameAmountOrder.getFloatAmount() != null
									&& sameAmountOrder.getFloatAmount().compareTo(floatAmount) == 0) {
								sameFloatAmountFlag = true;
								break;
							}
						}
						retryCount++;
						if (retryCount >= 10) {
							retryFlag = false;
						}
						if (!sameFloatAmountFlag) {
							retryFlag = false;
						}
					}
				}
			}

			userAccount.setCashDeposit(cashDeposit);
			userAccountRepo.save(userAccount);
			Integer orderEffectiveDuration = receiveOrderSetting.getOrderPayEffectiveDuration();
			merchantOrder.setFloatAmount(floatAmount);
			merchantOrder.updateReceived(userAccount.getId(), gatheringCode.getId(), gatheringCode.getStorageId(),
					receiveOrderChannel.getRebate());
			merchantOrder.updateUsefulTime(
					DateUtil.offset(merchantOrder.getReceivedTime(), DateField.MINUTE, orderEffectiveDuration));
			merchantOrderRepo.save(merchantOrder);
			accountChangeLogRepo
					.save(AccountChangeLog.buildWithReceiveOrderDeduction(userAccount, merchantOrder, false));

			redisTemplate.delete("DISPATCH_ORDER_ACCOUNT_" + userAccount.getId());
			queueRecord.used("接单成功");
			queueRecordRepo.save(queueRecord);
			if (!Constant.接单状态_正在接单.equals(userAccount.getReceiveOrderState())) {
				break;
			}
			QueueRecord newQueueRecord = QueueRecord.build(userAccount);
			queueRecordRepo.save(newQueueRecord);
			System.out.println("派单耗时：" + (System.currentTimeMillis() - currentTimeMillis));
			break;
		}
	}

	public List<String> getQueueAccountId(List<QueueRecord> queueRecords) {
		List<String> queueAccountIds = new ArrayList<>();
		for (QueueRecord queueRecord : queueRecords) {
			queueAccountIds.add(queueRecord.getUserAccountId());
		}
		return queueAccountIds;
	}

	@Transactional
	public void manualStartOrder(@NotEmpty List<ManualStartOrderParam> params) {
		List<String> ids = new ArrayList<>();
		for (ManualStartOrderParam param : params) {
			Merchant merchant = merchantRepo.findByUserNameAndDeletedFlagIsFalse(param.getMerchantNum());
			if (merchant == null) {
				throw new BizException(BizError.商户未接入);
			}
			String amount = new DecimalFormat("###################.###########").format(param.getGatheringAmount());
			StartOrderParam startOrderParam = new StartOrderParam();
			startOrderParam.setMerchantNum(param.getMerchantNum());
			startOrderParam.setOrderNo(param.getOrderNo());
			startOrderParam.setPayType(param.getGatheringChannelCode());
			startOrderParam.setAmount(amount);
			startOrderParam.setNotifyUrl(param.getNotifyUrl());
			startOrderParam.setReturnUrl(param.getReturnUrl());
			startOrderParam.setAttch(param.getAttch());
			String sign = startOrderParam.getMerchantNum() + startOrderParam.getOrderNo() + amount
					+ param.getNotifyUrl() + merchant.getSecretKey();
			sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
			startOrderParam.setSign(sign);
			ids.add(startOrderInner(startOrderParam).getId());
		}
		ThreadPoolUtils.getDispatchOrderPool().schedule(() -> {
			for (String id : ids) {
				redisTemplate.opsForList().leftPush(Constant.派单订单ID, id);
			}
		}, 300, TimeUnit.MILLISECONDS);
	}

	@ParamValid
	@Transactional
	public StartOrderSuccessVO startOrderWithApi(StartOrderParam param) {
		StartOrderSuccessVO vo = startOrderInner(param);
		ThreadPoolUtils.getDispatchOrderPool().schedule(() -> {
			redisTemplate.opsForList().leftPush(Constant.派单订单ID, vo.getId());
		}, 200, TimeUnit.MILLISECONDS);
		return vo;
	}

	@ParamValid
	@Transactional
	public StartOrderSuccessVO startOrderWithMerchant(StartOrderParam param) {
		if (StrUtil.isNotBlank(param.getIp())) {
		}
		StartOrderSuccessVO vo = startOrderInner(param);
		ThreadPoolUtils.getDispatchOrderPool().schedule(() -> {
			redisTemplate.opsForList().leftPush(Constant.派单订单ID, vo.getId());
		}, 200, TimeUnit.MILLISECONDS);
		return vo;
	}

	@ParamValid
	@Transactional
	public StartOrderSuccessVO startOrderInner(StartOrderParam param) {
		ReceiveOrderSetting receiveOrderSetting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (receiveOrderSetting.getStopStartAndReceiveOrder()) {
			throw new BizException(BizError.系统维护中不能发起订单);
		}
		if (StrUtil.isNotBlank(param.getIp())) {
			String blackListIp = redisTemplate.opsForValue().get("START_ORDER_BLACK_LIST_IP_" + param.getIp());
			if (StrUtil.isNotBlank(blackListIp)) {
				throw new BizException("该ip禁止下单，请联系管理员");
			}
			ReceiveOrderRiskSetting receiveOrderRisk = receiveOrderRiskSettingRepo.findTopByOrderByLatelyUpdateTime();
			List<MerchantOrder> pendingOrders = merchantOrderRepo.findByIpAndOrderStateIn(param.getIp(),
					Arrays.asList(Constant.商户订单状态_等待接单, Constant.商户订单状态_已接单));
			if (CollectionUtil.isNotEmpty(pendingOrders)
					&& pendingOrders.size() >= receiveOrderRisk.getSameIpOrderNum()) {
				throw new BizException("下单比较频繁,请稍后再试");
			}
		}
		Merchant merchant = merchantRepo.findByUserNameAndDeletedFlagIsFalse(param.getMerchantNum());
		if (merchant == null) {
			throw new BizException(BizError.商户未接入);
		}
		if (!NumberUtil.isNumber(param.getAmount())) {
			throw new BizException(BizError.金额格式不正确);
		}
		if (Double.parseDouble(param.getAmount()) <= 0) {
			throw new BizException(BizError.金额不能小于或等于0);
		}
		String sign = param.getMerchantNum() + param.getOrderNo() + param.getAmount() + param.getNotifyUrl()
				+ merchant.getSecretKey();
		System.out.println(sign);
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
		if (!sign.equals(param.getSign())) {
			throw new BizException(BizError.签名不正确);
		}
		GatheringChannel gatheringChannel = gatheringChannelRepo
				.findByChannelCodeAndDeletedFlagIsFalse(param.getPayType());
		if (gatheringChannel == null) {
			throw new BizException(BizError.发起订单失败通道不存在);
		}
		if (!gatheringChannel.getEnabled()) {
			throw new BizException(BizError.发起订单失败通道维护中);
		}
		GatheringChannelRate gatheringChannelRate = gatheringChannelRateRepo
				.findByMerchantIdAndChannelChannelCode(merchant.getId(), param.getPayType());
		if (gatheringChannelRate == null) {
			throw new BizException(BizError.发起订单失败该通道未开通);
		}
		if (!gatheringChannelRate.getEnabled()) {
			throw new BizException(BizError.发起订单失败该通道已关闭);
		}
		if (Double.parseDouble(param.getAmount()) < gatheringChannelRate.getMinAmount()) {
			throw new BizException(BizError.业务异常.getCode(),
					MessageFormat.format("最低限额为:{0}", gatheringChannelRate.getMinAmount()));
		}
		if (Double.parseDouble(param.getAmount()) > gatheringChannelRate.getMaxAmount()) {
			throw new BizException(BizError.业务异常.getCode(),
					MessageFormat.format("最高限额为:{0}", gatheringChannelRate.getMaxAmount()));
		}
		Integer orderEffectiveDuration = receiveOrderSetting.getReceiveOrderEffectiveDuration();
		MerchantOrder merchantOrder = param.convertToPo(merchant.getId(), gatheringChannel.getId(),
				orderEffectiveDuration);
		if (StrUtil.isNotBlank(merchant.getInviterId())) {
			GatheringChannelRate inviterRate = gatheringChannelRateRepo
					.findByMerchantIdAndChannelChannelCode(merchant.getInviterId(), param.getPayType());
			if (inviterRate != null) {
				merchantOrder.setMerchantAgentRate(inviterRate.getRate());
			}
		}
		merchantOrderRepo.save(merchantOrder);
		String homePageUrl = systemSettingRepo.findTopByOrderByLatelyUpdateTime().getHomePageUrl();
		return StartOrderSuccessVO.convertFor(merchantOrder, homePageUrl, gatheringChannel.getPayUrl());
	}

	@Transactional(readOnly = true)
	public void paySuccessAutoAsynNotice() {
		List<MerchantOrder> merchantOrders = merchantOrderRepo.findByNoticeStateAndOrderStateIn(Constant.通知状态_未通知,
				Arrays.asList(Constant.商户订单状态_已支付, Constant.商户订单状态_补单));
		for (MerchantOrder merchantOrder : merchantOrders) {
			noticePaySuccessAsynNotice(merchantOrder.getId());
		}
	}

	@Transactional(readOnly = true)
	public void noticePaySuccessAsynNotice(@NotBlank String merchantOrderId) {
		redisTemplate.opsForList().leftPush(Constant.异步通知订单ID, merchantOrderId);
	}

	@Lock(keys = "'paySuccessAsynNotice_' + #merchantOrderId")
	@Transactional
	public String paySuccessAsynNotice(@NotBlank String merchantOrderId) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(merchantOrderId);
		if (!(Constant.商户订单状态_已支付.equals(merchantOrder.getOrderState())
				|| Constant.商户订单状态_补单.equals(merchantOrder.getOrderState()))) {
			throw new BizException(BizError.只有已支付的订单才能进行异步通知);
		}
		if (Constant.通知状态_通知成功.equals(merchantOrder.getNoticeState())) {
			log.warn("商户订单支付已通知成功,无需重复通知;商户订单id为{}", merchantOrderId);
			return Constant.通知成功返回值;
		}
		Merchant merchant = merchantRepo.findByUserNameAndDeletedFlagIsFalse(merchantOrder.getMerchant().getUserName());
		if (merchant == null) {
			throw new BizException(BizError.商户未接入);
		}

		String sign = Constant.支付成功 + merchantOrder.getMerchant().getUserName() + merchantOrder.getMerchantOrderNo()
				+ new DecimalFormat("###################.###########").format(merchantOrder.getGatheringAmount())
				+ merchant.getSecretKey();
		sign = new Digester(DigestAlgorithm.MD5).digestHex(sign);
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("merchantNum", merchantOrder.getMerchant().getUserName());
		paramMap.put("orderNo", merchantOrder.getMerchantOrderNo());
		paramMap.put("platformOrderNo", merchantOrder.getOrderNo());
		paramMap.put("amount",
				new DecimalFormat("###################.###########").format(merchantOrder.getGatheringAmount()));
		paramMap.put("actualPayAmount", merchantOrder.getGatheringAmount());
		paramMap.put("attch", merchantOrder.getAttch());
		paramMap.put("state", Constant.支付成功);
		paramMap.put("payTime", DateUtil.format(merchantOrder.getConfirmTime(), DatePattern.NORM_DATETIME_PATTERN));
		paramMap.put("sign", sign);
		String result = "fail";
		// 通知3次
		for (int i = 0; i < 3; i++) {
			try {
				result = HttpUtil.get(merchantOrder.getNotifyUrl(), paramMap, 3500);
				if (Constant.通知成功返回值.equals(result)) {
					break;
				}
			} catch (Exception e) {
				result = e.getMessage();
				log.error(MessageFormat.format("商户订单支付成功异步通知地址请求异常,id为{0}", merchantOrderId), e);
			}
		}
		merchantOrder.setNoticeState(Constant.通知成功返回值.equals(result) ? Constant.通知状态_通知成功 : Constant.通知状态_通知失败);
		merchantOrderRepo.save(merchantOrder);
		return result;
	}

	@Transactional
	public void updateNote(@NotBlank String id, String note, String merchantId) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(id);
		if (!merchantOrder.getMerchantId().equals(merchantId)) {
			throw new BizException(BizError.无权修改商户订单备注);
		}
		updateNoteInner(id, note);
	}

	@Transactional
	public void updateNoteInner(@NotBlank String id, String note) {
		MerchantOrder merchantOrder = merchantOrderRepo.getOne(id);
		merchantOrder.setNote(note);
		merchantOrderRepo.save(merchantOrder);
	}

}
