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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.mashang.common.utils.IdUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "actual_income_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class ActualIncomeRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private Double actualIncome;

	private Date createTime;

	private Date settlementTime;
	
	private Boolean availableFlag;

	@Version
	private Long version;

	@Column(name = "merchant_order_id", length = 32)
	private String merchantOrderId;

	@NotFound(action = NotFoundAction.IGNORE)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_order_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private MerchantOrder merchantOrder;

	@Column(name = "merchant_id", length = 32)
	private String merchantId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Merchant merchant;

	public static ActualIncomeRecord build(Double actualIncome, String merchantOrderId, String merchantId) {
		ActualIncomeRecord po = new ActualIncomeRecord();
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setAvailableFlag(true);
		po.setActualIncome(actualIncome);
		po.setMerchantOrderId(merchantOrderId);
		po.setMerchantId(merchantId);
		return po;
	}

}
