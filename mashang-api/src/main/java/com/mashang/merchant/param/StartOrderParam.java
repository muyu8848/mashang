package com.mashang.merchant.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.constants.Constant;
import com.mashang.merchant.domain.MerchantOrder;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import lombok.Data;

@Data
public class StartOrderParam {

	@NotBlank
	private String merchantNum;

	@NotBlank
	private String orderNo;

	@NotBlank
	private String payType;

	@NotBlank
	private String amount;

	@NotBlank
	private String notifyUrl;

	private String returnUrl;

	private String attch;

	@NotBlank
	private String sign;
	
	private String ip;

	private String province;

	private String city;

	private String cityCode;

	public MerchantOrder convertToPo(String merchantId, String gatheringChannelId,
			Integer orderEffectiveDuration) {
		MerchantOrder po = new MerchantOrder();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setOrderNo(po.getId());
		po.setMerchantOrderNo(this.getOrderNo());
		po.setMerchantId(merchantId);
		po.setGatheringChannelId(gatheringChannelId);
		po.setGatheringAmount(Double.parseDouble(this.getAmount()));
		po.setSubmitTime(new Date());
		po.setOrderState(Constant.商户订单状态_等待接单);
		po.setUsefulTime(DateUtil.offset(po.getSubmitTime(), DateField.SECOND, orderEffectiveDuration));
		po.setNoticeState(Constant.通知状态_未通知);
		return po;
	}

}
