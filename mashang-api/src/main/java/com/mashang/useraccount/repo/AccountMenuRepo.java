package com.mashang.useraccount.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.useraccount.domain.AccountMenu;

public interface AccountMenuRepo extends JpaRepository<AccountMenu, String>, JpaSpecificationExecutor<AccountMenu> {
	
	List<AccountMenu> findByUserAccountIdAndMenuDeletedFlagFalseOrderByMenuOrderNo(String userAccountId);

}
