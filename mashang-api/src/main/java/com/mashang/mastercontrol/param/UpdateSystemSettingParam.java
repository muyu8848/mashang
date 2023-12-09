package com.mashang.mastercontrol.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateSystemSettingParam {

	@NotBlank
	private String websiteTitle;

	@NotBlank
	private String homePageUrl;
	
	private String appUrl;
	
	@NotBlank
	private String localStoragePath;

	@NotBlank
	private String currencyUnit;

	@NotNull
	private Boolean merchantLoginGoogleAuth;

	@NotNull
	private Boolean backgroundLoginGoogleAuth;
	
	@NotNull
	private Boolean memberClientLoginGoogleAuth;

}
