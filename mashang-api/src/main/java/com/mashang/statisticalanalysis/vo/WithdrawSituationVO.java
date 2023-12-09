package com.mashang.statisticalanalysis.vo;

import java.io.Serializable;

import org.springframework.beans.BeanUtils;

import com.mashang.statisticalanalysis.domain.WithdrawSituation;

import lombok.Data;

@Data
public class WithdrawSituationVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Double totalTradeAmount;

	private Long totalSuccessOrderNum;

	private Long totalOrderNum;

	private Double monthTradeAmount;

	private Long monthSuccessOrderNum;

	private Long monthOrderNum;

	private Double yesterdayTradeAmount;

	private Long yesterdaySuccessOrderNum;

	private Long yesterdayOrderNum;

	private Double todayTradeAmount;

	private Long todaySuccessOrderNum;

	private Long todayOrderNum;

	public static WithdrawSituationVO convertFor(WithdrawSituation withdrawSituation) {
		if (withdrawSituation == null) {
			return null;
		}
		WithdrawSituationVO vo = new WithdrawSituationVO();
		BeanUtils.copyProperties(withdrawSituation, vo);
		return vo;
	}

}
