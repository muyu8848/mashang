package com.mashang.statisticalanalysis.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.statisticalanalysis.domain.CollectForAnotherIncomeSituation;
import com.mashang.statisticalanalysis.domain.PlatformIncome;

import lombok.Data;

@Data
public class IncomeVO {

	private Double totalIncome = 0d;

	private Double monthIncome = 0d;

	private Double yesterdayIncome = 0d;

	private Double todayIncome = 0d;
	
	public static IncomeVO convertForPlatform(PlatformIncome income) {
		IncomeVO vo = new IncomeVO();
		if (income != null) {
			BeanUtils.copyProperties(income, vo);
		}
		
		return vo;
	}

	public static IncomeVO convertForCollectForAnother(CollectForAnotherIncomeSituation income) {
		IncomeVO vo = new IncomeVO();
		if (income != null) {
			BeanUtils.copyProperties(income, vo);
		}
		return vo;
	}

}
