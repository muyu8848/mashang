package com.mashang.gatheringcode.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.gatheringcode.domain.GatheringCodeUsage;

public interface GatheringCodeUsageRepo
		extends JpaRepository<GatheringCodeUsage, String>, JpaSpecificationExecutor<GatheringCodeUsage> {


}
