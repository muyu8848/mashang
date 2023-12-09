package com.mashang.gatheringcode.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.dictconfig.DictHolder;
import com.mashang.gatheringcode.domain.GatheringCode;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

@Data
public class GatheringCodeVO implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private String id;

	private String gatheringChannelId;

	private String gatheringChannelCode;

	private String gatheringChannelName;

	private String state;

	private String stateName;

	private Double minAmount;

	private Double maxAmount;

	private String payee;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	private String mobile;

	private String realName;

	private String account;

	private String alipayId;

	private String address;

	private Boolean inUse;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date initiateAuditTime;

	private String auditType;

	private String auditTypeName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private String storageId;

	private String userName;

	private Double everydayTradeAmount;

	private Double everydayTradeCount;

	private GatheringCodeUsageVO usage;

	public static List<GatheringCodeVO> convertFor(Collection<GatheringCode> gatheringCodes) {
		if (CollectionUtil.isEmpty(gatheringCodes)) {
			return new ArrayList<>();
		}
		List<GatheringCodeVO> vos = new ArrayList<>();
		for (GatheringCode gatheringCode : gatheringCodes) {
			vos.add(convertFor(gatheringCode));
		}
		return vos;
	}

	public static GatheringCodeVO convertFor(GatheringCode gatheringCode) {
		if (gatheringCode == null) {
			return null;
		}
		GatheringCodeVO vo = new GatheringCodeVO();
		BeanUtils.copyProperties(gatheringCode, vo);
		vo.setStateName(DictHolder.getDictItemName("gatheringCodeState", vo.getState()));
		if (StrUtil.isNotBlank(vo.getAuditType())) {
			vo.setAuditTypeName(DictHolder.getDictItemName("gatheringCodeAuditType", vo.getAuditType()));
		}
		if (gatheringCode.getUserAccount() != null) {
			vo.setUserName(gatheringCode.getUserAccount().getUserName());
		}
		if (gatheringCode.getGatheringChannel() != null) {
			vo.setGatheringChannelCode(gatheringCode.getGatheringChannel().getChannelCode());
			vo.setGatheringChannelName(gatheringCode.getGatheringChannel().getChannelName());
		}
		vo.setUsage(GatheringCodeUsageVO.convertFor(gatheringCode.getUsage()));
		return vo;
	}

}
