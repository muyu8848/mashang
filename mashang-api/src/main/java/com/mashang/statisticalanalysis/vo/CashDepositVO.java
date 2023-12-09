package com.mashang.statisticalanalysis.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.statisticalanalysis.domain.CashDeposit;

import lombok.Data;

@Data
public class CashDepositVO {

	private Double totalCashDeposit;

	private Double totalBounty;

	private Double monthBounty;

	private Double yesterdayBounty;

	private Double todayBounty;

	public static CashDepositVO convertFor(CashDeposit cashDepositBounty) {
		if (cashDepositBounty == null) {
			return null;
		}
		CashDepositVO vo = new CashDepositVO();
		BeanUtils.copyProperties(cashDepositBounty, vo);
		return vo;
	}

}
