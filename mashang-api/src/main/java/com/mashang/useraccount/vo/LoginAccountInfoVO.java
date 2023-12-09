package com.mashang.useraccount.vo;

import org.springframework.beans.BeanUtils;

import com.mashang.useraccount.domain.UserAccount;

import lombok.Data;

@Data
public class LoginAccountInfoVO {

	private String id;

	private String userName;

	private String loginPwd;

	private String googleSecretKey;

	private String accountType;

	private String state;

	public static LoginAccountInfoVO convertFor(UserAccount userAccount) {
		if (userAccount == null) {
			return null;
		}
		LoginAccountInfoVO vo = new LoginAccountInfoVO();
		BeanUtils.copyProperties(userAccount, vo);
		return vo;
	}

}
