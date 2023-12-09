package com.mashang.merchant.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.merchant.domain.Merchant;

import lombok.Data;

@Data
public class MerchantEditParam {

	private String id;

	@NotBlank
	private String userName;

	@NotBlank
	private String merchantName;

	@NotBlank
	private String secretKey;

	@NotBlank
	private String state;

	public Merchant convertToPo() {
		Merchant po = new Merchant();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		return po;
	}

}
