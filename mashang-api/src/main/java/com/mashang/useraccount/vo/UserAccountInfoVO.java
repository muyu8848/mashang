package com.mashang.useraccount.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.useraccount.domain.UserAccount;

import lombok.Data;

@Data
public class UserAccountInfoVO {

	private String id;

	private String userName;

	private String realName;
	
	private String mobile;

	private String inviteCode;

	private Double cashDeposit;

	private Double freezeAmount;

	private Double serviceProviderAmount;

	private Double serviceProviderFreezeAmount;

	private String receiveOrderState;

	public static UserAccountInfoVO convertFor(UserAccount userAccount) {
		if (userAccount == null) {
			return null;
		}
		UserAccountInfoVO vo = new UserAccountInfoVO();
		BeanUtils.copyProperties(userAccount, vo);
		return vo;
	}

}
