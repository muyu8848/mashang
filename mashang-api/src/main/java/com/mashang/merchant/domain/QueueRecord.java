package com.mashang.merchant.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.mashang.common.utils.IdUtils;
import com.mashang.useraccount.domain.UserAccount;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "queue_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class QueueRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Date queueTime;

	private Boolean used;

	private String note;

	private Boolean markRead;

	@Version
	private Long version;

	@Column(name = "user_account_id", length = 32)
	private String userAccountId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount userAccount;

	public void used(String note) {
		this.setUsed(true);
		this.setNote(note);
	}

	public static QueueRecord build(UserAccount userAccount) {
		QueueRecord po = new QueueRecord();
		po.setId(IdUtils.getId());
		po.setQueueTime(new Date());
		po.setUsed(false);
		po.setUserAccountId(userAccount.getId());
		po.setMarkRead(false);
		return po;
	}

}
