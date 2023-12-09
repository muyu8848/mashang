package com.mashang.mastercontrol.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.mastercontrol.domain.RechargeSetting;

import lombok.Data;

@Data
public class RechargeSettingVO {

	private Double rechargeLowerLimit;

	private Double rechargeUpperLimit;

	private String quickInputAmount;

	private Double usdtCnyExchangeRate;

	private Boolean autoUpdateUsdtCnyExchangeRate;

	private String rechargeExplain;

	private Boolean cantContinuousSubmit;

	private Double serviceProviderRechargeIncomeRate;

	public static RechargeSettingVO convertFor(RechargeSetting rechargeSetting) {
		RechargeSettingVO vo = new RechargeSettingVO();
		if (rechargeSetting != null) {
			BeanUtils.copyProperties(rechargeSetting, vo);
		}
		return vo;
	}

}
