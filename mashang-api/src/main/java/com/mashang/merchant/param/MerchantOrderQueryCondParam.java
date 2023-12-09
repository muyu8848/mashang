package com.mashang.merchant.param;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MerchantOrderQueryCondParam extends PageParam {

	private String orderNo;

	private String merchantId;
	
	private String merchantNum;
	
	private String higherLevelMerchantId;

	private String merchantOrderNo;

	private String channelId;
	
	private Double minAmount;
	
	private Double maxAmount;

	private String orderState;

	private String receiverUserName;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date submitStartTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date submitEndTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date receiveOrderStartTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date receiveOrderEndTime;
	
	private String payNoticeState;
	
	private String gatheringCodeDetailInfo;

}
