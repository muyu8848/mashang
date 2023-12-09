package com.mashang.statisticalanalysis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.AdjustCashDepositSituation;

public interface AdjustCashDepositSituationRepo
		extends JpaRepository<AdjustCashDepositSituation, String>, JpaSpecificationExecutor<AdjustCashDepositSituation> {

	AdjustCashDepositSituation findTopBy();

}
