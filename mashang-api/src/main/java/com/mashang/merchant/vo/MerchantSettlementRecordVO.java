package com.mashang.merchant.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.dictconfig.DictHolder;
import com.mashang.merchant.domain.MerchantSettlementRecord;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MerchantSettlementRecordVO {

	private String id;

	@Excel(name = "订单号", orderNum = "1")
	private String orderNo;

	@Excel(name = "提现金额", orderNum = "3")
	private Double withdrawAmount;

	private Double serviceFee;

	private Double actualToAccount;

	@Excel(name = "开户银行", orderNum = "4")
	private String openAccountBank;

	@Excel(name = "开户人", orderNum = "5")
	private String accountHolder;

	@Excel(name = "卡号", orderNum = "6")
	private String bankCardAccount;

	@Excel(name = "申请时间", orderNum = "8", format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date applyTime;

	private String state;

	@Excel(name = "状态", orderNum = "7")
	private String stateName;

	private String note;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date approvalTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date confirmCreditedTime;

	@Excel(name = "商户号", orderNum = "2")
	private String merchantNum;

	private String merchantName;

	private String receiverUserName;

	private String depositor;

	private String paymentVoucherId;

	public static List<MerchantSettlementRecordVO> convertFor(
			List<MerchantSettlementRecord> merchantSettlementRecords) {
		if (CollectionUtil.isEmpty(merchantSettlementRecords)) {
			return new ArrayList<>();
		}
		List<MerchantSettlementRecordVO> vos = new ArrayList<>();
		for (MerchantSettlementRecord merchantSettlementRecord : merchantSettlementRecords) {
			vos.add(convertFor(merchantSettlementRecord));
		}
		return vos;
	}

	public static MerchantSettlementRecordVO convertFor(MerchantSettlementRecord merchantSettlementRecord) {
		if (merchantSettlementRecord == null) {
			return null;
		}
		MerchantSettlementRecordVO vo = new MerchantSettlementRecordVO();
		BeanUtils.copyProperties(merchantSettlementRecord, vo);
		vo.setStateName(DictHolder.getDictItemName("merchantSettlementState", vo.getState()));
		if (merchantSettlementRecord.getMerchant() != null) {
			vo.setMerchantNum(merchantSettlementRecord.getMerchant().getUserName());
			vo.setMerchantName(merchantSettlementRecord.getMerchant().getMerchantName());
		}
		if (merchantSettlementRecord.getDistributePayoutOrder() != null) {
			vo.setDepositor(merchantSettlementRecord.getDistributePayoutOrder().getDepositor());
			vo.setPaymentVoucherId(merchantSettlementRecord.getDistributePayoutOrder().getPaymentVoucherId());
			vo.setReceiverUserName(
					merchantSettlementRecord.getDistributePayoutOrder().getReceivedAccount().getUserName());
		}
		return vo;
	}

}
