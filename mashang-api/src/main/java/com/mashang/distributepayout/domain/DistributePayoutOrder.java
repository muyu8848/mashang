package com.mashang.distributepayout.domain;

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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.mashang.constants.Constant;
import com.mashang.merchant.domain.MerchantSettlementRecord;
import com.mashang.useraccount.domain.UserAccount;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "distribute_payout_order")
@DynamicInsert(true)
@DynamicUpdate(true)
public class DistributePayoutOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;

	private Double amount;

	private Double memberIncome;

	private String note;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	private Date createTime;

	private Date receivedTime;

	private String orderState;

	private Date dealTime;

	private Date settlementTime;

	private String depositor;

	private String paymentVoucherId;

	@Version
	private Long version;

	@Column(name = "merchant_settlement_record_id", length = 32)
	private String merchantSettlementRecordId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_settlement_record_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private MerchantSettlementRecord merchantSettlementRecord;

	@Column(name = "received_account_id", length = 32)
	private String receivedAccountId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "received_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount receivedAccount;
	
	@Column(name = "deal_account_id", length = 32)
	private String dealAccountId;
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deal_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount dealAccount;

	public void paid(String dealAccountId) {
		this.setDealTime(new Date());
		this.setDealAccountId(dealAccountId);
		this.setOrderState(Constant.下发代付订单状态_支付成功);
	}

	public void fail(String note, String dealAccountId) {
		this.setDealTime(new Date());
		this.setDealAccountId(dealAccountId);
		this.setOrderState(Constant.下发代付订单状态_支付失败);
		this.setNote(note);
	}

}
