package com.mashang.merchant.vo;

import com.mashang.merchant.domain.MerchantOrder;

import lombok.Data;

@Data
public class StartOrderSuccessVO {

	private String id;

	private String payUrl;

	public static StartOrderSuccessVO convertFor(MerchantOrder merchantOrder, String homePageUrl, String payUrl) {
		StartOrderSuccessVO vo = new StartOrderSuccessVO();
		vo.setId(merchantOrder.getId());
		vo.setPayUrl(homePageUrl + payUrl + "?orderNo=" + merchantOrder.getOrderNo());
		return vo;
	}

}
