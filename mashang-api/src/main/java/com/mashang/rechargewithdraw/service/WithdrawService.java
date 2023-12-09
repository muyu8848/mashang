package com.mashang.rechargewithdraw.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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

import com.mashang.common.exception.BizError;
import com.mashang.common.exception.BizException;
import com.mashang.common.valid.ParamValid;
import com.mashang.common.vo.PageResult;
import com.mashang.constants.Constant;
import com.mashang.mastercontrol.domain.WithdrawSetting;
import com.mashang.mastercontrol.repo.WithdrawSettingRepo;
import com.mashang.rechargewithdraw.domain.WithdrawRecord;
import com.mashang.rechargewithdraw.param.StartWithdrawParam;
import com.mashang.rechargewithdraw.param.WithdrawRecordQueryCondParam;
import com.mashang.rechargewithdraw.repo.WithdrawRecordRepo;
import com.mashang.rechargewithdraw.vo.WithdrawRecordVO;
import com.mashang.useraccount.domain.AccountChangeLog;
import com.mashang.useraccount.domain.BankCard;
import com.mashang.useraccount.domain.UserAccount;
import com.mashang.useraccount.repo.AccountChangeLogRepo;
import com.mashang.useraccount.repo.BankCardRepo;
import com.mashang.useraccount.repo.UserAccountRepo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

@Service
public class WithdrawService {

	@Autowired
	private WithdrawRecordRepo withdrawRecordRepo;

	@Autowired
	private UserAccountRepo userAccountRepo;

	@Autowired
	private AccountChangeLogRepo accountChangeLogRepo;

	@Autowired
	private WithdrawSettingRepo withdrawSettingRepo;

	@Autowired
	private BankCardRepo bankCardRepo;

	@Transactional(readOnly = true)
	public WithdrawRecordVO findWithdrawRecordById(@NotBlank String id) {
		WithdrawRecord withdrawRecord = withdrawRecordRepo.getOne(id);
		return WithdrawRecordVO.convertFor(withdrawRecord);
	}

	@ParamValid
	@Transactional
	public void approved(@NotBlank String id, String note) {
		WithdrawRecord withdrawRecord = withdrawRecordRepo.getOne(id);
		if (!Constant.提现记录状态_审核中.equals(withdrawRecord.getState())) {
			throw new BizException("只有审核中的记录才能审核通过");
		}

		withdrawRecord.approved(note);
		withdrawRecordRepo.save(withdrawRecord);
	}

	@ParamValid
	@Transactional
	public void notApproved(@NotBlank String id, String note) {
		WithdrawRecord withdrawRecord = withdrawRecordRepo.getOne(id);
		if (!(Constant.提现记录状态_审核中.equals(withdrawRecord.getState())
				|| Constant.提现记录状态_审核通过.equals(withdrawRecord.getState()))) {
			throw new BizException(BizError.只有状态为发起提现或审核通过的记录才能进行审核不通过操作);
		}

		withdrawRecord.notApproved(note);
		withdrawRecordRepo.save(withdrawRecord);

		UserAccount userAccount = withdrawRecord.getUserAccount();
		double cashDeposit = NumberUtil.round(
				userAccount.getCashDeposit() + withdrawRecord.getWithdrawAmount() + withdrawRecord.getHandlingFee(), 2)
				.doubleValue();
		userAccount.setCashDeposit(cashDeposit);
		userAccountRepo.save(userAccount);
		accountChangeLogRepo.save(AccountChangeLog.buildWithWithdrawNotApprovedRefund(userAccount, withdrawRecord));
	}

	@ParamValid
	@Transactional
	public void confirmCredited(@NotBlank String id) {
		WithdrawRecord withdrawRecord = withdrawRecordRepo.getOne(id);
		if (!(Constant.提现记录状态_审核通过.equals(withdrawRecord.getState()))) {
			throw new BizException(BizError.只有状态为审核通过的记录才能进行确认到帐操作);
		}

		withdrawRecord.confirmCredited();
		withdrawRecordRepo.save(withdrawRecord);
	}

