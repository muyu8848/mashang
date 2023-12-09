package com.mashang.rechargewithdraw.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.rechargewithdraw.domain.WithdrawRecord;

public interface WithdrawRecordRepo
		extends JpaRepository<WithdrawRecord, String>, JpaSpecificationExecutor<WithdrawRecord> {
	
	long deleteBySubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(Date startTime, Date endTime);

	List<WithdrawRecord> findByUserAccountIdAndSubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(
			String userAccountId, Date startTime, Date endTime);

}
