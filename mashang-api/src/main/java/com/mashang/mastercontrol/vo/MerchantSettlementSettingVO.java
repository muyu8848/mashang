package com.mashang.mastercontrol.vo;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.mastercontrol.domain.MerchantSettlementSetting;

import lombok.Data;

@Data
public class MerchantSettlementSettingVO {

	private String id;

	private Double merchantSettlementRate;

	private Double minServiceFee;

	private Double minAmount;

	private Double maxAmount;

	private Double distributePayoutIncomeRate;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date latelyUpdateTime;

	public static MerchantSettlementSettingVO convertFor(MerchantSettlementSetting setting) {
		MerchantSettlementSettingVO vo = new MerchantSettlementSettingVO();
		if (setting != null) {
			BeanUtils.copyProperties(setting, vo);
		}
		return vo;
	}

}
