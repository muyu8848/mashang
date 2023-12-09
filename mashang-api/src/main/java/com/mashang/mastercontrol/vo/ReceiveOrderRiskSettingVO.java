package com.mashang.mastercontrol.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.mastercontrol.domain.ReceiveOrderRiskSetting;

import lombok.Data;

@Data
public class ReceiveOrderRiskSettingVO {

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

	public static ReceiveOrderRiskSettingVO convertFor(ReceiveOrderRiskSetting setting) {
		if (setting == null) {
			return null;
		}
		ReceiveOrderRiskSettingVO vo = new ReceiveOrderRiskSettingVO();
		BeanUtils.copyProperties(setting, vo);
		return vo;
	}

}
