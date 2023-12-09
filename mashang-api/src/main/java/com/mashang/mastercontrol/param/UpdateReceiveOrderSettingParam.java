package com.mashang.mastercontrol.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateReceiveOrderSettingParam {

	@NotNull
	private Boolean stopStartAndReceiveOrder;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Integer receiveOrderEffectiveDuration;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Integer orderPayEffectiveDuration;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Double cashDepositMinimumRequire;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Double cashPledge;
	
	@NotNull
	private Boolean freezeMode;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Integer freezeEffectiveDuration;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Integer unconfirmedAutoFreezeDuration;

}
