package com.mashang.statisticalanalysis.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.mashang.statisticalanalysis.domain.merchant.MerchantChannelTradeSituation;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MerchantChannelTradeSituationVO {

	private String id;

	private String merchantId;

	private String merchantName;

	private String channelId;

	private String channelName;
	
	private Double tradeAmount;

	private Double poundage;

	private Double actualIncome;

	private Long paidOrderNum;

	private Long orderNum;

	private Double successRate;

	private Double totalTradeAmount;

	private Double totalPoundage;

	private Double totalActualIncome;

	private Long totalPaidOrderNum;

	private Long totalOrderNum;

	private Double totalSuccessRate;

	private Double monthTradeAmount;

	private Double monthPoundage;

	private Double monthActualIncome;

	private Long monthPaidOrderNum;

	private Long monthOrderNum;

	private Double monthSuccessRate;

	private Double yesterdayTradeAmount;

	private Double yesterdayPoundage;

	private Double yesterdayActualIncome;

	private Long yesterdayPaidOrderNum;

	private Long yesterdayOrderNum;

	private Double yesterdaySuccessRate;

	private Double todayTradeAmount;

	private Double todayPoundage;

	private Double todayActualIncome;

	private Long todayPaidOrderNum;

	private Long todayOrderNum;

	private Double todaySuccessRate;

	public static List<MerchantChannelTradeSituationVO> convertFor(List<MerchantChannelTradeSituation> situations) {
		if (CollectionUtil.isEmpty(situations)) {
			return new ArrayList<>();
		}
		List<MerchantChannelTradeSituationVO> vos = new ArrayList<>();
		for (MerchantChannelTradeSituation situation : situations) {
			vos.add(convertFor(situation));
		}
		return vos;
	}

	public static MerchantChannelTradeSituationVO convertFor(MerchantChannelTradeSituation situation) {
		if (situation == null) {
			return null;
		}
		MerchantChannelTradeSituationVO vo = new MerchantChannelTradeSituationVO();
		BeanUtils.copyProperties(situation, vo);
		return vo;
	}
	
	public static MerchantChannelTradeSituationVO build(String id, String channelName) {
		MerchantChannelTradeSituationVO vo = new MerchantChannelTradeSituationVO();
		vo.setChannelId(id);
		vo.setChannelName(channelName);
		vo.setTradeAmount(0d);
		vo.setPoundage(0d);
		vo.setActualIncome(0d);
		vo.setPaidOrderNum(0L);
		vo.setOrderNum(0L);
		vo.setSuccessRate(0d);
		return vo;
	}

}
