package com.mashang.systemnotice.param;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.systemnotice.domain.SystemNotice;

import lombok.Data;

@Data
public class AddOrUpdateSystemNoticeParam {

	private String id;
	
	private String title;

	private String content;

	public SystemNotice convertToPo() {
		SystemNotice po = new SystemNotice();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		return po;
	}

}
