package com.mashang.useraccount.domain;

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
@Table(name = "v_member_lately_login_log")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MemberLatelyLoginLog {

	/**
	 * 用户名
	 */
	@Id
	private String userName;

	/**
	 * 最后访问时间
	 */
	private Date lastAccessTime;

}
