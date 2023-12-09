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
@Table(name = "recharge_setting")
@DynamicInsert(true)
@DynamicUpdate(true)
public class RechargeSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Double rechargeLowerLimit;

	private Double rechargeUpperLimit;

	private String quickInputAmount;

	private Double usdtCnyExchangeRate;

	private Boolean autoUpdateUsdtCnyExchangeRate;

	private String rechargeExplain;

	private Boolean cantContinuousSubmit;

	private Double serviceProviderRechargeIncomeRate;

	private Date latelyUpdateTime;

	public static RechargeSetting build() {
		RechargeSetting setting = new RechargeSetting();
		setting.setId(IdUtils.getId());
		return setting;
	}

}
