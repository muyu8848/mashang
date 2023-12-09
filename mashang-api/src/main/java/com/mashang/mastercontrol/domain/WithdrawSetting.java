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
@Table(name = "withdraw_setting")
@DynamicInsert(true)
@DynamicUpdate(true)
public class WithdrawSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Double withdrawRate;

	private Double minHandlingFee;

	private Integer everydayWithdrawTimesUpperLimit;

	private Double withdrawLowerLimit;

	private Double withdrawUpperLimit;

	private String withdrawExplain;

	private Date latelyUpdateTime;

	public static WithdrawSetting build() {
		WithdrawSetting setting = new WithdrawSetting();
		setting.setId(IdUtils.getId());
		return setting;
	}

}
