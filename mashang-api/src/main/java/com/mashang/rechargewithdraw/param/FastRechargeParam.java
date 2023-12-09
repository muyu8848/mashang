package com.mashang.rechargewithdraw.param;

import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.constants.Constant;
import com.mashang.rechargewithdraw.domain.RechargeOrder;

import lombok.Data;

@Data
public class FastRechargeParam {

	@NotNull
	@DecimalMin(value = "0", inclusive = false)
	private Double rechargeAmount;

	@NotBlank
	private String userAccountId;
	
	public RechargeOrder convertToPo() {
		RechargeOrder po = new RechargeOrder();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setSubmitTime(new Date());
		po.setOrderNo(po.getId());
		po.setOrderState(Constant.充值订单状态_审核中);
		po.setRechargeChannelType(Constant.充值通道类型_银行卡);
		po.setRechargeWay(Constant.充值方式_快速充值);
		return po;
	}

}
