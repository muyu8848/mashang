package com.mashang.merchant.vo;

import lombok.Data;

@Data
public class StartOrderRealNameBlackListVO {

	private String realName;

	private String createTime;

	public static StartOrderRealNameBlackListVO convertFor(String realName, String createTime) {
		StartOrderRealNameBlackListVO vo = new StartOrderRealNameBlackListVO();
		vo.setRealName(realName);
		vo.setCreateTime(createTime);
		return vo;
	}

}
