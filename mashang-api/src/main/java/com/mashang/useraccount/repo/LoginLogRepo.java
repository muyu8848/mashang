package com.mashang.useraccount.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.useraccount.domain.LoginLog;

public interface LoginLogRepo extends JpaRepository<LoginLog, String>, JpaSpecificationExecutor<LoginLog> {
	
	long deleteByLoginTimeGreaterThanEqualAndLoginTimeLessThanEqual(Date startTime, Date endTime);

	LoginLog findTopBySessionIdOrderByLoginTime(String sessionId);

}
