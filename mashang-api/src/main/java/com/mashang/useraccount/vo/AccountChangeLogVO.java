package com.mashang.useraccount.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.dictconfig.DictHolder;
import com.mashang.useraccount.domain.AccountChangeLog;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class AccountChangeLogVO {

	private String id;

	@Excel(name = "订单号", orderNum = "2")
	private String orderNo;

	@Excel(name = "账变时间", orderNum = "4", format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date accountChangeTime;

	private String accountChangeType;

	@Excel(name = "账变类型", orderNum = "3")
	private String accountChangeTypeName;
	
	@Excel(name = "账变", orderNum = "8")
	private Double cashDepositChange;

	@Excel(name = "账变前", orderNum = "6")
	private Double cashDepositBefore;

	@Excel(name = "账变后", orderNum = "7")
	private Double cashDepositAfter;

	@Excel(name = "备注", orderNum = "5")
	private String note;

	private String userAccountId;

	@Excel(name = "用户名", orderNum = "1")
	private String userName;

	public static List<AccountChangeLogVO> convertFor(List<AccountChangeLog> accountChangeLogs) {
		if (CollectionUtil.isEmpty(accountChangeLogs)) {
			return new ArrayList<>();
		}
		List<AccountChangeLogVO> vos = new ArrayList<>();
		for (AccountChangeLog accountChangeLog : accountChangeLogs) {
			vos.add(convertFor(accountChangeLog));
		}
		return vos;
	}

	public static AccountChangeLogVO convertFor(AccountChangeLog accountChangeLog) {
		if (accountChangeLog == null) {
			return null;
		}
		AccountChangeLogVO vo = new AccountChangeLogVO();
		BeanUtils.copyProperties(accountChangeLog, vo);
		if (accountChangeLog.getUserAccount() != null) {
			vo.setUserName(accountChangeLog.getUserAccount().getUserName());
		}
		vo.setAccountChangeTypeName(DictHolder.getDictItemName("accountChangeType", vo.getAccountChangeType()));
		return vo;
	}
}
