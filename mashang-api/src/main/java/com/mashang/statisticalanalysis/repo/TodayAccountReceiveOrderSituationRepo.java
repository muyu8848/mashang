package com.mashang.statisticalanalysis.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.TodayAccountReceiveOrderSituation;

public interface TodayAccountReceiveOrderSituationRepo extends JpaRepository<TodayAccountReceiveOrderSituation, String>, JpaSpecificationExecutor<TodayAccountReceiveOrderSituation> {
	
	List<TodayAccountReceiveOrderSituation> findTop10ByOrderByBountyDesc();
	
	TodayAccountReceiveOrderSituation findByReceivedAccountId(String receivedAccountId);

}
