package com.mashang.merchant.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.mashang.common.exception.BizError;
import com.mashang.common.exception.BizException;
import com.mashang.common.utils.IdUtils;
import com.mashang.common.valid.ParamValid;
import com.mashang.common.vo.PageResult;
import com.mashang.constants.Constant;
import com.mashang.distributepayout.domain.DistributePayoutOrder;
import com.mashang.distributepayout.repo.DistributePayoutOrderRepo;
import com.mashang.mastercontrol.domain.MerchantSettlementSetting;
import com.mashang.mastercontrol.repo.MerchantSettlementSettingRepo;
import com.mashang.merchant.domain.Merchant;
import com.mashang.merchant.domain.MerchantAccountChangeLog;
import com.mashang.merchant.domain.MerchantSettlementRecord;
import com.mashang.merchant.param.ApplySettlementParam;
import com.mashang.merchant.param.MerchantSettlementRecordQueryCondParam;
import com.mashang.merchant.repo.MerchantAccountChangeLogRepo;
import com.mashang.merchant.repo.MerchantRepo;
import com.mashang.merchant.repo.MerchantSettlementRecordRepo;
import com.mashang.merchant.vo.MerchantSettlementRecordSummaryVO;
import com.mashang.merchant.vo.MerchantSettlementRecordVO;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class MerchantSettlementService {

	@Autowired
	private MerchantRepo merchantRepo;

	@Autowired
	private MerchantSettlementRecordRepo merchantSettlementRecordRepo;

	@Autowired
	private MerchantSettlementSettingRepo merchantSettlementSettingRepo;

	@Autowired
	private MerchantAccountChangeLogRepo merchantAccountChangeLogRepo;

	@Autowired
	private DistributePayoutOrderRepo distributePayoutOrderRepo;

	@Lock(keys = "'updateMerchantSettlementOrderState_' + #id")
	public void exceptionBack(@NotBlank String id, String note) {
		MerchantSettlementRecord record = merchantSettlementRecordRepo.getOne(id);
		if (!(Constant.商户结算状态_已到账.equals(record.getState()))) {
			throw new BizException("只有已到账的记录才能异常退回");
		}
		record.exceptionBack(note);
		merchantSettlementRecordRepo.save(record);

		Merchant merchant = record.getMerchant();
		double withdrawableAmount = NumberUtil
				.round(merchant.getWithdrawableAmount() + record.getActualToAccount() + record.getServiceFee(), 2)
				.doubleValue();
		merchant.setWithdrawableAmount(withdrawableAmount);
		merchantRepo.save(merchant);
		merchantAccountChangeLogRepo
				.save(MerchantAccountChangeLog.buildWithWithdrawSettlementExceptionBack(merchant, record));

	}

	@Lock(keys = "'updateMerchantSettlementOrderState_' + #id")
	@ParamValid
	@Transactional
	public void cancelDistributeResetToPendingState(@NotBlank String id) {
		MerchantSettlementRecord record = merchantSettlementRecordRepo.getOne(id);
		if (!(Constant.商户结算状态_下发待处理.equals(record.getState()))) {
			throw new BizException("只有下发待处理的订单才能退回到审核中");
		}
		record.resetToPendingState();
		merchantSettlementRecordRepo.save(record);
	}

	@Lock(keys = "'updateMerchantSettlementOrderState_' + #id")
	@ParamValid
	@Transactional
	public void resetToPendingState(@NotBlank String id) {
		MerchantSettlementRecord record = merchantSettlementRecordRepo.getOne(id);
		if (!(Constant.商户结算状态_审核通过.equals(record.getState()) || Constant.商户结算状态_下发处理中.equals(record.getState()))) {
			throw new BizException(BizError.业务异常.getCode(), "只有审核通过或者下发处理中的订单才能退回到审核中");
		}
		if (record.getDistributePayoutOrder() != null
				&& Constant.下发代付订单状态_待支付.equals(record.getDistributePayoutOrder().getOrderState())) {
			DistributePayoutOrder order = record.getDistributePayoutOrder();
			order.setOrderState(Constant.下发代付订单状态_支付失败);
			order.setDealTime(new Date());
			distributePayoutOrderRepo.save(order);
		}
		record.resetToPendingState();
		merchantSettlementRecordRepo.save(record);
	}

	@Lock(keys = "'updateMerchantSettlementOrderState_' + #id")
	@ParamValid
	@Transactional
	public void distributeMember(@NotBlank String id) {
		MerchantSettlementRecord record = merchantSettlementRecordRepo.getOne(id);
		if (!(Constant.商户结算状态_审核中.equals(record.getState()))) {
			throw new BizException(BizError.业务异常.getCode(), "只有审核中的订单才能下发给会员");
		}
		record.distributeMember();
		merchantSettlementRecordRepo.save(record);

		DistributePayoutOrder order = new DistributePayoutOrder();
		order.setId(IdUtils.getId());
		order.setOrderNo(order.getId());
		order.setCreateTime(new Date());
		order.setOrderState(Constant.下发代付订单状态_待接单);
		order.setAmount(record.getActualToAccount());
		order.setOpenAccountBank(record.getOpenAccountBank());
		order.setAccountHolder(record.getAccountHolder());
		order.setBankCardAccount(record.getBankCardAccount());
		order.setMerchantSettlementRecordId(record.getId());
		distributePayoutOrderRepo.save(order);
	}

	@ParamValid
	@Transactional
	public void applySettlement(ApplySettlementParam param) {
		MerchantSettlementSetting merchantSettlementSetting = merchantSettlementSettingRepo
				.findTopByOrderByLatelyUpdateTime();
		if (param.getWithdrawAmount() < merchantSettlementSetting.getMinAmount()) {
			throw new BizException(BizError.业务异常.getCode(),
					"最低金额不能小于" + new DecimalFormat("###################.###########")
							.format(merchantSettlementSetting.getMinAmount()));
		}
		if (param.getWithdrawAmount() > merchantSettlementSetting.getMaxAmount()) {
			throw new BizException(BizError.业务异常.getCode(),
					"最高金额不能大于" + new DecimalFormat("###################.###########")
							.format(merchantSettlementSetting.getMaxAmount()));
		}
		Merchant merchant = merchantRepo.getOne(param.getMerchantId());
		BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
		if (StrUtil.isBlank(merchant.getMoneyPwd())) {
			throw new BizException("请联系客服设置资金密码");
		}
		if (!pwdEncoder.matches(param.getMoneyPwd(), merchant.getMoneyPwd())) {
			throw new BizException(BizError.资金密码不正确);
		}
		if (merchant.getWithdrawableAmount() - param.getWithdrawAmount() < 0) {
			throw new BizException(BizError.可提现金额不足);
		}
		MerchantSettlementSetting setting = merchantSettlementSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting.getMerchantSettlementRate() != null) {
			double serviceFee = NumberUtil
					.round(param.getWithdrawAmount() * setting.getMerchantSettlementRate() / 100, 2).doubleValue();
			if (serviceFee < setting.getMinServiceFee()) {
				serviceFee = setting.getMinServiceFee();
			}
			double actualToAccount = param.getWithdrawAmount();
			if (param.getWithdrawAmount() + serviceFee > merchant.getWithdrawableAmount()) {
				actualToAccount = NumberUtil.round(param.getWithdrawAmount() - serviceFee, 2).doubleValue();
			}
			param.setServiceFee(serviceFee);
			param.setActualToAccount(actualToAccount);
		} else {
			param.setServiceFee(0d);
			param.setActualToAccount(param.getWithdrawAmount());
		}
		merchant.setWithdrawableAmount(NumberUtil
				.round(merchant.getWithdrawableAmount() - param.getActualToAccount() - param.getServiceFee(), 2)
				.doubleValue());
		merchantRepo.save(merchant);
		MerchantSettlementRecord merchantSettlementRecord = param.convertToPo();
		merchantSettlementRecordRepo.save(merchantSettlementRecord);
		merchantAccountChangeLogRepo
				.save(MerchantAccountChangeLog.buildWithWithdrawSettlement(merchant, merchantSettlementRecord));
	}

	@Lock(keys = "'updateMerchantSettlementOrderState_' + #id")
	@ParamValid
	@Transactional
	public void settlementConfirmCredited(@NotBlank String id) {
		MerchantSettlementRecord record = merchantSettlementRecordRepo.getOne(id);
		if (!(Constant.商户结算状态_审核通过.equals(record.getState()))) {
			throw new BizException(BizError.只有状态为审核通过的记录才能进行确认到帐操作);
		}

		record.confirmCredited();
		merchantSettlementRecordRepo.save(record);
	}

	@Lock(keys = "'updateMerchantSettlementOrderState_' + #id")
	@ParamValid
	@Transactional
	public void settlementApproved(@NotBlank String id, String note) {
		MerchantSettlementRecord record = merchantSettlementRecordRepo.getOne(id);
		if (!Constant.商户结算状态_审核中.equals(record.getState())) {
			throw new BizException(BizError.只有状态为审核中的记录才能审核通过操作);
		}

		record.approved(note);
		merchantSettlementRecordRepo.save(record);
	}

	@Lock(keys = "'updateMerchantSettlementOrderState_' + #id")
	public void settlementNotApproved(@NotBlank String id, String note) {
		MerchantSettlementRecord record = merchantSettlementRecordRepo.getOne(id);
		if (!(Constant.商户结算状态_审核中.equals(record.getState()) || Constant.商户结算状态_审核通过.equals(record.getState()))) {
			throw new BizException(BizError.只有状态为审核中或审核通过的记录才能进行审核不通过操作);
		}
		record.notApproved(note);
		merchantSettlementRecordRepo.save(record);

		Merchant merchant = record.getMerchant();
		double withdrawableAmount = NumberUtil
				.round(merchant.getWithdrawableAmount() + record.getActualToAccount() + record.getServiceFee(), 2)
				.doubleValue();
		merchant.setWithdrawableAmount(withdrawableAmount);
		merchantRepo.save(merchant);
		merchantAccountChangeLogRepo
				.save(MerchantAccountChangeLog.buildWithWithdrawSettlementNotApprovedRefund(merchant, record));

	}

	@Transactional(readOnly = true)
	public MerchantSettlementRecordVO findByMerchantSettlementRecordId(@NotBlank String id) {
		return MerchantSettlementRecordVO.convertFor(merchantSettlementRecordRepo.getOne(id));
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantSettlementRecordVO> findTop5TodoSettlementByPage() {
		Specification<MerchantSettlementRecord> spec = new Specification<MerchantSettlementRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantSettlementRecord> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("state"), Constant.商户结算状态_审核中));
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<MerchantSettlementRecord> result = merchantSettlementRecordRepo.findAll(spec,
				PageRequest.of(0, 5, Sort.by(Sort.Order.desc("applyTime"))));
		PageResult<MerchantSettlementRecordVO> pageResult = new PageResult<>(
				MerchantSettlementRecordVO.convertFor(result.getContent()), 1, 5, result.getTotalElements());
		return pageResult;
	}

	public Specification<MerchantSettlementRecord> buildMerchantSettlementRecordQueryCond(
			MerchantSettlementRecordQueryCondParam param) {
		Specification<MerchantSettlementRecord> spec = new Specification<MerchantSettlementRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantSettlementRecord> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getMerchantId())) {
					predicates.add(builder.equal(root.get("merchantId"), param.getMerchantId()));
				}
				if (StrUtil.isNotBlank(param.getMerchantNum())) {
					predicates.add(builder.equal(root.join("merchant", JoinType.INNER).get("userName"),
							param.getMerchantNum()));
				}
				if (StrUtil.isNotBlank(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}
				if (param.getApplyStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("applyTime").as(Date.class),
							DateUtil.beginOfDay(param.getApplyStartTime())));
				}
				if (param.getApplyEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("applyTime").as(Date.class),
							DateUtil.endOfDay(param.getApplyEndTime())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

	@Transactional(readOnly = true)
	public List<MerchantSettlementRecordVO> findMerchantSettlementRecord(MerchantSettlementRecordQueryCondParam param) {
		Specification<MerchantSettlementRecord> spec = buildMerchantSettlementRecordQueryCond(param);
		List<MerchantSettlementRecord> result = merchantSettlementRecordRepo.findAll(spec,
				Sort.by(Sort.Order.desc("applyTime")));
		return MerchantSettlementRecordVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantSettlementRecordVO> findMerchantSettlementRecordByPage(
			MerchantSettlementRecordQueryCondParam param) {
		Specification<MerchantSettlementRecord> spec = buildMerchantSettlementRecordQueryCond(param);
		Page<MerchantSettlementRecord> result = merchantSettlementRecordRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("applyTime"))));
		PageResult<MerchantSettlementRecordVO> pageResult = new PageResult<>(
				MerchantSettlementRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public MerchantSettlementRecordSummaryVO merchantSettlementRecordSummary(
			MerchantSettlementRecordQueryCondParam param) {
		Double withdrawAmount = 0d;
		Double serviceFee = 0d;
		Double actualToAccount = 0d;
		Specification<MerchantSettlementRecord> spec = buildMerchantSettlementRecordQueryCond(param);
		List<MerchantSettlementRecord> records = merchantSettlementRecordRepo.findAll(spec);
		for (MerchantSettlementRecord record : records) {
			if (!(Constant.商户结算状态_已到账.equals(record.getState()))) {
				continue;
			}
			withdrawAmount += record.getWithdrawAmount();
			serviceFee += record.getServiceFee();
			actualToAccount += record.getActualToAccount();
		}
		withdrawAmount = NumberUtil.round(withdrawAmount, 2).doubleValue();
		serviceFee = NumberUtil.round(serviceFee, 2).doubleValue();
		actualToAccount = NumberUtil.round(actualToAccount, 2).doubleValue();
		return MerchantSettlementRecordSummaryVO.build(withdrawAmount, serviceFee, actualToAccount);
	}

}
