package com.mashang.mastercontrol.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.mastercontrol.domain.RegisterSetting;

import lombok.Data;

@Data
public class RegisterSettingVO {

	private Boolean registerFun;
	
	private Boolean inviteRegisterMode;
	
	private Integer loginFailBlacklist;

	public static RegisterSettingVO convertFor(RegisterSetting registerSetting) {
		if (registerSetting == null) {
			return null;
		}
		RegisterSettingVO vo = new RegisterSettingVO();
		BeanUtils.copyProperties(registerSetting, vo);
		return vo;
	}

}
