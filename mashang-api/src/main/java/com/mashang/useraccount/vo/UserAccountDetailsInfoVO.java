package com.mashang.useraccount.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.dictconfig.DictHolder;
import com.mashang.useraccount.domain.UserAccount;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class UserAccountDetailsInfoVO {

	private String id;

	private String userName;

	private String realName;
	
	private String mobile;

	private String accountType;

	private String accountTypeName;

	private Integer accountLevel;

	private Double cashDeposit;

	private Double freezeAmount;

	private Double serviceProviderAmount;

	private Double serviceProviderFreezeAmount;

	private String state;

	private String stateName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date registeredTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date latelyLoginTime;

	private String receiveOrderState;

	private String receiveOrderStateName;

	private String inviterId;

	private String inviterUserName;

	private String googleSecretKey;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date googleAuthBindTime;

	public static List<UserAccountDetailsInfoVO> convertFor(List<UserAccount> userAccounts) {
		if (CollectionUtil.isEmpty(userAccounts)) {
			return new ArrayList<>();
		}
		List<UserAccountDetailsInfoVO> vos = new ArrayList<>();
		for (UserAccount userAccount : userAccounts) {
			vos.add(convertFor(userAccount));
		}
		return vos;
	}

	public static UserAccountDetailsInfoVO convertFor(UserAccount userAccount) {
		if (userAccount == null) {
			return null;
		}
		UserAccountDetailsInfoVO vo = new UserAccountDetailsInfoVO();
		BeanUtils.copyProperties(userAccount, vo);
		vo.setAccountTypeName(DictHolder.getDictItemName("accountType", vo.getAccountType()));
		vo.setStateName(DictHolder.getDictItemName("accountState", vo.getState()));
		vo.setReceiveOrderStateName(DictHolder.getDictItemName("receiveOrderState", vo.getReceiveOrderState()));
		if (userAccount.getInviter() != null) {
			vo.setInviterUserName(userAccount.getInviter().getUserName());
		}
		return vo;
	}

}
