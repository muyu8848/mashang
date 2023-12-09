package com.mashang.merchant.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.merchant.domain.MerchantFreezeFundRecord;

public interface MerchantFreezeFundRecordRepo
		extends JpaRepository<MerchantFreezeFundRecord, String>, JpaSpecificationExecutor<MerchantFreezeFundRecord> {
	
	List<MerchantFreezeFundRecord> findByMerchantIdAndReleaseFlagFalse(String merchantId);

}
