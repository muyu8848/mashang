package com.mashang.mastercontrol.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.mastercontrol.domain.ReceiveOrderRiskSetting;

public interface ReceiveOrderRiskSettingRepo
		extends JpaRepository<ReceiveOrderRiskSetting, String>, JpaSpecificationExecutor<ReceiveOrderRiskSetting> {

	ReceiveOrderRiskSetting findTopByOrderByLatelyUpdateTime();

}
