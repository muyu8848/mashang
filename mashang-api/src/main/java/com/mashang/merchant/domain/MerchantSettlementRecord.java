package com.mashang.merchant.domain;

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
import com.mashang.distributepayout.domain.DistributePayoutOrder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "merchant_settlement_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MerchantSettlementRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;

	private Double withdrawAmount;

	private Double serviceFee;

	private Double actualToAccount;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	private Date applyTime;

	private String state;

	private String note;

	private Date approvalTime;

	private Date confirmCreditedTime;
	
	@Version
	private Long version;
	
	@Column(name = "merchant_id", length = 32)
	private String merchantId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Merchant merchant;
	
	@Column(name = "distribute_payout_order_id", length = 32)
	private String distributePayoutOrderId;
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "distribute_payout_order_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private DistributePayoutOrder distributePayoutOrder;
	
	public void notApproved(String note) {
		this.setState(Constant.商户结算状态_审核不通过);
		this.setApprovalTime(new Date());
		this.setNote(note);
	}
	
	public void exceptionBack(String note) {
		this.setState(Constant.商户结算状态_异常退回);
		this.setApprovalTime(new Date());
		this.setNote(note);
	}

	public void approved(String note) {
		this.setState(Constant.商户结算状态_审核通过);
		this.setApprovalTime(new Date());
		this.setNote(note);
	}

	public void confirmCredited() {
		this.setState(Constant.商户结算状态_已到账);
		this.setConfirmCreditedTime(new Date());
	}
	
	public void resetToPendingState() {
		this.setState(Constant.商户结算状态_审核中);
		this.setDistributePayoutOrderId(null);
	}
	
	public void distributeMember() {
		this.setState(Constant.商户结算状态_下发待处理);
		this.setDistributePayoutOrderId(null);
	}
	
	public void memberReceiveOrder(String distributePayoutOrderId) {
		this.setState(Constant.商户结算状态_下发处理中);
		this.setDistributePayoutOrderId(distributePayoutOrderId);
	}
	
	

}
