package com.mashang.useraccount.domain;

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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_account")
@DynamicInsert(true)
@DynamicUpdate(true)
public class UserAccount implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String userName;

	private String realName;
	
	private String mobile;
	
	private String inviteCode;
	
	private String googleSecretKey;

	private Date googleAuthBindTime;

	private String accountType;
	
	private Integer accountLevel;

	private String accountLevelPath;

	private String loginPwd;

	private String moneyPwd;

	private Double cashDeposit;
	
	private Double freezeAmount;
	
	private Double serviceProviderAmount;
	
	private Double serviceProviderFreezeAmount;

	private String state;

	private Date registeredTime;

	private Date latelyLoginTime;

	private String receiveOrderState;

	private Boolean deletedFlag;

	private Date deletedTime;

	@Column(name = "inviter_id", length = 32)
	private String inviterId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inviter_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount inviter;

	@Version
	private Long version;
	
	public void updateInviteInfo(UserAccount inviter) {
		this.setInviterId(inviter.getId());
		this.setAccountLevel(inviter.getAccountLevel() + 1);
		this.setAccountLevelPath(inviter.getAccountLevelPath() + "." + this.getId());
	}

	public void deleted() {
		this.setDeletedFlag(true);
		this.setDeletedTime(new Date());
	}
	
	public void bindGoogleAuth(String googleSecretKey) {
		this.setGoogleSecretKey(googleSecretKey);
		this.setGoogleAuthBindTime(new Date());
	}

	public void unBindGoogleAuth() {
		this.setGoogleSecretKey(null);
		this.setGoogleAuthBindTime(null);
	}
	
}
