package com.mashang.merchant.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.merchant.domain.GatheringChannelRate;

public interface GatheringChannelRateRepo
		extends JpaRepository<GatheringChannelRate, String>, JpaSpecificationExecutor<GatheringChannelRate> {

	GatheringChannelRate findByMerchantIdAndChannelChannelCode(String merchantId, String channelCode);
	
	GatheringChannelRate findByMerchantIdAndChannelId(String merchantId, String channelId);

	List<GatheringChannelRate> findByMerchantId(String merchantId);
}
