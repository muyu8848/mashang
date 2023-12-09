package com.mashang.mastercontrol.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateUsdtRechargeSettingParam {

	@NotNull
	private Boolean usdtErc20RechargeMode;

	@NotNull
	private Boolean usdtTrc20RechargeMode;

	private String etherApiKey;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double usdtCnyExchangeRate;
	
	@NotNull
	private Boolean autoUpdateUsdtCnyExchangeRate;

	private String usdtErc20RechargeExplain;

	private String usdtTrc20RechargeExplain;

}
