package com.mashang.merchant.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.merchant.domain.AccountWaitConfirmOrderNum;

public interface AccountWaitConfirmOrderNumRepo
		extends JpaRepository<AccountWaitConfirmOrderNum, String>, JpaSpecificationExecutor<AccountWaitConfirmOrderNum> {

	AccountWaitConfirmOrderNum findByIdAndWaitConfirmOrderNumIsGreaterThanEqual(String id, Integer waitConfirmOrderNum);
	
	List<AccountWaitConfirmOrderNum> findByIdInAndWaitConfirmOrderNumIsGreaterThanEqual(List<String> ids, Integer waitConfirmOrderNum);
}
