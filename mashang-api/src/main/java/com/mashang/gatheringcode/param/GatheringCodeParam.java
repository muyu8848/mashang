package com.mashang.gatheringcode.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.constants.Constant;
import com.mashang.gatheringcode.domain.GatheringCode;

import lombok.Data;

@Data
public class GatheringCodeParam {

	@NotBlank
	private String gatheringChannelId;

	private Double minAmount;

	private Double maxAmount;

	private String payee;

	private String codeContent;

	private String storageId;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	private String mobile;

	private String realName;

	private String account;

	private String alipayId;

	private String address;

	private Double everydayTradeAmount;

	private Double everydayTradeCount;

	public GatheringCode convertToPo(String userAccountId) {
		GatheringCode po = new GatheringCode();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setUserAccountId(userAccountId);
		po.setInUse(false);
		po.setState(Constant.收款码状态_正常);
		po.setDeletedFlag(false);
		return po;
	}

}
