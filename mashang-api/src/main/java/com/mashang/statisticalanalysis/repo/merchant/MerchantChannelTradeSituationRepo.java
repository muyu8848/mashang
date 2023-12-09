package com.mashang.statisticalanalysis.repo.merchant;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.merchant.MerchantChannelTradeSituation;

public interface MerchantChannelTradeSituationRepo extends JpaRepository<MerchantChannelTradeSituation, String>,
		JpaSpecificationExecutor<MerchantChannelTradeSituation> {

	List<MerchantChannelTradeSituation> findByMerchantId(String merchantId);
}
