package com.mashang.mastercontrol.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.mastercontrol.domain.SystemSetting;

import lombok.Data;

@Data
public class SystemSettingVO {

	private String websiteTitle;

	private String homePageUrl;
	
	private String appUrl;
	
	private String localStoragePath;

	private String currencyUnit;

	private Boolean merchantLoginGoogleAuth;

	private Boolean backgroundLoginGoogleAuth;
	
	private Boolean memberClientLoginGoogleAuth;

	public static SystemSettingVO convertFor(SystemSetting setting) {
		if (setting == null) {
			return null;
		}
		SystemSettingVO vo = new SystemSettingVO();
		BeanUtils.copyProperties(setting, vo);
		return vo;
	}

}
