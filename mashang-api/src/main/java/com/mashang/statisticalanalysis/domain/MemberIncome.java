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
@Table(name = "v_member_income")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MemberIncome implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Double totalIncome;
	
	private Double yesterdayIncome;

	private Double todayIncome;

}
