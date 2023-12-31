package com.mashang.mastercontrol.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.mastercontrol.domain.ReceiveOrderSetting;

public interface ReceiveOrderSettingRepo
		extends JpaRepository<ReceiveOrderSetting, String>, JpaSpecificationExecutor<ReceiveOrderSetting> {

	ReceiveOrderSetting findTopByOrderByLatelyUpdateTime();

}
