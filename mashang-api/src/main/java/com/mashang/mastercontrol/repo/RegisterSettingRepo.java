package com.mashang.mastercontrol.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.mastercontrol.domain.RegisterSetting;

public interface RegisterSettingRepo
		extends JpaRepository<RegisterSetting, String>, JpaSpecificationExecutor<RegisterSetting> {

	RegisterSetting findTopByOrderByLatelyUpdateTime();
	
}
