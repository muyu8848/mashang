package com.mashang.merchant.vo;

import lombok.Data;

@Data
public class StartOrderIpBlackListVO {

	private String ipAddr;

	private String createTime;

	public static StartOrderIpBlackListVO convertFor(String ipAddr, String createTime) {
		StartOrderIpBlackListVO vo = new StartOrderIpBlackListVO();
		vo.setIpAddr(ipAddr);
		vo.setCreateTime(createTime);
		return vo;
	}

}
