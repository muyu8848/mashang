package com.mashang.mastercontrol.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateRegisterSettingParam {
	
	@NotNull
	private Boolean registerFun;
	
	@NotNull
	private Boolean inviteRegisterMode;
	
	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Integer loginFailBlacklist;

}
