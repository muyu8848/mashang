package com.mashang.merchant.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.merchant.domain.Merchant;

public interface MerchantRepo extends JpaRepository<Merchant, String>, JpaSpecificationExecutor<Merchant> {

	Merchant findByMerchantNameAndDeletedFlagIsFalse(String merchantName);

	Merchant findByUserNameAndDeletedFlagIsFalse(String userName);

	List<Merchant> findByDeletedFlagIsFalse();
	
	List<Merchant> findByAccountTypeAndDeletedFlagIsFalse(String accountType);
	
	Merchant findByIdAndDeletedFlagIsFalse(String id);
}
