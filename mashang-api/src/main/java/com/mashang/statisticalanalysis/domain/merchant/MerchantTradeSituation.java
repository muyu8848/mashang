package com.mashang.statisticalanalysis.domain.merchant;

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
@Table(name = "v_merchant_trade_situation")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MerchantTradeSituation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Double totalTradeAmount;

	private Double totalHandlingFee;
	
	private Double totalAgentIncome;

	private Double totalActualIncome;

	private Long totalPaidOrderNum;

	private Long totalOrderNum;

	private Double totalSuccessRate;

	private Double monthTradeAmount;

	private Double monthHandlingFee;
	
	private Double monthAgentIncome;

	private Double monthActualIncome;

	private Long monthPaidOrderNum;

	private Long monthOrderNum;

	private Double monthSuccessRate;

	private Double yesterdayTradeAmount;

	private Double yesterdayHandlingFee;
	
	private Double yesterdayAgentIncome;

	private Double yesterdayActualIncome;

	private Long yesterdayPaidOrderNum;

	private Long yesterdayOrderNum;

	private Double yesterdaySuccessRate;

	private Double todayTradeAmount;

	private Double todayHandlingFee;
	
	private Double todayAgentIncome;

	private Double todayActualIncome;

	private Long todayPaidOrderNum;

	private Long todayOrderNum;

	private Double todaySuccessRate;

}
