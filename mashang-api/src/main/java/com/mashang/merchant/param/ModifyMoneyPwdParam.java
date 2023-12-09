package com.mashang.merchant.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ModifyMoneyPwdParam {

	@NotBlank
	private String oldMoneyPwd;

	@NotBlank
	private String newMoneyPwd;

	@NotBlank
	private String merchantId;

}
