package com.mashang.merchant.param;

import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.mashang.common.utils.IdUtils;
import com.mashang.merchant.domain.MerchantFreezeFundRecord;

import lombok.Data;

@Data
public class FreezeFundParam {
	
	@NotBlank
	private String merchantId;
	
	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double freezeFund;
	
	@NotBlank
	private String note;
	
	public MerchantFreezeFundRecord convertToPo() {
		MerchantFreezeFundRecord po = new MerchantFreezeFundRecord();
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setCreateTime(new Date());
		po.setReleaseFlag(false);
		po.setFreezeFund(freezeFund);
		po.setNote(note);
		po.setMerchantId(merchantId);
		return po;
	}

}
