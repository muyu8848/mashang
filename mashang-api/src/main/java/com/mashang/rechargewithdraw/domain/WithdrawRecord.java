package com.mashang.rechargewithdraw.domain;

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

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.mashang.constants.Constant;
import com.mashang.useraccount.domain.BankCard;
import com.mashang.useraccount.domain.UserAccount;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "withdraw_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class WithdrawRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;

	private Double withdrawAmount;

	private Double handlingFee;

	private String withdrawWay;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	private Date submitTime;

	private String state;

	private String note;

	private Date approvalTime;

	private Date confirmCreditedTime;

	@Column(name = "user_account_id", length = 32)
	private String userAccountId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount userAccount;

	public void setBankInfo(BankCard bankCard) {
		this.setWithdrawWay(Constant.提现方式_银行卡);
		this.setOpenAccountBank(bankCard.getOpenAccountBank());
		this.setAccountHolder(bankCard.getAccountHolder());
		this.setBankCardAccount(bankCard.getBankCardAccount());
	}

	public void approved(String note) {
		this.setState(Constant.提现记录状态_审核通过);
		this.setApprovalTime(new Date());
		this.setNote(note);
	}

	public void notApproved(String note) {
		this.setState(Constant.提现记录状态_审核不通过);
		this.setApprovalTime(new Date());
		this.setNote(note);
	}

	public void confirmCredited() {
		this.setState(Constant.提现记录状态_已到账);
		this.setConfirmCreditedTime(new Date());
	}

}
