package com.mashang.useraccount.vo;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.useraccount.domain.UserAccount;

import lombok.Data;

@Data
public class MemberAccountInfoVO {

	private String userName;

	private String realName;

	private String mobile;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date registeredTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date latelyLoginTime;

	public static MemberAccountInfoVO convertFor(UserAccount userAccount) {
		if (userAccount == null) {
			return null;
		}
		MemberAccountInfoVO vo = new MemberAccountInfoVO();
		BeanUtils.copyProperties(userAccount, vo);
		return vo;
	}

}
