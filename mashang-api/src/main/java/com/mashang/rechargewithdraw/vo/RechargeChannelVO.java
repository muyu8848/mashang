package com.mashang.rechargewithdraw.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.mashang.rechargewithdraw.domain.RechargeChannel;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class RechargeChannelVO {

	private String id;

	private String channelType;

	private String channelName;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	private String addressType;

	private String address;

	private Double orderNo;

	private Boolean enabled;

	public static List<RechargeChannelVO> convertFor(List<RechargeChannel> channels) {
		if (CollectionUtil.isEmpty(channels)) {
			return new ArrayList<>();
		}
		List<RechargeChannelVO> vos = new ArrayList<>();
		for (RechargeChannel channel : channels) {
			vos.add(convertFor(channel));
		}
		return vos;
	}

	public static RechargeChannelVO convertFor(RechargeChannel channel) {
		if (channel == null) {
			return null;
		}
		RechargeChannelVO vo = new RechargeChannelVO();
		BeanUtils.copyProperties(channel, vo);
		return vo;
	}

}
