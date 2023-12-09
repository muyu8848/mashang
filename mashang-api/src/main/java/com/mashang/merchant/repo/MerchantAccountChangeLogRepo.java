package com.mashang.merchant.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.merchant.domain.MerchantAccountChangeLog;

public interface MerchantAccountChangeLogRepo
		extends JpaRepository<MerchantAccountChangeLog, String>, JpaSpecificationExecutor<MerchantAccountChangeLog> {
	
	long deleteByAccountChangeTimeGreaterThanEqualAndAccountChangeTimeLessThanEqual(Date startTime, Date endTime);

}
