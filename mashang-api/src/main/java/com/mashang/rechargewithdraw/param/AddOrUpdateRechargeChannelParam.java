package com.mashang.rechargewithdraw.param;

import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.rechargewithdraw.domain.RechargeChannel;

import lombok.Data;

@Data
public class AddOrUpdateRechargeChannelParam {

	private String id;

	@NotBlank
	private String channelType;

	@NotBlank
	private String channelName;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	private String addressType;

	private String address;

	@NotNull
	@DecimalMin(value = "0", inclusive = true)
	private Double orderNo;

	@NotNull
	private Boolean enabled;

	public RechargeChannel convertToPo() {
		RechargeChannel po = new RechargeChannel();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setDeletedFlag(false);
		return po;
	}

}
