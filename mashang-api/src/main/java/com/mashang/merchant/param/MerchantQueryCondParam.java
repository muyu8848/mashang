package com.mashang.merchant.param;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MerchantQueryCondParam extends PageParam {

	private String userName;
	
	private String inviterId;

}
