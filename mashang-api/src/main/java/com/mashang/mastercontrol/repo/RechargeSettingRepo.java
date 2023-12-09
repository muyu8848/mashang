package com.mashang.mastercontrol.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.mastercontrol.domain.RechargeSetting;

public interface RechargeSettingRepo
		extends JpaRepository<RechargeSetting, String>, JpaSpecificationExecutor<RechargeSetting> {
	
	RechargeSetting findTopByOrderByLatelyUpdateTime();

}
