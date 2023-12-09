package com.mashang.useraccount.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.useraccount.domain.AccountRole;

public interface AccountRoleRepo extends JpaRepository<AccountRole, String>, JpaSpecificationExecutor<AccountRole> {
	
	List<AccountRole> findByUserAccountId(String userAccountId);
	
	List<AccountRole> findByUserAccountIdAndRoleDeletedFlagFalse(String userAccountId);

}
