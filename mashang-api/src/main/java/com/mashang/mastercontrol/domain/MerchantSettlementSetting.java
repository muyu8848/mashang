package com.mashang.mastercontrol.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.mashang.common.utils.IdUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "merchant_settlement_setting")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MerchantSettlementSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Double merchantSettlementRate;

	private Double minServiceFee;
	
	private Double minAmount;
	
	private Double maxAmount;

	private Double distributePayoutIncomeRate;

	private Date latelyUpdateTime;

	public static MerchantSettlementSetting build() {
		MerchantSettlementSetting setting = new MerchantSettlementSetting();
		setting.setId(IdUtils.getId());
		return setting;
	}

}
