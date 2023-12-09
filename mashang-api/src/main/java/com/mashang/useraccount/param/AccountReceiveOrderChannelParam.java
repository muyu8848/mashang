package com.mashang.useraccount.param;

import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.useraccount.domain.AccountReceiveOrderChannel;

import lombok.Data;

@Data
public class AccountReceiveOrderChannelParam {
	
	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Double rebate;

	@NotBlank
	private String channelId;

	public AccountReceiveOrderChannel convertToPo() {
		AccountReceiveOrderChannel po = new AccountReceiveOrderChannel();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		return po;
	}

}
