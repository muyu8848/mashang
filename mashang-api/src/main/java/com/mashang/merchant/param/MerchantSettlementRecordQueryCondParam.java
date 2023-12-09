package com.mashang.merchant.param;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class MerchantSettlementRecordQueryCondParam extends PageParam {

	private String orderNo;

	private String merchantId;

	private String merchantNum;

	private String state;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date applyStartTime;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date applyEndTime;

}
