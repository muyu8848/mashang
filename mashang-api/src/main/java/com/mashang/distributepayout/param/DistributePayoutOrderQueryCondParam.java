package com.mashang.distributepayout.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DistributePayoutOrderQueryCondParam extends PageParam {

	@NotBlank
	private String receivedAccountId;

	private String orderNo;

	private String merchantSettlementRecordId;
	
	private String merchantNum;

	private String orderState;
	
	private String bankCardInfo;

	private Double minAmount;

	private Double maxAmount;
	
	private String receiverUserName;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createTimeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createTimeEnd;

}
