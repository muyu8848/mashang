package com.mashang.statisticalanalysis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.TradeSituation;

public interface TradeSituationRepo
		extends JpaRepository<TradeSituation, String>, JpaSpecificationExecutor<TradeSituation> {

	TradeSituation findTopBy();

}
