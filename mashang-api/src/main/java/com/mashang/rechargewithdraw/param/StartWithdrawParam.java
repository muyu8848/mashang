package com.mashang.rechargewithdraw.param;

import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.constants.Constant;
import com.mashang.rechargewithdraw.domain.WithdrawRecord;

import lombok.Data;

@Data
public class StartWithdrawParam {

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double withdrawAmount;

	@NotBlank
	private String moneyPwd;

	@NotBlank
	private String userAccountId;

	private String bankCardId;

	public WithdrawRecord convertToPo(double handlingFee) {
		WithdrawRecord po = new WithdrawRecord();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setSubmitTime(new Date());
		po.setState(Constant.提现记录状态_审核中);
		po.setHandlingFee(handlingFee);
		return po;
	}

}
