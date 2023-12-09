package com.mashang.useraccount.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.useraccount.domain.TeamNumberOfPeople;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class TeamNumberOfPeopleVO {

	private String id;

	private String accountLevelPath;

	private String inviterId;

	private String userName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date registeredTime;

	private Integer level1;

	private Integer totalLevel;

	private Double todayTradeAmount;

	public static List<TeamNumberOfPeopleVO> convertFor(List<TeamNumberOfPeople> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<TeamNumberOfPeopleVO> vos = new ArrayList<>();
		for (TeamNumberOfPeople po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static TeamNumberOfPeopleVO convertFor(TeamNumberOfPeople teamNumberOfPeople) {
		if (teamNumberOfPeople == null) {
			return null;
		}
		TeamNumberOfPeopleVO vo = new TeamNumberOfPeopleVO();
		BeanUtils.copyProperties(teamNumberOfPeople, vo);
		return vo;
	}

}
