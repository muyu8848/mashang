package com.mashang.merchant.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.merchant.domain.Merchant;

import lombok.Data;

@Data
public class LoginMerchantInfoVO {

	private String id;

	private String userName;

	private String loginPwd;
	
	private String googleSecretKey;

	private String merchantNum;

	private String merchantName;

	private String state;

	public static LoginMerchantInfoVO convertFor(Merchant merchant) {
		if (merchant == null) {
			return null;
		}
		LoginMerchantInfoVO vo = new LoginMerchantInfoVO();
		BeanUtils.copyProperties(merchant, vo);
		return vo;
	}

}
