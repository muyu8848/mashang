package com.mashang.merchant.vo;

import lombok.Data;

@Data
public class OrderInfoVO {

	private String merchantOrderNo;

	private String orderState;

	private String merchantNum;

	private Double amount;
	
private String payerName;
	
	private String payerBankCardTail;

	public static OrderInfoVO build(String merchantOrderNo, String orderState, String merchantNum, Double amount) {
		OrderInfoVO vo = new OrderInfoVO();
		vo.setMerchantOrderNo(merchantOrderNo);
		vo.setOrderState(orderState);
		vo.setMerchantNum(merchantNum);
		vo.setAmount(amount);
		return vo;
	}

}
