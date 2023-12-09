package com.mashang.merchant.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.dictconfig.DictHolder;
import com.mashang.merchant.domain.MerchantAccountChangeLog;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MerchantAccountChangeLogVO {

	private String id;

	@Excel(name = "订单号", orderNum = "2")
	private String orderNo;

	@Excel(name = "账变时间", orderNum = "4", format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date accountChangeTime;

	private String accountChangeTypeCode;

	@Excel(name = "账变类型", orderNum = "3")
	private String accountChangeTypeName;

	@Excel(name = "账变金额", orderNum = "6")
	private Double accountChangeAmount;

	@Excel(name = "剩余可提现金额", orderNum = "7")
	private Double withdrawableAmount;

	@Excel(name = "备注", orderNum = "5")
	private String note;

	private String merchantId;

	@Excel(name = "账号", orderNum = "1")
	private String userName;

	public static List<MerchantAccountChangeLogVO> convertFor(List<MerchantAccountChangeLog> accountChangeLogs) {
		if (CollectionUtil.isEmpty(accountChangeLogs)) {
			return new ArrayList<>();
		}
		List<MerchantAccountChangeLogVO> vos = new ArrayList<>();
		for (MerchantAccountChangeLog accountChangeLog : accountChangeLogs) {
			vos.add(convertFor(accountChangeLog));
		}
		return vos;
	}

	public static MerchantAccountChangeLogVO convertFor(MerchantAccountChangeLog accountChangeLog) {
		if (accountChangeLog == null) {
			return null;
		}
		MerchantAccountChangeLogVO vo = new MerchantAccountChangeLogVO();
		BeanUtils.copyProperties(accountChangeLog, vo);
		if (accountChangeLog.getMerchant() != null) {
			vo.setUserName(accountChangeLog.getMerchant().getUserName());
		}
		vo.setAccountChangeTypeName(DictHolder.getDictItemName("merchantAccountChangeType", vo.getAccountChangeTypeCode()));
		return vo;
	}

}
