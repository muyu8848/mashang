package com.mashang.useraccount.param;

import javax.validation.constraints.NotBlank;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Level1MemberQueryCondParam extends PageParam {

	@NotBlank
	private String currentAccountId;

	@NotBlank
	private String lowerLevelId;

	private String userName;

}
