package com.mashang.gatheringcode.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.gatheringcode.domain.GatheringCode;

public interface GatheringCodeRepo
		extends JpaRepository<GatheringCode, String>, JpaSpecificationExecutor<GatheringCode> {

	long deleteByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(Date startTime, Date endTime);

	List<GatheringCode> findByUserAccountIdAndDeletedFlagFalse(String userAccountId);
	
	List<GatheringCode> findByUserAccountIdAndIdInAndDeletedFlagFalse(String userAccountId, List<String> gatheringCodeIds);
	
	List<GatheringCode> findByUserAccountIdInAndGatheringChannelIdAndStateAndInUseTrueAndDeletedFlagFalse(List<String> userAccountId,
			String gatheringChannelId, String state);
	
	List<GatheringCode> findByUserAccountIdAndStateAndDeletedFlagFalse(String userAccountId, String state);
	
	List<GatheringCode> findByUserAccountIdAndGatheringChannelChannelCodeInAndStateAndInUseTrueAndDeletedFlagFalse(
			String userAccountId, List<String> gatheringChannelCodes, String state);
	
}
