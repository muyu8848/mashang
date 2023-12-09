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
@Table(name = "v_collect_for_another_income_situation")
@DynamicInsert(true)
@DynamicUpdate(true)
public class CollectForAnotherIncomeSituation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Double totalIncome;

	private Double monthIncome;

	private Double yesterdayIncome;

	private Double todayIncome;

}
