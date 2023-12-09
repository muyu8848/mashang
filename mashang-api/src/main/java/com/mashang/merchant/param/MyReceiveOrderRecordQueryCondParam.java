package com.mashang.merchant.param;

import javax.validation.constraints.NotBlank;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MyReceiveOrderRecordQueryCondParam extends PageParam {

	@NotBlank
	private String receivedAccountId;
	
	private String orderNo;
	
	private String merchantOrderNo;
	
	private String payState;

}
