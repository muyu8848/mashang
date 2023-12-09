package com.mashang.mastercontrol.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.mastercontrol.domain.WithdrawSetting;

public interface WithdrawSettingRepo
		extends JpaRepository<WithdrawSetting, String>, JpaSpecificationExecutor<WithdrawSetting> {
	
	WithdrawSetting findTopByOrderByLatelyUpdateTime();

}
