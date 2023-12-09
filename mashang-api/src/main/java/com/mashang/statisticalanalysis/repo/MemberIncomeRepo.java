package com.mashang.statisticalanalysis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.MemberIncome;

public interface MemberIncomeRepo extends JpaRepository<MemberIncome, String>, JpaSpecificationExecutor<MemberIncome> {

}
