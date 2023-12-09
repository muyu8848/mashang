package com.mashang.useraccount.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AdjustLowerLevelPointParam {

	private String id;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Double rebate;

}
