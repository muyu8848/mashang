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
import com.mashang.merchant.domain.GatheringChannel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "account_receive_order_channel")
@DynamicInsert(true)
@DynamicUpdate(true)
public class AccountReceiveOrderChannel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Double rebate;

	private Date createTime;

	@Version
	private Long version;

	@Column(name = "user_account_id", length = 32)
	private String userAccountId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount userAccount;

	@Column(name = "channel_id", length = 32)
	private String channelId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "channel_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private GatheringChannel channel;

	public static AccountReceiveOrderChannel build(GatheringChannel gatheringChannel, String userAccountId) {
		AccountReceiveOrderChannel po = new AccountReceiveOrderChannel();
		po.setId(IdUtils.getId());
		po.setRebate(gatheringChannel.getDefaultReceiveOrderRabate());
		po.setChannelId(gatheringChannel.getId());
		po.setUserAccountId(userAccountId);
		po.setCreateTime(new Date());
		return po;
	}

	public static AccountReceiveOrderChannel build(Double rebate, String channelId, String userAccountId) {
		AccountReceiveOrderChannel po = new AccountReceiveOrderChannel();
		po.setId(IdUtils.getId());
		po.setRebate(rebate);
		po.setChannelId(channelId);
		po.setUserAccountId(userAccountId);
		po.setCreateTime(new Date());
		return po;
	}

}
