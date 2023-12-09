package com.mashang.merchant.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.dictconfig.DictHolder;
import com.mashang.merchant.domain.Merchant;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MerchantVO {

	private String id;

	private String userName;

	private String merchantName;

	private String secretKey;

	private String state;

	private String stateName;
	
	private String accountType;

	private String accountTypeName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date latelyLoginTime;

	private Double withdrawableAmount;
	
	private Double freezeFund;
	
	private String googleSecretKey;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date googleAuthBindTime;
	
	private String inviterId;

	private String inviterUserName;

	public static List<MerchantVO> convertFor(List<Merchant> merchants) {
		if (CollectionUtil.isEmpty(merchants)) {
			return new ArrayList<>();
		}
		List<MerchantVO> vos = new ArrayList<>();
		for (Merchant merchant : merchants) {
			vos.add(convertFor(merchant));
		}
		return vos;
	}

	public static MerchantVO convertFor(Merchant merchant) {
		if (merchant == null) {
			return null;
		}
		MerchantVO vo = new MerchantVO();
		BeanUtils.copyProperties(merchant, vo);
		vo.setStateName(DictHolder.getDictItemName("accountState", vo.getState()));
		vo.setAccountTypeName(DictHolder.getDictItemName("merchantAccountType", vo.getAccountType()));
		if (merchant.getInviter() != null) {
			vo.setInviterUserName(merchant.getInviter().getUserName());
		}
		return vo;
	}

}
