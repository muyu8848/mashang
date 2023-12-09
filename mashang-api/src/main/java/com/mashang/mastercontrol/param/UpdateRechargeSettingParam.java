package com.mashang.mastercontrol.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateRechargeSettingParam {

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double rechargeLowerLimit;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double rechargeUpperLimit;

	@NotBlank
	private String quickInputAmount;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double usdtCnyExchangeRate;

	@NotNull
	private Boolean autoUpdateUsdtCnyExchangeRate;

	@NotBlank
	private String rechargeExplain;

	@NotNull
	private Boolean cantContinuousSubmit;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Double serviceProviderRechargeIncomeRate;

}
