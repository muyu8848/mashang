package com.mashang.merchant.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.merchant.domain.QueueRecord;

public interface QueueRecordRepo extends JpaRepository<QueueRecord, String>, JpaSpecificationExecutor<QueueRecord> {
	
	long deleteByQueueTimeGreaterThanEqualAndQueueTimeLessThanEqual(Date startTime, Date endTime);
	
	List<QueueRecord> findByUserAccountIdAndUsedTrueAndMarkReadFalse(String userAccountId);
	
	QueueRecord findTopByUserAccountIdAndUsedIsFalse(String userAccountId);

	List<QueueRecord> findByUserAccountIdNotInAndUsedIsFalseOrderByQueueTime(List<String> userAccountIds);

	QueueRecord findTopByUserAccountIdAndUsedIsTrueAndMarkReadIsFalse(String userAccountId);
	
	List<QueueRecord> findByUsedIsFalseOrderByQueueTime();
	
	

}
