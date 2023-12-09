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
@Table(name = "v_recharge_situation")
@DynamicInsert(true)
@DynamicUpdate(true)
public class RechargeSituation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Double totalTradeAmount;

	private Long totalSuccessOrderNum;

	private Long totalOrderNum;

	private Double monthTradeAmount;

	private Long monthSuccessOrderNum;

	private Long monthOrderNum;

	private Double yesterdayTradeAmount;

	private Long yesterdaySuccessOrderNum;

	private Long yesterdayOrderNum;

	private Double todayTradeAmount;

	private Long todaySuccessOrderNum;

	private Long todayOrderNum;

}
