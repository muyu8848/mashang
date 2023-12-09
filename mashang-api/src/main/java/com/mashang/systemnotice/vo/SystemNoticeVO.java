package com.mashang.systemnotice.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.systemnotice.domain.SystemNotice;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class SystemNoticeVO implements Serializable {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String title;

	private String content;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	public static List<SystemNoticeVO> convertFor(List<SystemNotice> systemNotices) {
		if (CollectionUtil.isEmpty(systemNotices)) {
			return new ArrayList<>();
		}
		List<SystemNoticeVO> vos = new ArrayList<>();
		for (SystemNotice systemNotice : systemNotices) {
			vos.add(convertFor(systemNotice));
		}
		return vos;
	}

	public static SystemNoticeVO convertFor(SystemNotice systemNotice) {
		if (systemNotice == null) {
			return null;
		}
		SystemNoticeVO vo = new SystemNoticeVO();
		BeanUtils.copyProperties(systemNotice, vo);
		return vo;
	}

}
