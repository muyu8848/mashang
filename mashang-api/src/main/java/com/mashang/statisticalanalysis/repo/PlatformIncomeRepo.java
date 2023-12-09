package com.mashang.statisticalanalysis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.PlatformIncome;

public interface PlatformIncomeRepo
		extends JpaRepository<PlatformIncome, String>, JpaSpecificationExecutor<PlatformIncome> {
	
	PlatformIncome findTopBy();

}
