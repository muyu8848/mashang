package com.mashang.useraccount.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.mashang.common.exception.BizError;
import com.mashang.common.exception.BizException;
import com.mashang.useraccount.domain.AccountReceiveOrderChannel;
import com.mashang.useraccount.param.AccountReceiveOrderChannelParam;
import com.mashang.useraccount.param.SaveAccountReceiveOrderChannelParam;
import com.mashang.useraccount.repo.AccountReceiveOrderChannelRepo;
import com.mashang.useraccount.vo.AccountReceiveOrderChannelVO;

@Validated
@Service
public class AccountChannelRebateService {

	@Autowired
	private AccountReceiveOrderChannelRepo accountReceiveOrderChannelRepo;

	@Transactional(readOnly = true)
	public List<AccountReceiveOrderChannelVO> findAccountReceiveOrderChannelByAccountId(
			@NotBlank String userAccountId) {
		List<AccountReceiveOrderChannel> channels = accountReceiveOrderChannelRepo
				.findByUserAccountIdAndChannelDeletedFlagFalse(userAccountId);
		return AccountReceiveOrderChannelVO.convertFor(channels);
	}

	@Transactional
	public void saveAccountReceiveOrderChannel(SaveAccountReceiveOrderChannelParam param) {
		Map<String, String> map = new HashMap<>();
		for (AccountReceiveOrderChannelParam channelParam : param.getChannels()) {
			if (map.get(channelParam.getChannelId()) != null) {
				throw new BizException(BizError.不能设置重复的接单通道);
			}
			map.put(channelParam.getChannelId(), channelParam.getChannelId());
		}

		List<AccountReceiveOrderChannel> channels = accountReceiveOrderChannelRepo
				.findByUserAccountId(param.getUserAccountId());
		accountReceiveOrderChannelRepo.deleteAll(channels);
		for (AccountReceiveOrderChannelParam channelParam : param.getChannels()) {
			AccountReceiveOrderChannel po = channelParam.convertToPo();
			po.setUserAccountId(param.getUserAccountId());
			accountReceiveOrderChannelRepo.save(po);
		}
	}

}
