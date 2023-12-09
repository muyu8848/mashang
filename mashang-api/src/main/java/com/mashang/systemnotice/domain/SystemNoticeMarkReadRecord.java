package com.mashang.systemnotice.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.mashang.common.utils.IdUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "system_notice_mark_read_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class SystemNoticeMarkReadRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Date createTime;

	private String systemNoticeId;

	private String userAccountId;

	public static SystemNoticeMarkReadRecord build(String systemNoticeId, String userAccountId) {
		SystemNoticeMarkReadRecord po = new SystemNoticeMarkReadRecord();
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setSystemNoticeId(systemNoticeId);
		po.setUserAccountId(userAccountId);
		return po;
	}

}
