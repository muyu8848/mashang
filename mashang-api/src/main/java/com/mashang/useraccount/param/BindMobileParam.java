package com.mashang.useraccount.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class BindMobileParam {

	@NotBlank
	private String mobile;

	@NotBlank
	private String smsCode;

	@NotBlank
	private String userAccountId;

}
