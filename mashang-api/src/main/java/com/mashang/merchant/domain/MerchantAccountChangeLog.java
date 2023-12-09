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

import com.mashang.common.utils.IdUtils;
import com.mashang.constants.Constant;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "merchant_account_change_log")
@DynamicInsert(true)
@DynamicUpdate(true)
public class MerchantAccountChangeLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String orderNo;

	private Date accountChangeTime;

	private String accountChangeTypeCode;

	private Double accountChangeAmount;

	private Double withdrawableAmount;

	private String note;

	@Version
	private Long version;

	@Column(name = "merchant_id", length = 32)
	private String merchantId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "merchant_id", updatable = false, insertable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Merchant merchant;
	
	public static MerchantAccountChangeLog buildWithReleaseFreezeFund(Merchant merchant, MerchantFreezeFundRecord record) {
		MerchantAccountChangeLog log = new MerchantAccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(record.getOrderNo());
		log.setAccountChangeTime(record.getCreateTime());
		log.setAccountChangeTypeCode(Constant.商户账变日志类型_解冻资金);
		log.setAccountChangeAmount(NumberUtil.round(record.getFreezeFund(), 2).doubleValue());
		log.setWithdrawableAmount(merchant.getWithdrawableAmount());
		log.setMerchantId(merchant.getId());
		return log;
	}

	public static MerchantAccountChangeLog buildWithFreezeFund(Merchant merchant, MerchantFreezeFundRecord record) {
		MerchantAccountChangeLog log = new MerchantAccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(record.getOrderNo());
		log.setAccountChangeTime(record.getCreateTime());
		log.setAccountChangeTypeCode(Constant.商户账变日志类型_冻结资金);
		log.setAccountChangeAmount(-NumberUtil.round(record.getFreezeFund(), 2).doubleValue());
		log.setWithdrawableAmount(merchant.getWithdrawableAmount());
		log.setMerchantId(merchant.getId());
		return log;
	}

	public static MerchantAccountChangeLog buildWithWithdrawSettlementNotApprovedRefund(Merchant merchant,
			MerchantSettlementRecord settlementRecord) {
		MerchantAccountChangeLog log = new MerchantAccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(settlementRecord.getOrderNo());
		log.setAccountChangeTime(settlementRecord.getApprovalTime());
		log.setAccountChangeTypeCode(Constant.商户账变日志类型_提现不符退款);
		log.setAccountChangeAmount(NumberUtil
				.round(settlementRecord.getActualToAccount() + settlementRecord.getServiceFee(), 2).doubleValue());
		log.setWithdrawableAmount(merchant.getWithdrawableAmount());
		log.setMerchantId(merchant.getId());
		if (settlementRecord.getServiceFee() > 0) {
			log.setNote("金额:" + settlementRecord.getWithdrawAmount() + "," + "服务费:" + settlementRecord.getServiceFee()
					+ "," + "实际到账:" + settlementRecord.getActualToAccount() + "," + "实际退款:"
					+ NumberUtil.round(settlementRecord.getActualToAccount() + settlementRecord.getServiceFee(), 2)
							.doubleValue());
		} else {
			log.setNote("金额:" + settlementRecord.getWithdrawAmount() + "," + "实际到账:"
					+ settlementRecord.getActualToAccount() + "," + "实际退款:" + settlementRecord.getActualToAccount());
		}
		return log;
	}
	
	public static MerchantAccountChangeLog buildWithWithdrawSettlementExceptionBack(Merchant merchant,
			MerchantSettlementRecord settlementRecord) {
		MerchantAccountChangeLog log = new MerchantAccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(settlementRecord.getOrderNo());
		log.setAccountChangeTime(settlementRecord.getApprovalTime());
		log.setAccountChangeTypeCode(Constant.商户账变日志类型_提现异常退回);
		log.setAccountChangeAmount(NumberUtil
				.round(settlementRecord.getActualToAccount() + settlementRecord.getServiceFee(), 2).doubleValue());
		log.setWithdrawableAmount(merchant.getWithdrawableAmount());
		log.setMerchantId(merchant.getId());
		if (settlementRecord.getServiceFee() > 0) {
			log.setNote("金额:" + settlementRecord.getWithdrawAmount() + "," + "服务费:" + settlementRecord.getServiceFee()
					+ "," + "实际到账:" + settlementRecord.getActualToAccount() + "," + "实际退款:"
					+ NumberUtil.round(settlementRecord.getActualToAccount() + settlementRecord.getServiceFee(), 2)
							.doubleValue());
		} else {
			log.setNote("金额:" + settlementRecord.getWithdrawAmount() + "," + "实际到账:"
					+ settlementRecord.getActualToAccount() + "," + "实际退款:" + settlementRecord.getActualToAccount());
		}
		return log;
	}

	public static MerchantAccountChangeLog buildWithWithdrawSettlement(Merchant merchant,
			MerchantSettlementRecord settlementRecord) {
		MerchantAccountChangeLog log = new MerchantAccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(settlementRecord.getOrderNo());
		log.setAccountChangeTime(new Date());
		log.setAccountChangeTypeCode(Constant.商户账变日志类型_提现结算);
		log.setAccountChangeAmount(-NumberUtil
				.round(settlementRecord.getActualToAccount() + settlementRecord.getServiceFee(), 2).doubleValue());
		log.setWithdrawableAmount(merchant.getWithdrawableAmount());
		log.setMerchantId(merchant.getId());
		if (settlementRecord.getServiceFee() > 0) {
			log.setNote("金额:" + settlementRecord.getWithdrawAmount() + "," + "服务费:" + settlementRecord.getServiceFee()
					+ "实际到账:" + settlementRecord.getActualToAccount());
		} else {
			log.setNote("金额:" + settlementRecord.getWithdrawAmount() + "," + "实际到账:"
					+ settlementRecord.getActualToAccount());
		}
		return log;
	}

	public static MerchantAccountChangeLog buildAdjustWithdrawableAmount(Merchant merchant,
			Double accountChangeAmount, String note) {
		MerchantAccountChangeLog log = new MerchantAccountChangeLog();
		log.setId(IdUtils.getId());
		log.setAccountChangeTime(new Date());
		log.setAccountChangeTypeCode(accountChangeAmount > 0 ? Constant.商户账变日志类型_增加余额 : Constant.商户账变日志类型_减少余额);
		log.setAccountChangeAmount(accountChangeAmount);
		log.setWithdrawableAmount(merchant.getWithdrawableAmount());
		log.setMerchantId(merchant.getId());
		if (StrUtil.isNotBlank(note)) {
			log.setNote(note);
		}
		return log;
	}

	public static MerchantAccountChangeLog buildWithPaidOrderActualIncome(String merchantId, Double accountChangeAmount,
			Double withdrawableAmount, String orderNo) {
		MerchantAccountChangeLog log = new MerchantAccountChangeLog();
		log.setId(IdUtils.getId());
		log.setOrderNo(orderNo);
		log.setAccountChangeTime(new Date());
		log.setAccountChangeTypeCode(Constant.商户账变日志类型_已支付订单实收金额);
		log.setAccountChangeAmount(accountChangeAmount);
		log.setWithdrawableAmount(withdrawableAmount);
		log.setMerchantId(merchantId);
		return log;
	}

}
