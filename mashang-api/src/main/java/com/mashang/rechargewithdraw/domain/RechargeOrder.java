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
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.mashang.constants.Constant;
import com.mashang.useraccount.domain.UserAccount;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "recharge_order")
@DynamicInsert(true)
@DynamicUpdate(true)
public class RechargeOrder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;

	private Double rechargeAmount;

	private Double usdtQuantity;

	private Double usdtCnyExchangeRate;

	private Date submitTime;

	private String orderState;

	private String note;

	private Date dealTime;

	private Date settlementTime;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	private Date depositTime;

	private String depositor;

	private String addressType;

	private String address;

	private String tradeId;

	private String paymentVoucherId;

	private String rechargeChannelType;

	private String rechargeWay;

	private Double serviceProviderIncome;

	@Version
	private Long version;

	@Column(name = "user_account_id", length = 32)
	private String userAccountId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount userAccount;

	@Column(name = "service_provider_id", length = 32)
	private String serviceProviderId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_provider_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount serviceProvider;

	public void paid() {
		this.setDealTime(new Date());
		this.setOrderState(Constant.充值订单状态_已支付);
	}

	public void settlement() {
		this.setSettlementTime(new Date());
	}

}
