package com.mashang.merchant.param;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MerchantFreezeFundRecordQueryCondParam extends PageParam {

	private String merchantId;
	
	private String merchantNum;

	private String orderNo;

	private Boolean releaseFlag;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createTimeStart;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date createTimeEnd;

}
