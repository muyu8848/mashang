package com.mashang.useraccount.param;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UserAccountEditParam {

	@NotBlank
	private String id;

	@NotBlank
	private String userName;

	@NotBlank
	private String realName;
	
	private String mobile;
	
	@NotBlank
	private String state;

}
