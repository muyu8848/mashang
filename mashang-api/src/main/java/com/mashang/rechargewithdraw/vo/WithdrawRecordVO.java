package com.mashang.rechargewithdraw.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.dictconfig.DictHolder;
import com.mashang.rechargewithdraw.domain.WithdrawRecord;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class WithdrawRecordVO {

	private String id;

	@Excel(name = "订单号", orderNum = "1")
	private String orderNo;

	@Excel(name = "提现金额", orderNum = "3")
	private Double withdrawAmount;
	
	private Double handlingFee;

	private String openAccountBank;

	private String accountHolder;

	private String bankCardAccount;

	@Excel(name = "提交时间", orderNum = "5", format = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date submitTime;

	private String state;

	@Excel(name = "状态", orderNum = "6")
	private String stateName;

	private String note;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date approvalTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date confirmCreditedTime;

	private String userAccountId;

	@Excel(name = "发起用户", orderNum = "2")
	private String userName;

	public static List<WithdrawRecordVO> convertFor(List<WithdrawRecord> withdrawRecords) {
		if (CollectionUtil.isEmpty(withdrawRecords)) {
			return new ArrayList<>();
		}
		List<WithdrawRecordVO> vos = new ArrayList<>();
		for (WithdrawRecord withdrawRecord : withdrawRecords) {
			vos.add(convertFor(withdrawRecord));
		}
		return vos;
	}

	public static WithdrawRecordVO convertFor(WithdrawRecord withdrawRecord) {
		if (withdrawRecord == null) {
			return null;
		}
		WithdrawRecordVO vo = new WithdrawRecordVO();
		BeanUtils.copyProperties(withdrawRecord, vo);
		vo.setStateName(DictHolder.getDictItemName("withdrawRecordState", vo.getState()));
		if (withdrawRecord.getUserAccount() != null) {
			vo.setUserName(withdrawRecord.getUserAccount().getUserName());
		}
		return vo;
	}

}
