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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.constants.Constant;
import com.mashang.gatheringcode.domain.GatheringCode;
import com.mashang.useraccount.domain.UserAccount;

import cn.hutool.core.util.NumberUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "merchant_order")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MerchantOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;

	private Double gatheringAmount;

	private Double floatAmount;

	private Date submitTime;

	private Date usefulTime;

	private String orderState;

	private String note;

	@Column(name = "merchant_id", length = 32)
	private String merchantId;

	@Column(name = "received_account_id", length = 32)
	private String receivedAccountId;

	private Date receivedTime;

	private String gatheringCodeStorageId;

	private Date dealTime;

	@Column(name = "deal_account_id", length = 32)
	private String dealAccountId;

	private Date confirmTime;

	private String payerName;
	
	private String payerBankCardTail;

	private Double rate;
	
	private Double merchantAgentRate;
	
	private Double handlingFee;
	
	private Double merchantAgentHandlingFee;

	private Double rebate;

	private Double bounty;
	
	private Double memberIncome;

	private Double memberTeamTotalIncome;

	private String merchantOrderNo;

	private String notifyUrl;

	private String returnUrl;

	private String attch;

	private String noticeState;

	private Date noticeTime;

	private String ip;

	@Version
	private Long version;

	@Column(name = "gathering_channel_id", length = 32)
	private String gatheringChannelId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gathering_channel_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private GatheringChannel gatheringChannel;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Merchant merchant;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "received_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount receivedAccount;

	@Column(name = "gathering_code_id", length = 32)
	private String gatheringCodeId;

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gathering_code_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private GatheringCode gatheringCode;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deal_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount dealAccount;

	public MerchantOrder supplementOrder() {
		MerchantOrder supplementOrder = new MerchantOrder();
		BeanUtils.copyProperties(this, supplementOrder);
		supplementOrder.setId(IdUtils.getId());
		supplementOrder.setSubmitTime(new Date());
		return supplementOrder;
	}

	public void updateBounty(Double bounty) {
		this.setBounty(bounty);
	}

	public void confirmToPaid(String dealAccountId) {
		this.setOrderState(Constant.商户订单状态_已支付);
		this.setConfirmTime(new Date());
		this.setDealTime(this.getConfirmTime());
		this.setDealAccountId(dealAccountId);
		this.setBounty(NumberUtil.round(this.getGatheringAmount() * this.getRebate() * 0.01, 2).doubleValue());
	}

	public void updateReceived(String receivedAccountId, String gatheringCodeId, String gatheringCodeStorageId,
			Double rebate) {
		this.setReceivedAccountId(receivedAccountId);
		this.setGatheringCodeId(gatheringCodeId);
		this.setGatheringCodeStorageId(gatheringCodeStorageId);
		this.setOrderState(Constant.商户订单状态_已接单);
		this.setReceivedTime(new Date());
		this.setRebate(rebate);
	}

	public void cancelOrderRefund(String dealAccountId) {
		this.setOrderState(Constant.商户订单状态_取消订单退款);
		this.setConfirmTime(new Date());
		this.setDealTime(this.getConfirmTime());
		this.setDealAccountId(dealAccountId);
	}

	public void updateUsefulTime(Date usefulTime) {
		this.setUsefulTime(usefulTime);
	}

}
