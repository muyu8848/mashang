package com.mashang.useraccount.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.mashang.common.utils.IdUtils;
import com.mashang.constants.Constant;
import com.mashang.distributepayout.domain.DistributePayoutOrder;
import com.mashang.income.domain.IncomeRecord;
import com.mashang.merchant.domain.MerchantOrder;
import com.mashang.rechargewithdraw.domain.RechargeOrder;
import com.mashang.rechargewithdraw.domain.WithdrawRecord;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "account_change_log")
@DynamicInsert(true)
@DynamicUpdate(true)
public class AccountChangeLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;

	private Date accountChangeTime;

	private String accountChangeType;

	private Double cashDepositChange;

	private Double cashDepositBefore;

	private Double cashDepositAfter;

	private String note;

	@Version
	private Long version;

	@Column(name = "user_account_id", length = 32)
	private String userAccountId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount userAccount;

	public static AccountChangeLog buildWithCollectForAnotherIncome(UserAccount userAccount,
			IncomeRecord incomeRecord) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setAccountChangeTime(new Date());
		log.setAccountChangeType(Constant.账变日志类型_代收收益);
		log.setCashDepositChange(incomeRecord.getIncome());
		log.setCashDepositBefore(
				NumberUtil.round(userAccount.getCashDeposit() - incomeRecord.getIncome(), 2).doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		log.setNote("代收记录订单号:" + incomeRecord.getBizId());
		return log;
	}

	public static AccountChangeLog buildWithCollectForAnotherTeamIncome(UserAccount userAccount,
			IncomeRecord incomeRecord) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setAccountChangeTime(new Date());
		log.setAccountChangeType(Constant.账变日志类型_代收团队收益);
		log.setCashDepositChange(incomeRecord.getIncome());
		log.setCashDepositBefore(
				NumberUtil.round(userAccount.getCashDeposit() - incomeRecord.getIncome(), 2).doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		log.setNote("代收记录订单号:" + incomeRecord.getBizId());
		return log;
	}
	
	public static AccountChangeLog buildWithDistributePayoutIncome(UserAccount userAccount, IncomeRecord incomeRecord) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setAccountChangeTime(new Date());
		log.setAccountChangeType(Constant.账变日志类型__下发代付收益);
		log.setCashDepositChange(incomeRecord.getIncome());
		log.setCashDepositBefore(
				NumberUtil.round(userAccount.getCashDeposit() - incomeRecord.getIncome(), 2).doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		log.setNote("下发代付订单号:" + incomeRecord.getBizId());
		return log;
	}

	public static AccountChangeLog buildWithServiceProviderRechargeIncome(UserAccount userAccount, IncomeRecord incomeRecord) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setAccountChangeTime(new Date());
		log.setAccountChangeType(Constant.账变日志类型_代充收益);
		log.setCashDepositChange(incomeRecord.getIncome());
		log.setCashDepositBefore(
				NumberUtil.round(userAccount.getCashDeposit() - incomeRecord.getIncome(), 2).doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		log.setNote("代充订单号:" + incomeRecord.getBizId());
		return log;
	}

	public static AccountChangeLog buildWithAlterToActualPayAmountRefund(UserAccount userAccount, String orderNo,
			Double refundAmount) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(orderNo);
		log.setAccountChangeTime(new Date());
		log.setAccountChangeType(Constant.账变日志类型__改单为实际支付金额);
		log.setCashDepositChange(refundAmount);
		log.setCashDepositBefore(NumberUtil.round(userAccount.getCashDeposit() - refundAmount, 2).doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		return log;
	}

	public static AccountChangeLog buildWithBackgroundAdjustCashDeposit(UserAccount userAccount, Double changeAmount,
			String note) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setAccountChangeTime(new Date());
		log.setAccountChangeType(Constant.账变日志类型_后台调整余额);
		log.setCashDepositChange(changeAmount);
		log.setCashDepositBefore(NumberUtil.round(userAccount.getCashDeposit() - changeAmount, 2).doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		if (StrUtil.isNotBlank(note)) {
			log.setNote(note);
		}
		return log;
	}

	public static AccountChangeLog buildWithReleaseFreezeAmount(UserAccount userAccount, MerchantOrder merchantOrder) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(merchantOrder.getOrderNo());
		log.setAccountChangeTime(new Date());
		log.setAccountChangeType(Constant.账变日志类型_退还冻结资金);
		log.setCashDepositChange(merchantOrder.getGatheringAmount());
		log.setCashDepositBefore(
				NumberUtil.round(userAccount.getCashDeposit() - merchantOrder.getGatheringAmount(), 2).doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		return log;
	}

	public static AccountChangeLog buildWithCancelOrderRefund(UserAccount userAccount, MerchantOrder merchantOrder) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(merchantOrder.getOrderNo());
		log.setAccountChangeTime(new Date());
		log.setAccountChangeType(Constant.账变日志类型_取消订单退款);
		log.setCashDepositChange(merchantOrder.getGatheringAmount());
		log.setCashDepositBefore(
				NumberUtil.round(userAccount.getCashDeposit() - merchantOrder.getGatheringAmount(), 2).doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		return log;
	}

	public static AccountChangeLog buildWithRecharge(UserAccount userAccount, RechargeOrder rechargeOrder) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(rechargeOrder.getOrderNo());
		log.setAccountChangeTime(rechargeOrder.getSettlementTime());
		log.setAccountChangeType(Constant.账变日志类型_账号充值);
		log.setCashDepositChange(NumberUtil.round(rechargeOrder.getRechargeAmount(), 2).doubleValue());
		log.setCashDepositBefore(
				NumberUtil.round(userAccount.getCashDeposit() - rechargeOrder.getRechargeAmount(), 2).doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		return log;
	}

	public static AccountChangeLog buildWithReceiveOrderDeduction(UserAccount userAccount, MerchantOrder merchantOrder,
			Boolean supplementOrderFlag) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(merchantOrder.getOrderNo());
		log.setAccountChangeTime(new Date());
		log.setAccountChangeType(Constant.账变日志类型_接单扣款);
		log.setCashDepositChange(-merchantOrder.getGatheringAmount());
		log.setCashDepositBefore(
				NumberUtil.round(userAccount.getCashDeposit() + merchantOrder.getGatheringAmount(), 2).doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		if (supplementOrderFlag) {
			log.setNote("补单");
		}
		log.setUserAccountId(userAccount.getId());
		return log;
	}

	public static AccountChangeLog buildWithStartWithdraw(UserAccount userAccount, WithdrawRecord withdrawRecord) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(withdrawRecord.getOrderNo());
		log.setAccountChangeTime(withdrawRecord.getSubmitTime());
		log.setAccountChangeType(Constant.账变日志类型_账号提现);
		log.setCashDepositChange(-NumberUtil
				.round(withdrawRecord.getWithdrawAmount() + withdrawRecord.getHandlingFee(), 2).doubleValue());
		log.setCashDepositBefore(NumberUtil.round(
				userAccount.getCashDeposit() + withdrawRecord.getWithdrawAmount() + withdrawRecord.getHandlingFee(), 2)
				.doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		if (withdrawRecord.getHandlingFee() > 0) {
			log.setNote("提现金额:" + withdrawRecord.getWithdrawAmount() + "," + "手续费:" + withdrawRecord.getHandlingFee());
		}
		return log;
	}

	public static AccountChangeLog buildWithWithdrawNotApprovedRefund(UserAccount userAccount,
			WithdrawRecord withdrawRecord) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(withdrawRecord.getOrderNo());
		log.setAccountChangeTime(withdrawRecord.getApprovalTime());
		log.setAccountChangeType(Constant.账变日志类型_提现不符退款);
		log.setCashDepositChange(NumberUtil
				.round(withdrawRecord.getWithdrawAmount() + withdrawRecord.getHandlingFee(), 2).doubleValue());
		log.setCashDepositBefore(NumberUtil.round(
				userAccount.getCashDeposit() - withdrawRecord.getWithdrawAmount() - withdrawRecord.getHandlingFee(), 2)
				.doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		if (withdrawRecord.getHandlingFee() > 0) {
			log.setNote("提现金额:" + withdrawRecord.getWithdrawAmount() + "," + "手续费:" + withdrawRecord.getHandlingFee());
		}
		return log;
	}

	public static AccountChangeLog buildWithDistributePayout(UserAccount userAccount, DistributePayoutOrder order) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(order.getOrderNo());
		log.setAccountChangeTime(order.getSettlementTime());
		log.setAccountChangeType(Constant.账变日志类型_下发代付);
		log.setCashDepositChange(NumberUtil.round(order.getAmount(), 2).doubleValue());
		log.setCashDepositBefore(NumberUtil.round(userAccount.getCashDeposit() - order.getAmount(), 2).doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		return log;
	}

	public static AccountChangeLog buildWithBalanceTransferOutServiceProviderAmount(UserAccount userAccount,
			Double quantity) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setAccountChangeTime(new Date());
		log.setAccountChangeType(Constant.账变日志类型_转出到代充余额);
		log.setCashDepositChange(-NumberUtil.round(quantity, 2).doubleValue());
		log.setCashDepositBefore(NumberUtil.round(userAccount.getCashDeposit() + quantity, 2).doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		return log;
	}

	public static AccountChangeLog buildWithServiceProviderAmountTransferInBalance(UserAccount userAccount,
			Double quantity) {
		AccountChangeLog log = new AccountChangeLog();
		log.setId(IdUtils.getId());
		log.setAccountChangeTime(new Date());
		log.setAccountChangeType(Constant.账变日志类型_代充余额转入);
		log.setCashDepositChange(NumberUtil.round(quantity, 2).doubleValue());
		log.setCashDepositBefore(NumberUtil.round(userAccount.getCashDeposit() - quantity, 2).doubleValue());
		log.setCashDepositAfter(userAccount.getCashDeposit());
		log.setUserAccountId(userAccount.getId());
		return log;
	}

}
