package com.mashang.merchant.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.merchant.domain.MerchantOrder;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import lombok.Data;

@Data
public class MyWaitConfirmOrderVO {

	private String id;

	private String merchantOrderNo;

	private String gatheringChannelId;

	private String gatheringChannelCode;

	private String gatheringChannelName;

	private Double gatheringAmount;

	private Double floatAmount;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date receivedTime;
	
	private String payerName;

	private String attch;

	private String ip;

	private String gatheringCodeId;

	private String payee;
	
	private String account;
	
	private String realName;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;
	
	private String address;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date freezeTime;

	public static List<MyWaitConfirmOrderVO> convertFor(List<MerchantOrder> merchantOrders,
			Integer unconfirmedAutoFreezeDuration) {
		if (CollectionUtil.isEmpty(merchantOrders)) {
			return new ArrayList<>();
		}
		List<MyWaitConfirmOrderVO> vos = new ArrayList<>();
		for (MerchantOrder merchantOrder : merchantOrders) {
			vos.add(convertFor(merchantOrder, unconfirmedAutoFreezeDuration));
		}
		return vos;
	}

	public static MyWaitConfirmOrderVO convertFor(MerchantOrder merchantOrder, Integer unconfirmedAutoFreezeDuration) {
		if (merchantOrder == null) {
			return null;
		}
		MyWaitConfirmOrderVO vo = new MyWaitConfirmOrderVO();
		BeanUtils.copyProperties(merchantOrder, vo);
		vo.setGatheringChannelCode(merchantOrder.getGatheringChannel().getChannelCode());
		vo.setGatheringChannelName(merchantOrder.getGatheringChannel().getChannelName());
		if (merchantOrder.getGatheringCode() != null) {
			vo.setPayee(merchantOrder.getGatheringCode().getPayee());
			vo.setAccount(merchantOrder.getGatheringCode().getAccount());
			vo.setRealName(merchantOrder.getGatheringCode().getRealName());
			vo.setOpenAccountBank(merchantOrder.getGatheringCode().getOpenAccountBank());
			vo.setAccountHolder(merchantOrder.getGatheringCode().getAccountHolder());
			vo.setBankCardAccount(merchantOrder.getGatheringCode().getBankCardAccount());
			vo.setAddress(merchantOrder.getGatheringCode().getAddress());
		}
		if (unconfirmedAutoFreezeDuration != null) {
			vo.setFreezeTime(
					DateUtil.offset(merchantOrder.getReceivedTime(), DateField.MINUTE, unconfirmedAutoFreezeDuration)
							.toJdkDate());
		}
		return vo;
	}

}
