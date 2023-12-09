package com.mashang.rechargewithdraw.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.rechargewithdraw.domain.RechargeOrder;

public interface RechargeOrderRepo
		extends JpaRepository<RechargeOrder, String>, JpaSpecificationExecutor<RechargeOrder> {

	long deleteBySubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(Date startTime, Date endTime);

	RechargeOrder findByOrderNo(String orderNo);

	List<RechargeOrder> findByOrderStateAndSettlementTimeIsNull(String orderState);

	List<RechargeOrder> findByOrderStateAndUserAccountId(String orderState, String userAccountId);

	RechargeOrder findByIdAndUserAccountId(String id, String userAccountId);

	List<RechargeOrder> findByOrderStateAndServiceProviderId(String orderState, String serviceProviderId);

	RechargeOrder findByIdAndServiceProviderId(String id, String serviceProviderId);

}
