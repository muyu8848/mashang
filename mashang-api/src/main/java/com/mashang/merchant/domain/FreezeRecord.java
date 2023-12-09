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

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "freeze_record")
@DynamicInsert(true)
@DynamicUpdate(true)
public class FreezeRecord implements Serializable {

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

	/**
	 * 有效时间
	 */
	private Date usefulTime;

	/**
	 * 处理时间
	 */
	private Date dealTime;

	private String receivedAccountId;

	/**
	 * 乐观锁版本号
	 */
	@Version
	private Long version;

	/**
	 * 商户订单id
	 */
	@Column(name = "merchant_order_id", length = 32)
	private String merchantOrderId;

	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_order_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private MerchantOrder merchantOrder;

	public static FreezeRecord build(String receivedAccountId, String merchantOrderId,
			Integer freezeEffectiveDuration) {
		FreezeRecord po = new FreezeRecord();
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setUsefulTime(DateUtil.offset(po.getCreateTime(), DateField.MINUTE, freezeEffectiveDuration));
		po.setReceivedAccountId(receivedAccountId);
		po.setMerchantOrderId(merchantOrderId);
		return po;
	}
}
