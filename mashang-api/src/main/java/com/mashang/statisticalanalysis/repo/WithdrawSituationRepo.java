package com.mashang.statisticalanalysis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.WithdrawSituation;

public interface WithdrawSituationRepo
		extends JpaRepository<WithdrawSituation, String>, JpaSpecificationExecutor<WithdrawSituation> {

	WithdrawSituation findTopBy();

}
