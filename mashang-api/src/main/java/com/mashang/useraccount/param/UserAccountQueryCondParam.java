package com.mashang.useraccount.param;

import java.util.List;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UserAccountQueryCondParam extends PageParam {
	
	private String userName;

	private String realName;
	
	private String mobile;
	
	private List<String> accountTypes;

}
