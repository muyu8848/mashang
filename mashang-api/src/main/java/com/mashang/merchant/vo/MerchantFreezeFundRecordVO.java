package com.mashang.merchant.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.merchant.domain.MerchantFreezeFundRecord;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MerchantFreezeFundRecordVO {

	private String id;

	private String orderNo;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private Double freezeFund;

	private String note;

	private Boolean releaseFlag;

	private String merchantNum;

	private String merchantName;

	public static List<MerchantFreezeFundRecordVO> convertFor(List<MerchantFreezeFundRecord> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MerchantFreezeFundRecordVO> vos = new ArrayList<>();
		for (MerchantFreezeFundRecord po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static MerchantFreezeFundRecordVO convertFor(MerchantFreezeFundRecord po) {
		if (po == null) {
			return null;
		}
		MerchantFreezeFundRecordVO vo = new MerchantFreezeFundRecordVO();
		BeanUtils.copyProperties(po, vo);
		if (po.getMerchant() != null) {
			vo.setMerchantNum(po.getMerchant().getUserName());
			vo.setMerchantName(po.getMerchant().getMerchantName());
		}
		return vo;
	}

}
