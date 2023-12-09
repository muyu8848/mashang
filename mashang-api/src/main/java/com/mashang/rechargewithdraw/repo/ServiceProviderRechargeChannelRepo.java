package com.mashang.rechargewithdraw.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.rechargewithdraw.domain.ServiceProviderRechargeChannel;

public interface ServiceProviderRechargeChannelRepo extends JpaRepository<ServiceProviderRechargeChannel, String>,
		JpaSpecificationExecutor<ServiceProviderRechargeChannel> {

	List<ServiceProviderRechargeChannel> findByUserAccountIdInAndDeletedFlagIsFalse(List<String> userAccountIds);
	
	ServiceProviderRechargeChannel findByIdAndUserAccountId(String id, String userAccountId);

}
