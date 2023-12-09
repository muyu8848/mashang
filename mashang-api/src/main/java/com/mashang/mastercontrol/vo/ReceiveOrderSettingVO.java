package com.mashang.mastercontrol.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.mastercontrol.domain.ReceiveOrderSetting;

import lombok.Data;

@Data
public class ReceiveOrderSettingVO {

	private String id;

	private Boolean stopStartAndReceiveOrder;

	private Integer receiveOrderEffectiveDuration;

	private Integer orderPayEffectiveDuration;

	private Double cashDepositMinimumRequire;

	private Double cashPledge;
	
	private Boolean freezeMode;

	private Integer freezeEffectiveDuration;

	private Integer unconfirmedAutoFreezeDuration;

	public static ReceiveOrderSettingVO convertFor(ReceiveOrderSetting setting) {
		if (setting == null) {
			return null;
		}
		ReceiveOrderSettingVO vo = new ReceiveOrderSettingVO();
		BeanUtils.copyProperties(setting, vo);
		return vo;
	}

}
