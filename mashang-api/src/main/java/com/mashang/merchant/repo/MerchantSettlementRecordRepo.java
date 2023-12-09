package com.mashang.merchant.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.merchant.domain.MerchantSettlementRecord;

public interface MerchantSettlementRecordRepo
		extends JpaRepository<MerchantSettlementRecord, String>, JpaSpecificationExecutor<MerchantSettlementRecord> {

	long deleteByApplyTimeGreaterThanEqualAndApplyTimeLessThanEqual(Date startTime, Date endTime);
	
	List<MerchantSettlementRecord> findByState(String state);
	
	MerchantSettlementRecord findByIdAndMerchantId(String id, String merchantId);
	
}
