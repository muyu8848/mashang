package com.mashang.merchant.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.dictconfig.DictHolder;
import com.mashang.merchant.domain.MerchantOrder;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class ReceiveOrderRecordVO {

	private String id;

	private String orderNo;

	private String merchantOrderNo;

	private String gatheringChannelCode;

	private String gatheringChannelName;

	private Double gatheringAmount;

	private Double floatAmount;

	private String orderState;

	private String orderStateName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date receivedTime;

	private Double bounty;

	private String receivedAccountId;

	private String receiverUserName;

	private String ip;

	private String note;

	private String payerName;

	private String payerBankCardTail;

	private String payee;

	private String account;

	private String realName;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	public static List<ReceiveOrderRecordVO> convertFor(List<MerchantOrder> platformOrders) {
		if (CollectionUtil.isEmpty(platformOrders)) {
			return new ArrayList<>();
		}
		List<ReceiveOrderRecordVO> vos = new ArrayList<>();
		for (MerchantOrder platformOrder : platformOrders) {
			vos.add(convertFor(platformOrder));
		}
		return vos;
	}

	public static ReceiveOrderRecordVO convertFor(MerchantOrder merchantOrder) {
		if (merchantOrder == null) {
			return null;
		}
		ReceiveOrderRecordVO vo = new ReceiveOrderRecordVO();
		BeanUtils.copyProperties(merchantOrder, vo);
		vo.setGatheringChannelCode(merchantOrder.getGatheringChannel().getChannelCode());
		vo.setGatheringChannelName(merchantOrder.getGatheringChannel().getChannelName());
		vo.setOrderStateName(DictHolder.getDictItemName("merchantOrderState", vo.getOrderState()));
		if (StrUtil.isNotBlank(merchantOrder.getReceivedAccountId()) && merchantOrder.getReceivedAccount() != null) {
			vo.setReceiverUserName(merchantOrder.getReceivedAccount().getUserName());
		}
		if (merchantOrder.getGatheringCode() != null) {
			vo.setPayee(merchantOrder.getGatheringCode().getPayee());
			vo.setAccount(merchantOrder.getGatheringCode().getAccount());
			vo.setRealName(merchantOrder.getGatheringCode().getRealName());
			vo.setOpenAccountBank(merchantOrder.getGatheringCode().getOpenAccountBank());
			vo.setAccountHolder(merchantOrder.getGatheringCode().getAccountHolder());
			vo.setBankCardAccount(merchantOrder.getGatheringCode().getBankCardAccount());
		}
		return vo;
	}

}
