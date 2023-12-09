package com.mashang.statisticalanalysis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.CashDeposit;

public interface CashDepositRepo
		extends JpaRepository<CashDeposit, String>, JpaSpecificationExecutor<CashDeposit> {
	
	CashDeposit findTopBy();

}
