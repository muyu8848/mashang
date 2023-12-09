package com.mashang.useraccount.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.useraccount.domain.OperLog;

public interface OperLogRepo extends JpaRepository<OperLog, String>, JpaSpecificationExecutor<OperLog> {
	
	long deleteByOperTimeGreaterThanEqualAndOperTimeLessThanEqual(Date startTime, Date endTime);

}
