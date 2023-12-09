package com.mashang.rechargewithdraw.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class FastRechargeSubmitPaymentInfoParam {

	@NotBlank
	private String userAccountId;

	@NotBlank
	private String rechargeOrderId;

	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
	private Date depositTime;

	private String depositor;

}
