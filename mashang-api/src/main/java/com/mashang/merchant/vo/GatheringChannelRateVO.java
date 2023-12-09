package com.mashang.merchant.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.merchant.domain.GatheringChannelRate;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class GatheringChannelRateVO {

	private String id;

	private Double rate;
	
	private Double minAmount;
	
	private Double maxAmount;

	private String channelId;
	
	private String channelCode;

	private String channelName;

	private String merchantId;

	private String merchantName;

	private Boolean enabled;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	public static List<GatheringChannelRateVO> convertFor(List<GatheringChannelRate> rates) {
		if (CollectionUtil.isEmpty(rates)) {
			return new ArrayList<>();
		}
		List<GatheringChannelRateVO> vos = new ArrayList<>();
		for (GatheringChannelRate rate : rates) {
			vos.add(convertFor(rate));
		}
		return vos;
	}

	public static GatheringChannelRateVO convertFor(GatheringChannelRate rate) {
		if (rate == null) {
			return null;
		}
		GatheringChannelRateVO vo = new GatheringChannelRateVO();
		BeanUtils.copyProperties(rate, vo);
		if (rate.getChannel() != null) {
			vo.setChannelCode(rate.getChannel().getChannelCode());
			vo.setChannelName(rate.getChannel().getChannelName());
		}
		if (rate.getMerchant() != null) {
			vo.setMerchantName(rate.getMerchant().getMerchantName());
		}
		return vo;
	}

}
