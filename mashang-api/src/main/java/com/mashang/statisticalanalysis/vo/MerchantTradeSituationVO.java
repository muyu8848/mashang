package com.mashang.statisticalanalysis.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.mashang.statisticalanalysis.domain.merchant.MerchantTradeSituation;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MerchantTradeSituationVO {

	private String id;

	private String userName;

	private String merchantName;

	private Double tradeAmount;

	private Double poundage;
	
	private Double merchantAgentHandlingFee;

	private Double actualIncome;

	private Long paidOrderNum;

	private Long orderNum;

	private Double successRate;

	private Double totalTradeAmount;

	private Double totalHandlingFee;

	private Double totalAgentIncome;

	private Double totalActualIncome;

	private Long totalPaidOrderNum;

	private Long totalOrderNum;

	private Double totalSuccessRate;

	private Double monthTradeAmount;

	private Double monthHandlingFee;

	private Double monthAgentIncome;

	private Double monthActualIncome;

	private Long monthPaidOrderNum;

	private Long monthOrderNum;

	private Double monthSuccessRate;

	private Double yesterdayTradeAmount;

	private Double yesterdayHandlingFee;

	private Double yesterdayAgentIncome;

	private Double yesterdayActualIncome;

	private Long yesterdayPaidOrderNum;

	private Long yesterdayOrderNum;

	private Double yesterdaySuccessRate;

	private Double todayTradeAmount;

	private Double todayHandlingFee;

	private Double todaydayAgentIncome;

	private Double todayActualIncome;

	private Long todayPaidOrderNum;

	private Long todayOrderNum;

	private Double todaySuccessRate;

	public static List<MerchantTradeSituationVO> convertFor(List<MerchantTradeSituation> situations) {
		if (CollectionUtil.isEmpty(situations)) {
			return new ArrayList<>();
		}
		List<MerchantTradeSituationVO> vos = new ArrayList<>();
		for (MerchantTradeSituation situation : situations) {
			vos.add(convertFor(situation));
		}
		return vos;
	}

	public static MerchantTradeSituationVO convertFor(MerchantTradeSituation situation) {
		MerchantTradeSituationVO vo = new MerchantTradeSituationVO();
		if (situation != null) {
			BeanUtils.copyProperties(situation, vo);
		}
		return vo;
	}

	public static MerchantTradeSituationVO build(String id, String userName, String merchantName) {
		MerchantTradeSituationVO vo = new MerchantTradeSituationVO();
		vo.setId(id);
		vo.setUserName(userName);
		vo.setMerchantName(merchantName);
		vo.setTradeAmount(0d);
		vo.setPoundage(0d);
		vo.setMerchantAgentHandlingFee(0d);
		vo.setActualIncome(0d);
		vo.setPaidOrderNum(0L);
		vo.setOrderNum(0L);
		vo.setSuccessRate(0d);
		return vo;
	}

}
