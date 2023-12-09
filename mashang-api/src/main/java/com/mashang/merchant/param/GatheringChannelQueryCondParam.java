package com.mashang.merchant.param;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GatheringChannelQueryCondParam extends PageParam {

	/**
	 * 通道code
	 */
	private String channelCode;

	/**
	 * 通道名称
	 */
	private String channelName;

}
