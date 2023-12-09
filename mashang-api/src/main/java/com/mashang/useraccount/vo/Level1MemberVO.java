package com.mashang.useraccount.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.useraccount.domain.UserAccount;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class Level1MemberVO {
	
	private String id;

	private String userName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date registeredTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date latelyLoginTime;

	public static List<Level1MemberVO> convertFor(List<UserAccount> userAccounts) {
		if (CollectionUtil.isEmpty(userAccounts)) {
			return new ArrayList<>();
		}
		List<Level1MemberVO> vos = new ArrayList<>();
		for (UserAccount userAccount : userAccounts) {
			vos.add(convertFor(userAccount));
		}
		return vos;
	}

	public static Level1MemberVO convertFor(UserAccount userAccount) {
		if (userAccount == null) {
			return null;
		}
		Level1MemberVO vo = new Level1MemberVO();
		BeanUtils.copyProperties(userAccount, vo);
		return vo;
	}

}
