package com.mashang.distributepayout.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.distributepayout.domain.DistributePayoutOrder;

public interface DistributePayoutOrderRepo
		extends JpaRepository<DistributePayoutOrder, String>, JpaSpecificationExecutor<DistributePayoutOrder> {

	DistributePayoutOrder findByIdAndReceivedAccountId(String id, String receivedAccountId);

	DistributePayoutOrder findByOrderNo(String orderNo);

	List<DistributePayoutOrder> findByOrderStateAndSettlementTimeIsNull(String orderState);
	
	List<DistributePayoutOrder> findByOrderStateOrderByCreateTimeAsc(String orderState);
	
	List<DistributePayoutOrder> findByMerchantSettlementRecordIdAndReceivedAccountId(String merchantSettlementRecordId, String receivedAccountId);

}
