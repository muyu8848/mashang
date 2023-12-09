package com.mashang.income.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.income.domain.IncomeRecord;

public interface IncomeRecordRepo extends JpaRepository<IncomeRecord, String>, JpaSpecificationExecutor<IncomeRecord> {
	
	long deleteByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(Date startTime, Date endTime);

	List<IncomeRecord> findByBizIdAndAvailableFlagTrue(String bizId);
	
	List<IncomeRecord> findBySettlementTimeIsNullAndAvailableFlagTrue();
	
}
