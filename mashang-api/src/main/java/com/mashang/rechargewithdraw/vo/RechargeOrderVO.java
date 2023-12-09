package com.mashang.rechargewithdraw.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.dictconfig.DictHolder;
import com.mashang.rechargewithdraw.domain.RechargeOrder;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class RechargeOrderVO {

	private String id;

	@Excel(name = "订单号", orderNum = "1")
	private String orderNo;

	private Double usdtQuantity;

	private Double usdtCnyExchangeRate;

	@Excel(name = "充值金额", orderNum = "5")
	private Double rechargeAmount;

	@Excel(name = "提交时间", orderNum = "7", format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date submitTime;

	private String orderState;

	@Excel(name = "订单状态", orderNum = "3")
	private String orderStateName;

	private String note;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date dealTime;

	@Excel(name = "结算时间", orderNum = "9", format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date settlementTime;

	private String rechargeChannelType;

	private String rechargeChannelTypeName;

	private String rechargeWay;

	private String rechargeWayName;

	private Double serviceProviderIncome;

	private String serviceProviderUserName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
	private Date depositTime;

	private String depositor;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	private String tradeId;

	private String paymentVoucherId;

	private String addressType;

	private String address;

	private String userAccountId;

	@Excel(name = "充值账号", orderNum = "2")
	private String userName;

	public static List<RechargeOrderVO> convertFor(List<RechargeOrder> rechargeOrders) {
		if (CollectionUtil.isEmpty(rechargeOrders)) {
			return new ArrayList<>();
		}
		List<RechargeOrderVO> vos = new ArrayList<>();
		for (RechargeOrder rechargeOrder : rechargeOrders) {
			vos.add(convertFor(rechargeOrder));
		}
		return vos;
	}

	public static RechargeOrderVO convertFor(RechargeOrder rechargeOrder) {
		if (rechargeOrder == null) {
			return null;
		}
		RechargeOrderVO vo = new RechargeOrderVO();
		BeanUtils.copyProperties(rechargeOrder, vo);
		vo.setOrderStateName(DictHolder.getDictItemName("rechargeOrderState", vo.getOrderState()));
		vo.setRechargeWayName(DictHolder.getDictItemName("rechargeWay", vo.getRechargeWay()));
		vo.setRechargeChannelTypeName(DictHolder.getDictItemName("rechargeChannelType", vo.getRechargeChannelType()));
		if (rechargeOrder.getUserAccount() != null) {
			vo.setUserName(rechargeOrder.getUserAccount().getUserName());
		}
		if (rechargeOrder.getServiceProvider() != null) {
			vo.setServiceProviderUserName(rechargeOrder.getServiceProvider().getUserName());
		}
		return vo;
	}

}
