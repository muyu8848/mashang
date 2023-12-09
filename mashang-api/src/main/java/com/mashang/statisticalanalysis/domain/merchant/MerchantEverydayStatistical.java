package com.mashang.statisticalanalysis.domain.merchant;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "v_merchant_everyday_statistical")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MerchantEverydayStatistical implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;

	private String merchantId;

	private Date everyday;

	private Double tradeAmount;

	private Long paidOrderNum;

	private Long orderNum;

}
