package com.mashang.mastercontrol.param;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateMerchantSettlementSettingParam {

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Double merchantSettlementRate;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Double minServiceFee;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double minAmount;

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double maxAmount;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Double distributePayoutIncomeRate;

}
