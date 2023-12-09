package com.mashang.distributepayout.vo;

import lombok.Data;

@Data
public class DistributePayoutOrderSummaryVO {

	private Double successAmount;

	private Long successOrderNum;

	private Double memberIncome;

	public static DistributePayoutOrderSummaryVO build(Double successAmount, Long successOrderNum,
			Double memberIncome) {
		DistributePayoutOrderSummaryVO vo = new DistributePayoutOrderSummaryVO();
		vo.setSuccessAmount(successAmount);
		vo.setSuccessOrderNum(successOrderNum);
		vo.setMemberIncome(memberIncome);
		return vo;
	}

}
