package com.mashang.useraccount.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ExchangeQuotaParam {
	
	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double quantity;
	
	@NotBlank
	private String transferOutType;
	
	@NotBlank
	private String transferInType;
	
	@NotBlank
	private String userAccountId;

}
