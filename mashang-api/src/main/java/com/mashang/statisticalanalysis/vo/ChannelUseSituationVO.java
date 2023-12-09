package com.mashang.statisticalanalysis.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.mashang.statisticalanalysis.domain.ChannelUseSituation;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class ChannelUseSituationVO implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private String id;

	private String channelName;

	private Integer accountTotal;

	private Integer inOrderAccountTotal;

	private Integer disabledAccountTotal;

	private Integer codeTotal;

	private Integer inOrderCodeTotal;

	private Integer onlineCodeTotal;

	private Integer exceptionCodeTotal;
	
	public static List<ChannelUseSituationVO> convertFor(List<ChannelUseSituation> situations) {
		if (CollectionUtil.isEmpty(situations)) {
			return new ArrayList<>();
		}
		List<ChannelUseSituationVO> vos = new ArrayList<>();
		for (ChannelUseSituation situation : situations) {
			vos.add(convertFor(situation));
		}
		return vos;
	}

	public static ChannelUseSituationVO convertFor(ChannelUseSituation situation) {
		if (situation == null) {
			return null;
		}
		ChannelUseSituationVO vo = new ChannelUseSituationVO();
		BeanUtils.copyProperties(situation, vo);
		return vo;
	}

}
