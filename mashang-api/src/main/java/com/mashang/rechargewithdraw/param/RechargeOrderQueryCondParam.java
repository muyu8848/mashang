package com.mashang.rechargewithdraw.param;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RechargeOrderQueryCondParam extends PageParam {

	private String orderNo;
	
	private String userAccountId;

	private String orderState;
	
	private String bankCardInfo;
	
	private String depositorInfo;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date submitStartTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date submitEndTime;
	
	private String rechargeWay;
	
	private String serviceProviderId;
	
	private String serviceProviderUserName;

}