	@Transactional(readOnly = true)
	public PageResult<WithdrawRecordVO> findTop5TodoWithdrawRecordByPage() {
		Specification<WithdrawRecord> spec = new Specification<WithdrawRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<WithdrawRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("state"), Constant.提现记录状态_审核中));
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<WithdrawRecord> result = withdrawRecordRepo.findAll(spec,
				PageRequest.of(0, 5, Sort.by(Sort.Order.desc("submitTime"))));
		PageResult<WithdrawRecordVO> pageResult = new PageResult<>(WithdrawRecordVO.convertFor(result.getContent()), 1,
				5, result.getTotalElements());
		return pageResult;
	}

	public Specification<WithdrawRecord> buildWithdrawRecordQueryCond(WithdrawRecordQueryCondParam param) {
		Specification<WithdrawRecord> spec = new Specification<WithdrawRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<WithdrawRecord> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (StrUtil.isNotBlank(param.getUserAccountId())) {
					predicates.add(builder.equal(root.get("userAccountId"), param.getUserAccountId()));
				}
				if (StrUtil.isNotBlank(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}
				if (StrUtil.isNotBlank(param.getBankCardInfo())) {
					Predicate or = builder.or(
							builder.like(root.get("bankCardAccount"), "%" + param.getBankCardInfo() + "%"),
							builder.like(root.get("accountHolder"), "%" + param.getBankCardInfo() + "%"));
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
	public List<WithdrawRecordVO> findWithdrawRecord(WithdrawRecordQueryCondParam param) {
		Specification<WithdrawRecord> spec = buildWithdrawRecordQueryCond(param);
		List<WithdrawRecord> result = withdrawRecordRepo.findAll(spec, Sort.by(Sort.Order.desc("submitTime")));
		return WithdrawRecordVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public PageResult<WithdrawRecordVO> findWithdrawRecordByPage(WithdrawRecordQueryCondParam param) {
		Specification<WithdrawRecord> spec = buildWithdrawRecordQueryCond(param);
		Page<WithdrawRecord> result = withdrawRecordRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("submitTime"))));
		PageResult<WithdrawRecordVO> pageResult = new PageResult<>(WithdrawRecordVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@ParamValid
	@Transactional
	public void startWithdrawWithBankCard(StartWithdrawParam param) {
		UserAccount userAccount = userAccountRepo.getOne(param.getUserAccountId());
		if (StrUtil.isBlank(userAccount.getMoneyPwd())) {
			throw new BizException("请联系客服设置资金密码");
		}
		BankCard bankCard = bankCardRepo.findByIdAndUserAccountId(param.getBankCardId(), param.getUserAccountId());
		if (bankCard == null) {
			throw new BizException("银行卡未绑定无法进行提现");
		}
		if (!new BCryptPasswordEncoder().matches(param.getMoneyPwd(), userAccount.getMoneyPwd())) {
			throw new BizException(BizError.资金密码不正确);
		}
		WithdrawSetting withdrawSetting = withdrawSettingRepo.findTopByOrderByLatelyUpdateTime();
		List<WithdrawRecord> withdrawRecords = withdrawRecordRepo
				.findByUserAccountIdAndSubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(userAccount.getId(),
						DateUtil.beginOfDay(new Date()), DateUtil.endOfDay(new Date()));
		if (withdrawRecords.size() >= withdrawSetting.getEverydayWithdrawTimesUpperLimit()) {
			throw new BizException(BizError.业务异常.getCode(),
					"每日提现次数为" + withdrawSetting.getEverydayWithdrawTimesUpperLimit() + "次,你已达到上限");
		}
		if (param.getWithdrawAmount() < withdrawSetting.getWithdrawLowerLimit()) {
			throw new BizException(BizError.业务异常.getCode(),
					"最低提现金额不能小于" + new DecimalFormat("###################.###########")
							.format(withdrawSetting.getWithdrawLowerLimit()));
		}
		if (param.getWithdrawAmount() > withdrawSetting.getWithdrawUpperLimit()) {
			throw new BizException(BizError.业务异常.getCode(),
					"最高提现金额不能大于" + new DecimalFormat("###################.###########")
							.format(withdrawSetting.getWithdrawUpperLimit()));
		}
		if (userAccount.getCashDeposit() - param.getWithdrawAmount() < 0) {
			throw new BizException("余额不足");
		}
		double handlingFee = 0d;
		if (withdrawSetting.getWithdrawRate() != null) {
			handlingFee = NumberUtil.round(param.getWithdrawAmount() * withdrawSetting.getWithdrawRate() / 100, 2)
					.doubleValue();
			if (handlingFee < withdrawSetting.getMinHandlingFee()) {
				handlingFee = withdrawSetting.getMinHandlingFee();
			}
			if (param.getWithdrawAmount() + handlingFee > userAccount.getCashDeposit()) {
				throw new BizException("余额不足以扣除手续费");
			}
		}
		WithdrawRecord withdrawRecord = param.convertToPo(handlingFee);
		withdrawRecord.setBankInfo(bankCard);
		withdrawRecordRepo.save(withdrawRecord);

		userAccount.setCashDeposit(NumberUtil
				.round(userAccount.getCashDeposit() - param.getWithdrawAmount() - handlingFee, 2).doubleValue());
		userAccountRepo.save(userAccount);
		accountChangeLogRepo.save(AccountChangeLog.buildWithStartWithdraw(userAccount, withdrawRecord));
	}

}
