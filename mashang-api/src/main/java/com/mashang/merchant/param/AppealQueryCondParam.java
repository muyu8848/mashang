package com.mashang.merchant.param;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class AppealQueryCondParam extends PageParam {

	private String orderNo;
	
	private String merchantId;
	
	private String merchantNum;

	private String gatheringChannelCode;

	private String receiverUserName;

	private String appealType;
	
	private String appealState;
	
	private String appealProcessWay;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date initiationStartTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date initiationEndTime;
	
	private String initiatorObj;

}
