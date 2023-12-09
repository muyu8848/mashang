package com.mashang.merchant.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.merchant.domain.FreezeRecord;

public interface FreezeRecordRepo extends JpaRepository<FreezeRecord, String>, JpaSpecificationExecutor<FreezeRecord> {
	
	long deleteByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(Date startTime, Date endTime);

	List<FreezeRecord> findByDealTimeIsNullAndUsefulTimeLessThan(Date usefulTime);

	List<FreezeRecord> findByDealTimeIsNull();

	List<FreezeRecord> findByDealTimeIsNullAndReceivedAccountId(String receivedAccountId);

	FreezeRecord findTopByMerchantOrderId(String merchantOrderId);

}
