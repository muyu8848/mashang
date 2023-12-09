package com.mashang.rechargewithdraw.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.mashang.rechargewithdraw.domain.ServiceProviderRechargeChannel;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class ServiceProviderRechargeChannelVO {

	private String id;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	public static List<ServiceProviderRechargeChannelVO> convertFor(List<ServiceProviderRechargeChannel> channels) {
		if (CollectionUtil.isEmpty(channels)) {
			return new ArrayList<>();
		}
		List<ServiceProviderRechargeChannelVO> vos = new ArrayList<>();
		for (ServiceProviderRechargeChannel channel : channels) {
			vos.add(convertFor(channel));
		}
		return vos;
	}

	public static ServiceProviderRechargeChannelVO convertFor(ServiceProviderRechargeChannel channel) {
		if (channel == null) {
			return null;
		}
		ServiceProviderRechargeChannelVO vo = new ServiceProviderRechargeChannelVO();
		BeanUtils.copyProperties(channel, vo);
		return vo;
	}

}
