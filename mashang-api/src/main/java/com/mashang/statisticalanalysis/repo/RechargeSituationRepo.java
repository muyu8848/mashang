package com.mashang.statisticalanalysis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.RechargeSituation;

public interface RechargeSituationRepo
		extends JpaRepository<RechargeSituation, String>, JpaSpecificationExecutor<RechargeSituation> {

	RechargeSituation findTopBy();

}
