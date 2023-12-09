package com.mashang.merchant.vo;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.dictconfig.DictHolder;
import com.mashang.merchant.domain.MerchantOrder;

import lombok.Data;

@Data
public class OrderGatheringCodeVO {

	private String id;

	private String orderNo;

	private String gatheringChannelCode;

	private String gatheringChannelName;

	private Double gatheringAmount;

	private Double floatAmount;

	private String orderState;

	private String orderStateName;
	
	private String payerName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date submitTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date usefulTime;

	private String returnUrl;

	private String gatheringCodeStorageId;

	private String payee;

	private String codeContent;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	private String mobile;

	private String realName;

	private String account;

	private String alipayId;
	
	private String address;

	public static OrderGatheringCodeVO convertFor(MerchantOrder merchantOrder) {
		if (merchantOrder == null) {
			return null;
		}
		OrderGatheringCodeVO vo = new OrderGatheringCodeVO();
		BeanUtils.copyProperties(merchantOrder, vo);
		vo.setOrderStateName(DictHolder.getDictItemName("merchantOrderState", vo.getOrderState()));
		if (merchantOrder.getGatheringChannel() != null) {
			vo.setGatheringChannelCode(merchantOrder.getGatheringChannel().getChannelCode());
			vo.setGatheringChannelName(merchantOrder.getGatheringChannel().getChannelName());
		}
		if (merchantOrder.getGatheringCode() != null) {
			vo.setPayee(merchantOrder.getGatheringCode().getPayee());
			vo.setCodeContent(merchantOrder.getGatheringCode().getCodeContent());
			vo.setOpenAccountBank(merchantOrder.getGatheringCode().getOpenAccountBank());
			vo.setAccountHolder(merchantOrder.getGatheringCode().getAccountHolder());
			vo.setBankCardAccount(merchantOrder.getGatheringCode().getBankCardAccount());
			vo.setMobile(merchantOrder.getGatheringCode().getMobile());
			vo.setRealName(merchantOrder.getGatheringCode().getRealName());
			vo.setAccount(merchantOrder.getGatheringCode().getAccount());
			vo.setAlipayId(merchantOrder.getGatheringCode().getAlipayId());
			vo.setAddress(merchantOrder.getGatheringCode().getAddress());
		}
		return vo;
	}

}
