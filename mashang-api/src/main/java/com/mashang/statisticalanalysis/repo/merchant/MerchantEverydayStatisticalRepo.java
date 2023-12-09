package com.mashang.statisticalanalysis.repo.merchant;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.merchant.MerchantEverydayStatistical;

public interface MerchantEverydayStatisticalRepo extends JpaRepository<MerchantEverydayStatistical, String>,
		JpaSpecificationExecutor<MerchantEverydayStatistical> {

	List<MerchantEverydayStatistical> findByMerchantIdAndEverydayGreaterThanEqualAndEverydayLessThanEqualOrderByEveryday(
			String merchantId, Date startTime, Date endTime);

}
