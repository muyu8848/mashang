package com.mashang.mastercontrol.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.mastercontrol.domain.CustomerServiceSetting;

import lombok.Data;

@Data
public class CustomerServiceSettingVO {

	private String customerServiceExplain;

	public static CustomerServiceSettingVO convertFor(CustomerServiceSetting setting) {
		if (setting == null) {
			return null;
		}
		CustomerServiceSettingVO vo = new CustomerServiceSettingVO();
		BeanUtils.copyProperties(setting, vo);
		return vo;
	}

}
