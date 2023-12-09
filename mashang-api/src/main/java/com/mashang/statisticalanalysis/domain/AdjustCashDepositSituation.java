package com.mashang.statisticalanalysis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "v_adjust_cash_deposit_situation")
@DynamicInsert(true)
@DynamicUpdate(true)
public class AdjustCashDepositSituation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Double totalAddAmount;

	private Double totalMinusAmount;

	private Double monthAddAmount;

	private Double monthMinusAmount;

	private Double yesterdayAddAmount;

	private Double yesterdayMinusAmount;

	private Double todayAddAmount;

	private Double todayMinusAmount;

}
