package com.mashang.statisticalanalysis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.MemberEverydayIncome;

public interface MemberEverydayIncomeRepo extends JpaRepository<MemberEverydayIncome, String>, JpaSpecificationExecutor<MemberEverydayIncome> {

}
