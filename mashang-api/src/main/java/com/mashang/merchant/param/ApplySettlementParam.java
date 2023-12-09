package com.mashang.merchant.param;

import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.constants.Constant;
import com.mashang.merchant.domain.MerchantSettlementRecord;

import lombok.Data;

@Data
public class ApplySettlementParam {

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double withdrawAmount;
	
	private Double serviceFee;
	
	private Double actualToAccount;

	@NotBlank
	private String moneyPwd;

	@NotBlank
	private String merchantId;

	@NotBlank
	private String accountHolder;

	@NotBlank
	private String bankCardAccount;

	@NotBlank
	private String openAccountBank;

	public MerchantSettlementRecord convertToPo() {
		MerchantSettlementRecord po = new MerchantSettlementRecord();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setApplyTime(new Date());
		po.setState(Constant.商户结算状态_审核中);
		return po;
	}

}
