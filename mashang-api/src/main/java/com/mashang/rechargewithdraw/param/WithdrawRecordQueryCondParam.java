package com.mashang.rechargewithdraw.param;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class WithdrawRecordQueryCondParam extends PageParam {
	
	private String orderNo;
	
	private String userAccountId;
	
	private String state;
	
	private String bankCardInfo;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date submitStartTime;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date submitEndTime;

}
