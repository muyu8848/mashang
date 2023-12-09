package com.mashang.merchant.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.merchant.domain.QueueRecord;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class RealTimeQueueRecordVO {

	private String id;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date queueTime;

	private String userAccountId;

	private String userName;

	private Double cashDeposit;

	private Double freezeAmount;

	public static List<RealTimeQueueRecordVO> convertFor(List<QueueRecord> queueRecords) {
		if (CollectionUtil.isEmpty(queueRecords)) {
			return new ArrayList<>();
		}
		List<RealTimeQueueRecordVO> vos = new ArrayList<>();
		for (QueueRecord queueRecord : queueRecords) {
			vos.add(convertFor(queueRecord));
		}
		return vos;
	}

	public static RealTimeQueueRecordVO convertFor(QueueRecord queueRecord) {
		if (queueRecord == null) {
			return null;
		}
		RealTimeQueueRecordVO vo = new RealTimeQueueRecordVO();
		BeanUtils.copyProperties(queueRecord, vo);
		if (queueRecord.getUserAccount() != null) {
			vo.setUserName(queueRecord.getUserAccount().getUserName());
			vo.setCashDeposit(queueRecord.getUserAccount().getCashDeposit());
			vo.setFreezeAmount(queueRecord.getUserAccount().getFreezeAmount());
		}
		return vo;
	}

}
