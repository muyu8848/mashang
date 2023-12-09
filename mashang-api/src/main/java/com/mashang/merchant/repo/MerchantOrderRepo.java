package com.mashang.merchant.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.merchant.domain.MerchantOrder;

public interface MerchantOrderRepo
		extends JpaRepository<MerchantOrder, String>, JpaSpecificationExecutor<MerchantOrder> {

	long countByOrderNo(String orderNo);

	List<MerchantOrder> findByIpAndOrderStateIn(String ip, List<String> orderStates);

	List<MerchantOrder> findByPayerNameAndOrderState(String payerName, String orderState);

	List<MerchantOrder> findByOrderStateAndReceivedAccountId(String orderState, String receivedAccountId);

	List<MerchantOrder> findByOrderState(String orderState);

	List<MerchantOrder> findByOrderStateAndGatheringChannelChannelCodeIn(String orderState, List<String> channelCodes);

	List<MerchantOrder> findByOrderStateAndReceivedTimeLessThan(String orderState, Date receivedTime);

	long deleteBySubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(Date startTime, Date endTime);

	List<MerchantOrder> findByOrderStateInAndReceivedAccountIdOrderBySubmitTimeDesc(List<String> orderStates,
			String receivedAccountId);

	MerchantOrder findByIdAndReceivedAccountId(String id, String receivedAccountId);

	MerchantOrder findByIdAndMerchantId(String id, String merchantId);

	MerchantOrder findTopByOrderNoOrderBySubmitTimeDesc(String orderNo);

	List<MerchantOrder> findByOrderStateAndUsefulTimeLessThan(String orderState, Date usefulTime);

	MerchantOrder findTopByGatheringCodeIdOrderByReceivedTimeDesc(String gatheringCodeId);

	List<MerchantOrder> findByOrderStateAndReceivedAccountIdInAndGatheringAmountOrderBySubmitTimeDesc(String orderState,
			List<String> receivedAccountIds, Double gatheringAmount);

	MerchantOrder findTopByMerchantOrderNoOrderBySubmitTimeDesc(String merchantOrderNo);

	List<MerchantOrder> findByNoticeStateAndOrderStateIn(String noticeState, List<String> orderStates);

}
