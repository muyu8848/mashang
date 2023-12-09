package com.mashang.useraccount.vo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.mashang.useraccount.domain.AccountReceiveOrderChannel;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class AccountReceiveOrderChannelVO {

	private String id;

	private Double rebate;

	private String userAccountId;

	private String channelId;

	private String channelCode;

	private String channelName;

	private Boolean addGatheringCodeSetLimit;
	
	private String fiexdAmount;

	public static List<AccountReceiveOrderChannelVO> convertFor(List<AccountReceiveOrderChannel> rebates) {
		if (CollectionUtil.isEmpty(rebates)) {
			return new ArrayList<>();
		}
		List<AccountReceiveOrderChannelVO> vos = new ArrayList<>();
		for (AccountReceiveOrderChannel rebate : rebates) {
			vos.add(convertFor(rebate));
		}
		return vos;
	}

	public static AccountReceiveOrderChannelVO convertFor(AccountReceiveOrderChannel rebate) {
		if (rebate == null) {
			return null;
		}
		AccountReceiveOrderChannelVO vo = new AccountReceiveOrderChannelVO();
		BeanUtils.copyProperties(rebate, vo);
		if (rebate.getChannel() != null) {
			vo.setChannelCode(rebate.getChannel().getChannelCode());
			vo.setChannelName(rebate.getChannel().getChannelName());
			vo.setAddGatheringCodeSetLimit(rebate.getChannel().getAddGatheringCodeSetLimit());
			vo.setFiexdAmount(rebate.getChannel().getFiexdAmount());
		}
		return vo;
	}

}
