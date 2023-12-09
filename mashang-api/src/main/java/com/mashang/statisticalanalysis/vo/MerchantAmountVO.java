package com.mashang.statisticalanalysis.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.statisticalanalysis.domain.MerchantAmount;

import lombok.Data;

@Data
public class MerchantAmountVO {

	private Double totalMerchantAmount;

	private Double totalMerchantFreezeFund;

	public static MerchantAmountVO convertFor(MerchantAmount po) {
		if (po == null) {
			return null;
		}
		MerchantAmountVO vo = new MerchantAmountVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
