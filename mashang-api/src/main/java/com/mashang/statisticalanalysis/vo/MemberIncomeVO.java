package com.mashang.statisticalanalysis.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.statisticalanalysis.domain.MemberIncome;

import lombok.Data;

@Data
public class MemberIncomeVO {

	private Double totalIncome = 0d;

	private Double yesterdayIncome = 0d;

	private Double todayIncome = 0d;

	public static MemberIncomeVO convertFor(MemberIncome memberIncome) {
		MemberIncomeVO vo = new MemberIncomeVO();
		if (memberIncome != null) {
			BeanUtils.copyProperties(memberIncome, vo);
		}
		return vo;
	}

}
