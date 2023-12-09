package com.mashang.merchant.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.merchant.domain.GatheringChannel;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class GatheringChannelVO {

	private String id;

	private String channelCode;

	private String channelName;
	
	private Double defaultReceiveOrderRabate;
	
	private Boolean addGatheringCodeSetLimit;
	
	private String fiexdAmount;
	
	private String payUrl;

	private Boolean enabled;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	public static List<GatheringChannelVO> convertFor(List<GatheringChannel> gatheringChannels) {
		if (CollectionUtil.isEmpty(gatheringChannels)) {
			return new ArrayList<>();
		}
		List<GatheringChannelVO> vos = new ArrayList<>();
		for (GatheringChannel gatheringChannel : gatheringChannels) {
			vos.add(convertFor(gatheringChannel));
		}
		return vos;
	}

	public static GatheringChannelVO convertFor(GatheringChannel gatheringChannel) {
		if (gatheringChannel == null) {
			return null;
		}
		GatheringChannelVO vo = new GatheringChannelVO();
		BeanUtils.copyProperties(gatheringChannel, vo);
		return vo;
	}

}
