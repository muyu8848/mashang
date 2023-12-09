package com.mashang.merchant.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.dictconfig.DictHolder;
import com.mashang.merchant.domain.MerchantOrder;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class MerchantOrderVO {

	private String id;

	@Excel(name = "订单号", orderNum = "1")
	private String orderNo;

	@Excel(name = "商户订单号", orderNum = "2")
	private String merchantOrderNo;

	private String gatheringChannelCode;

	@Excel(name = "通道", orderNum = "5")
	private String gatheringChannelName;

	@Excel(name = "金额", orderNum = "6")
	private Double gatheringAmount;

	private Double floatAmount;

	@Excel(name = "提交时间", orderNum = "12", format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date submitTime;

	@Excel(name = "订单有效时间", orderNum = "14", format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date usefulTime;

	private String orderState;

	@Excel(name = "订单状态", orderNum = "4")
	private String orderStateName;

	@Excel(name = "备注", orderNum = "16")
	private String note;

	private String payerName;
	
	private String payerBankCardTail;

	private String merchantId;

	@Excel(name = "商户号", orderNum = "2")
	private String merchantNum;

	private String merchantName;

	private String receivedAccountId;

	@Excel(name = "接单账号", orderNum = "10")
	private String receiverUserName;

	private String receiverMobile;

	@Excel(name = "接单时间", orderNum = "11", format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date receivedTime;

	private String gatheringCodeStorageId;

	@Excel(name = "系统处理时间", orderNum = "15", format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date dealTime;

	private String dealAccountId;

	private String dealAccountUserName;

	@Excel(name = "确认时间", orderNum = "14", format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date confirmTime;

	private String confirmWay;

	private String confirmWayName;

	@Excel(name = "费率", orderNum = "7")
	private Double rate;

	private Double merchantAgentRate;

	private Double handlingFee;

	private Double merchantAgentHandlingFee;

	private Double memberIncome;

	private Double memberTeamTotalIncome;

	@Excel(name = "支付地址", orderNum = "17")
	private String payUrl;

	@Excel(name = "异步通知地址", orderNum = "18")
	private String notifyUrl;

	@Excel(name = "同步通知地址", orderNum = "19")
	private String returnUrl;

	@Excel(name = "附加信息", orderNum = "20")
	private String attch;

	private String noticeState;

	@Excel(name = "通知状态", orderNum = "13")
	private String noticeStateName;

	@Excel(name = "通知时间", orderNum = "21", format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date noticeTime;

	private String ip;

	private String payee;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	private String mobile;

	private String realName;

	private String account;

	private String alipayId;

	private String address;

	public static List<MerchantOrderVO> convertFor(List<MerchantOrder> merchantOrders, String homePageUrl) {
		if (CollectionUtil.isEmpty(merchantOrders)) {
			return new ArrayList<>();
		}
		List<MerchantOrderVO> vos = new ArrayList<>();
		for (MerchantOrder merchantOrder : merchantOrders) {
			vos.add(convertFor(merchantOrder, homePageUrl));
		}
		return vos;
	}

	public static MerchantOrderVO convertFor(MerchantOrder merchantOrder, String homePageUrl) {
		if (merchantOrder == null) {
			return null;
		}
		MerchantOrderVO vo = new MerchantOrderVO();
		BeanUtils.copyProperties(merchantOrder, vo);
		vo.setGatheringChannelCode(merchantOrder.getGatheringChannel().getChannelCode());
		vo.setGatheringChannelName(merchantOrder.getGatheringChannel().getChannelName());
		vo.setOrderStateName(DictHolder.getDictItemName("merchantOrderState", vo.getOrderState()));
		vo.setConfirmWayName(DictHolder.getDictItemName("merchantOrderConfirmWay", vo.getConfirmWay()));
		vo.setNoticeStateName(DictHolder.getDictItemName("noticeState", vo.getNoticeState()));
		if (merchantOrder.getMerchant() != null) {
			vo.setMerchantNum(merchantOrder.getMerchant().getUserName());
			vo.setMerchantName(merchantOrder.getMerchant().getMerchantName());
		}
		if (merchantOrder.getDealAccount() != null) {
			vo.setDealAccountUserName(merchantOrder.getDealAccount().getUserName());
		}
		if (StrUtil.isNotBlank(merchantOrder.getReceivedAccountId()) && merchantOrder.getReceivedAccount() != null) {
			vo.setReceiverMobile(merchantOrder.getReceivedAccount().getMobile());
			vo.setReceiverUserName(merchantOrder.getReceivedAccount().getUserName());
			if (merchantOrder.getGatheringCode() != null) {
				vo.setPayee(merchantOrder.getGatheringCode().getPayee());
				vo.setOpenAccountBank(merchantOrder.getGatheringCode().getOpenAccountBank());
				vo.setAccountHolder(merchantOrder.getGatheringCode().getAccountHolder());
				vo.setBankCardAccount(merchantOrder.getGatheringCode().getBankCardAccount());
				vo.setMobile(merchantOrder.getGatheringCode().getMobile());
				vo.setRealName(merchantOrder.getGatheringCode().getRealName());
				vo.setAccount(merchantOrder.getGatheringCode().getAccount());
				vo.setAlipayId(merchantOrder.getGatheringCode().getAlipayId());
				vo.setAddress(merchantOrder.getGatheringCode().getAddress());
			}
		}
		vo.setPayUrl(homePageUrl + merchantOrder.getGatheringChannel().getPayUrl() + "?orderNo=" + vo.getOrderNo());
		return vo;
	}

}
