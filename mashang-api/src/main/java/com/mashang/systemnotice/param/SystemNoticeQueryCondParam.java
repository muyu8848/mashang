package com.mashang.systemnotice.param;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SystemNoticeQueryCondParam extends PageParam {
	
	private String title;

}
