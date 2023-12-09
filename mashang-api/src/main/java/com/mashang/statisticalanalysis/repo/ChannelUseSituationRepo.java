package com.mashang.statisticalanalysis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.statisticalanalysis.domain.ChannelUseSituation;

public interface ChannelUseSituationRepo
		extends JpaRepository<ChannelUseSituation, String>, JpaSpecificationExecutor<ChannelUseSituation> {

}
