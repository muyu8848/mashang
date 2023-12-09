package com.mashang.distributepayout.param;

import lombok.Data;

@Data
public class SubmitPaymentVoucherParam {

	private String id;

	private String paymentVoucherId;

	private String depositor;
	
	private String userAccountId;

}
