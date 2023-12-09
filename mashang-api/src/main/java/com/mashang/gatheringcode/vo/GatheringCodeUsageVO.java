package com.mashang.gatheringcode.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.gatheringcode.domain.GatheringCodeUsage;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class GatheringCodeUsageVO implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private String id;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date lastReceivedTime;

	private Double totalTradeAmount;

	private Long totalPaidOrderNum;

	private Long totalOrderNum;

	private Double totalSuccessRate;

	private Double todayTradeAmount;

	private Long todayPaidOrderNum;

	private Long todayOrderNum;

	private Double todaySuccessRate;

	public static List<GatheringCodeUsageVO> convertFor(Collection<GatheringCodeUsage> gatheringCodes) {
		if (CollectionUtil.isEmpty(gatheringCodes)) {
			return new ArrayList<>();
		}
		List<GatheringCodeUsageVO> vos = new ArrayList<>();
		for (GatheringCodeUsage gatheringCode : gatheringCodes) {
			vos.add(convertFor(gatheringCode));
		}
		return vos;
	}

	public static GatheringCodeUsageVO convertFor(GatheringCodeUsage gatheringCode) {
		if (gatheringCode == null) {
			return null;
		}
		GatheringCodeUsageVO vo = new GatheringCodeUsageVO();
		BeanUtils.copyProperties(gatheringCode, vo);
		return vo;
	}

}
