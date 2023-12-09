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
@Table(name = "receive_order_risk_setting")
@DynamicInsert(true)
@DynamicUpdate(true)
public class ReceiveOrderRiskSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Boolean auditGatheringCode;

	private Boolean banReceiveRepeatOrder;
	
	private Integer noOpsStopReceiveOrder;
	
	private Integer sameIpOrderNum;
	
	private Integer sameRealNameOrderNum;

	private Integer waitConfirmOrderUpperLimit;

	private Boolean floatAmountMode;

	private String floatAmountDirection;

	private Integer minFloatAmount;

	private Integer maxFloatAmount;

	private Date latelyUpdateTime;

	public static ReceiveOrderRiskSetting build() {
		ReceiveOrderRiskSetting setting = new ReceiveOrderRiskSetting();
		setting.setId(IdUtils.getId());
		return setting;
	}

}
