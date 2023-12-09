package com.mashang.useraccount.param;

import java.util.List;

import lombok.Data;

@Data
public class AssignRoleParam {

	private String userAccountId;

	private List<String> roleIds;

}
