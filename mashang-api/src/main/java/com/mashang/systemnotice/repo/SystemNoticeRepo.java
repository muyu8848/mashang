package com.mashang.systemnotice.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.systemnotice.domain.SystemNotice;

public interface SystemNoticeRepo extends JpaRepository<SystemNotice, String>, JpaSpecificationExecutor<SystemNotice> {

	SystemNotice findTopByCreateTimeIsLessThanEqualOrderByCreateTimeDesc(Date publishTime);
	
	List<SystemNotice> findTop10ByCreateTimeIsLessThanEqualOrderByCreateTimeDesc(Date publishTime);

}
