package com.mashang.merchant.vo;

import lombok.Data;

@Data
public class MerchantSettlementRecordSummaryVO {

	private Double withdrawAmount;

	private Double serviceFee;

	private Double actualToAccount;

	public static MerchantSettlementRecordSummaryVO build(Double withdrawAmount, Double serviceFee,
			Double actualToAccount) {
		MerchantSettlementRecordSummaryVO vo = new MerchantSettlementRecordSummaryVO();
		vo.setWithdrawAmount(withdrawAmount);
		vo.setServiceFee(serviceFee);
		vo.setActualToAccount(actualToAccount);
		return vo;
	}

}
