package com.mashang.mastercontrol.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UpdateCustomerServiceSettingParam {

	@NotBlank
	private String customerServiceExplain;

}
