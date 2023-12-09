package com.mashang.rechargewithdraw.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.rechargewithdraw.domain.RechargeChannel;

public interface RechargeChannelRepo extends JpaRepository<RechargeChannel, String>, JpaSpecificationExecutor<RechargeChannel> {

	List<RechargeChannel> findByEnabledIsTrueAndDeletedFlagIsFalseOrderByOrderNoAsc();
	
	List<RechargeChannel> findByDeletedFlagIsFalseOrderByOrderNoAsc();

}
