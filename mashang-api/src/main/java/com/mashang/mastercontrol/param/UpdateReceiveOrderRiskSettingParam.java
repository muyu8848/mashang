package com.mashang.mastercontrol.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateReceiveOrderRiskSettingParam {

	@NotNull
	private Boolean auditGatheringCode;

	@NotNull
	private Boolean banReceiveRepeatOrder;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Integer noOpsStopReceiveOrder;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Integer sameIpOrderNum;
	
	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Integer sameRealNameOrderNum;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Integer waitConfirmOrderUpperLimit;

	@NotNull
	private Boolean floatAmountMode;

	@NotBlank
	private String floatAmountDirection;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Integer minFloatAmount;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Integer maxFloatAmount;

}
