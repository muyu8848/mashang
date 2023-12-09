package com.mashang.mastercontrol.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.mastercontrol.domain.MerchantSettlementSetting;

public interface MerchantSettlementSettingRepo
		extends JpaRepository<MerchantSettlementSetting, String>, JpaSpecificationExecutor<MerchantSettlementSetting> {
	
	MerchantSettlementSetting findTopByOrderByLatelyUpdateTime();

}
