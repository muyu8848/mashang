package com.mashang.useraccount.vo;

import lombok.Data;

@Data
public class LoginIpBlackListVO {

	private String ipAddr;

	private String createTime;

	public static LoginIpBlackListVO convertFor(String ipAddr, String createTime) {
		LoginIpBlackListVO vo = new LoginIpBlackListVO();
		vo.setIpAddr(ipAddr);
		vo.setCreateTime(createTime);
		return vo;
	}

}
