package com.mashang.merchant.param;

import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.merchant.domain.GatheringChannel;

import lombok.Data;

@Data
public class AddOrUpdateGatheringChannelParam {

	private String id;

	@NotBlank
	private String channelCode;

	@NotBlank
	private String channelName;
	
	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Double defaultReceiveOrderRabate;
	
	@NotNull
	private Boolean addGatheringCodeSetLimit;
	
	private String fiexdAmount;
	
	@NotBlank
	private String payUrl;

	@NotNull
	private Boolean enabled;

	public GatheringChannel convertToPo() {
		GatheringChannel po = new GatheringChannel();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setDeletedFlag(false);
		return po;
	}

}
