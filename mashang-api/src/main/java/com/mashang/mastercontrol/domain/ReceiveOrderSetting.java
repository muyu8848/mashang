package com.mashang.mastercontrol.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.mashang.common.utils.IdUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "receive_order_setting")
@DynamicInsert(true)
@DynamicUpdate(true)
public class ReceiveOrderSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Boolean stopStartAndReceiveOrder;

	private Integer receiveOrderEffectiveDuration;

	private Integer orderPayEffectiveDuration;

	private Double cashDepositMinimumRequire;

	private Double cashPledge;
	
	private Boolean freezeMode;

	private Integer freezeEffectiveDuration;

	private Integer unconfirmedAutoFreezeDuration;

	private Date latelyUpdateTime;

	public static ReceiveOrderSetting build() {
		ReceiveOrderSetting setting = new ReceiveOrderSetting();
		setting.setId(IdUtils.getId());
		return setting;
	}

}
