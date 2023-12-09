package com.mashang.merchant.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mashang.merchant.domain.FreezeRecord;
import com.mashang.merchant.domain.MerchantOrder;
import com.mashang.merchant.repo.FreezeRecordRepo;

import cn.hutool.core.util.NumberUtil;

@Service
public class FreezeRecordService {

	@Autowired
	private FreezeRecordRepo freezeRecordRepo;

	@Transactional(readOnly = true)
	public double getFreezeAmount(String userAccountId) {
		double freezeAmount = 0d;
		List<FreezeRecord> freezeRecords = freezeRecordRepo.findByDealTimeIsNullAndReceivedAccountId(userAccountId);
		for (FreezeRecord freezeRecord : freezeRecords) {
			MerchantOrder merchantOrder = freezeRecord.getMerchantOrder();
			if (merchantOrder == null) {
				continue;
			}
			freezeAmount += merchantOrder.getGatheringAmount();
		}
		return NumberUtil.round(freezeAmount, 4).doubleValue();
	}

}
