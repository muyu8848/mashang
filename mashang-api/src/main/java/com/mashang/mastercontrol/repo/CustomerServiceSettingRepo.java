package com.mashang.mastercontrol.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.mastercontrol.domain.CustomerServiceSetting;

public interface CustomerServiceSettingRepo
		extends JpaRepository<CustomerServiceSetting, String>, JpaSpecificationExecutor<CustomerServiceSetting> {

	CustomerServiceSetting findTopByOrderByLatelyUpdateTime();

}
