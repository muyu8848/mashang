package com.mashang.statisticalanalysis.repo.merchant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.merchant.MerchantTradeSituation;

public interface MerchantTradeSituationRepo
		extends JpaRepository<MerchantTradeSituation, String>, JpaSpecificationExecutor<MerchantTradeSituation> {

}
