package com.mashang.mastercontrol.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.mastercontrol.domain.WithdrawSetting;

import lombok.Data;

@Data
public class WithdrawSettingVO {

	private Double withdrawRate;

	private Double minHandlingFee;

	private Integer everydayWithdrawTimesUpperLimit;

	private Double withdrawLowerLimit;

	private Double withdrawUpperLimit;

	private String withdrawExplain;

	public static WithdrawSettingVO convertFor(WithdrawSetting setting) {
		WithdrawSettingVO vo = new WithdrawSettingVO();
		if (setting != null) {
			BeanUtils.copyProperties(setting, vo);
		}
		return vo;
	}

}
