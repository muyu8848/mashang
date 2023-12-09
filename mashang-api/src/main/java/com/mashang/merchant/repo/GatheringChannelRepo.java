package com.mashang.merchant.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.merchant.domain.GatheringChannel;

public interface GatheringChannelRepo
		extends JpaRepository<GatheringChannel, String>, JpaSpecificationExecutor<GatheringChannel> {

	GatheringChannel findByChannelCodeAndDeletedFlagIsFalse(String channelCode);

	List<GatheringChannel> findByDeletedFlagIsFalse();

	List<GatheringChannel> findByEnabledAndDeletedFlagIsFalse(Boolean enabled);
	
	GatheringChannel findByIdAndDeletedFlagIsFalse(String id);

}
