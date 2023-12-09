package com.mashang.rechargewithdraw.param;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.rechargewithdraw.domain.ServiceProviderRechargeChannel;

import lombok.Data;

@Data
public class AddOrUpdateServiceProviderRechargeChannelParam {

	private String id;
	
	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	public ServiceProviderRechargeChannel convertToPo() {
		ServiceProviderRechargeChannel po = new ServiceProviderRechargeChannel();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setDeletedFlag(false);
		return po;
	}

}
