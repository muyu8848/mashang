package com.mashang.useraccount.param;

import java.util.List;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SaveAccountReceiveOrderChannelParam {

	@NotBlank
	private String userAccountId;

	private List<AccountReceiveOrderChannelParam> channels;

}
