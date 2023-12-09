package com.mashang.statisticalanalysis.domain;

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
@Table(name = "v_member_everyday_income")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MemberEverydayIncome implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private Double everydayIncome;

	private Date everyday;

	private String userAccountId;

}
