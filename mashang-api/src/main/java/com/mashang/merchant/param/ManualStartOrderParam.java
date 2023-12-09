package com.mashang.merchant.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ManualStartOrderParam {

	@NotBlank
	private String merchantNum;

	@NotBlank
	private String gatheringChannelCode;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double gatheringAmount;

	@NotBlank
	private String orderNo;

	@NotBlank
	private String notifyUrl;

	private String returnUrl;

	private String attch;

	private String sign;

}
