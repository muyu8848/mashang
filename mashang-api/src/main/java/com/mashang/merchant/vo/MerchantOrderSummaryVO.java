package com.mashang.merchant.vo;

import lombok.Data;

@Data
public class MerchantOrderSummaryVO {

	private Double successAmount;

	private Long successOrderNum;

	private Double merchantAgentIncome;

	private Double merchantIncome;

	private Double handlingFee;

	private Double merchantAgentHandlingFee;

	private Double memberIncome;

	private Double memberTeamTotalIncome;

	public static MerchantOrderSummaryVO build(Double successAmount, Long successOrderNum, Double merchantAgentIncome,
			Double merchantIncome, Double handlingFee, Double merchantAgentHandlingFee, Double memberIncome,
			Double memberTeamTotalIncome) {
		MerchantOrderSummaryVO vo = new MerchantOrderSummaryVO();
		vo.setSuccessAmount(successAmount);
		vo.setSuccessOrderNum(successOrderNum);
		vo.setMerchantAgentIncome(merchantAgentIncome);
		vo.setMerchantIncome(merchantIncome);
		vo.setHandlingFee(handlingFee);
		vo.setMerchantAgentHandlingFee(merchantAgentHandlingFee);
		vo.setMemberIncome(memberIncome);
		vo.setMemberTeamTotalIncome(memberTeamTotalIncome);
		return vo;
	}

}
