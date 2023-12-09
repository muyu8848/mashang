package com.mashang.statisticalanalysis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.CollectForAnotherIncomeSituation;

public interface CollectForAnotherIncomeSituationRepo extends JpaRepository<CollectForAnotherIncomeSituation, String>,
		JpaSpecificationExecutor<CollectForAnotherIncomeSituation> {

	CollectForAnotherIncomeSituation findTopBy();

}
