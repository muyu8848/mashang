package com.mashang.distributepayout.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.dictconfig.DictHolder;
import com.mashang.distributepayout.domain.DistributePayoutOrder;
import com.mashang.merchant.domain.Merchant;
import com.mashang.merchant.domain.MerchantSettlementRecord;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class DistributePayoutOrderVO {

	private String id;

	private String orderNo;

	private String merchantSettlementRecordId;

	private String merchantNum;

	private String merchantName;

	private String note;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date receivedTime;

	private String receiverUserName;

	private String receiverRealName;

	private Double amount;

	private Double memberIncome;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	private String orderState;

	private String orderStateName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date dealTime;

	private String dealAccountId;

	private String dealAccountUserName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date settlementTime;

	private String paymentVoucherId;

	private String depositor;

	public static List<DistributePayoutOrderVO> convertFor(List<DistributePayoutOrder> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<DistributePayoutOrderVO> vos = new ArrayList<>();
		for (DistributePayoutOrder po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static DistributePayoutOrderVO convertFor(DistributePayoutOrder order) {
		if (order == null) {
			return null;
		}
		DistributePayoutOrderVO vo = new DistributePayoutOrderVO();
		BeanUtils.copyProperties(order, vo);
		vo.setOrderStateName(DictHolder.getDictItemName("distributePayoutOrderState", vo.getOrderState()));
		if (order.getReceivedAccount() != null) {
			vo.setReceiverRealName(order.getReceivedAccount().getRealName());
			vo.setReceiverUserName(order.getReceivedAccount().getUserName());
		}
		if (order.getDealAccount() != null) {
			vo.setDealAccountUserName(order.getDealAccount().getUserName());
		}
		MerchantSettlementRecord merchantSettlementRecord = order.getMerchantSettlementRecord();
		if (merchantSettlementRecord != null) {
			Merchant merchant = merchantSettlementRecord.getMerchant();
			if (merchant != null) {
				vo.setMerchantNum(merchant.getUserName());
				vo.setMerchantName(merchant.getMerchantName());
			}
		}
		return vo;
	}

}
