package com.mashang.income.domain;

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
@Table(name = "income_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class IncomeRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Boolean availableFlag;

	private String incomeType;

	private Double income;

	private Date settlementTime;

	private Date createTime;

	@Version
	private Long version;

	@Column(name = "biz_id", length = 32)
	private String bizId;

	@Column(name = "user_account_id", length = 32)
	private String userAccountId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_account_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private UserAccount userAccount;

	public void settlement() {
		this.setSettlementTime(new Date());
	}

	public static IncomeRecord build(String bizId, String incomeType, Double income, String userAccountId) {
		IncomeRecord incomeRecord = new IncomeRecord();
		incomeRecord.setId(IdUtils.getId());
		incomeRecord.setCreateTime(new Date());
		incomeRecord.setBizId(bizId);
		incomeRecord.setIncomeType(incomeType);
		incomeRecord.setIncome(income);
		incomeRecord.setUserAccountId(userAccountId);
		incomeRecord.setAvailableFlag(true);
		return incomeRecord;
	}

}
