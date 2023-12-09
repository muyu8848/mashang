package com.mashang.merchant.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.merchant.domain.ActualIncomeRecord;

public interface ActualIncomeRecordRepo
		extends JpaRepository<ActualIncomeRecord, String>, JpaSpecificationExecutor<ActualIncomeRecord> {
	
	long deleteByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(Date startTime, Date endTime);
	
	List<ActualIncomeRecord> findByMerchantOrderIdAndAvailableFlagTrue(String merchantOrderId);

	List<ActualIncomeRecord> findBySettlementTimeIsNullAndAvailableFlagTrue();

}
