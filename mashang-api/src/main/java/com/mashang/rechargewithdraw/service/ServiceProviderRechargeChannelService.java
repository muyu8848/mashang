package com.mashang.rechargewithdraw.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.mashang.common.valid.ParamValid;
import com.mashang.rechargewithdraw.domain.ServiceProviderRechargeChannel;
import com.mashang.rechargewithdraw.param.AddOrUpdateServiceProviderRechargeChannelParam;
import com.mashang.rechargewithdraw.repo.ServiceProviderRechargeChannelRepo;
import com.mashang.rechargewithdraw.vo.ServiceProviderRechargeChannelVO;

import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class ServiceProviderRechargeChannelService {

	@Autowired
	private ServiceProviderRechargeChannelRepo channelRepo;

	@Transactional(readOnly = true)
	public List<ServiceProviderRechargeChannelVO> findRechargeChannel(@NotBlank String userAccountId) {
		List<ServiceProviderRechargeChannel> channels = channelRepo
				.findByUserAccountIdInAndDeletedFlagIsFalse(Arrays.asList(userAccountId));
		return ServiceProviderRechargeChannelVO.convertFor(channels);
	}

	@Transactional(readOnly = true)
	public ServiceProviderRechargeChannelVO findRechargeChannelById(@NotBlank String id,
			@NotBlank String userAccountId) {
		return ServiceProviderRechargeChannelVO.convertFor(channelRepo.findByIdAndUserAccountId(id, userAccountId));
	}

	@ParamValid
	@Transactional
	public void addOrUpdateRechargeChannel(AddOrUpdateServiceProviderRechargeChannelParam param, String userAccountId) {
		// 新增
		if (StrUtil.isBlank(param.getId())) {
			ServiceProviderRechargeChannel channel = param.convertToPo();
			channel.setUserAccountId(userAccountId);
			channelRepo.save(channel);
		}
		// 修改
		else {
			ServiceProviderRechargeChannel channel = channelRepo.findByIdAndUserAccountId(param.getId(), userAccountId);
			BeanUtils.copyProperties(param, channel);
			channel.setLatelyModifyTime(new Date());
			channelRepo.save(channel);
		}
	}

	@Transactional
	public void delRechargeChannelById(@NotBlank String id, @NotBlank String userAccountId) {
		ServiceProviderRechargeChannel channel = channelRepo.findByIdAndUserAccountId(id, userAccountId);
		channel.deleted();
		channelRepo.save(channel);
	}

}
