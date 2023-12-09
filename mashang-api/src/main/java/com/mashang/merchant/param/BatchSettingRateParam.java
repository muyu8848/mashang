package com.mashang.merchant.param;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class BatchSettingRateParam {

	@NotBlank
	private String merchantId;

	private List<GatheringChannelRateParam> channelRates;
}
