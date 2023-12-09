package com.mashang.statisticalanalysis.vo;

import java.io.Serializable;

import org.springframework.beans.BeanUtils;

import com.mashang.statisticalanalysis.domain.TodayAccountReceiveOrderSituation;

import lombok.Data;

@Data
public class AccountReceiveOrderSituationVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String receivedAccountId;

	private String userName;

	private Double gatheringAmount;

	private Long orderNum;

	private Double paidAmount;

	private Double bounty;

	private Long paidOrderNum;

	private Double rebateAmount;

	private Double successRate;

	public static AccountReceiveOrderSituationVO convertForToday(TodayAccountReceiveOrderSituation situation) {
		if (situation == null) {
			return null;
		}
		AccountReceiveOrderSituationVO vo = new AccountReceiveOrderSituationVO();
		BeanUtils.copyProperties(situation, vo);
		return vo;
	}

}
