package com.mashang.useraccount.domain;

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
@Table(name = "v_team_number_of_people")
@DynamicInsert(true)
@DynamicUpdate(true)
public class TeamNumberOfPeople implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String accountLevelPath;

	private String inviterId;

	private String userName;

	private Date registeredTime;

	private Integer level1;

	private Integer totalLevel;

	private Double todayTradeAmount;

}
