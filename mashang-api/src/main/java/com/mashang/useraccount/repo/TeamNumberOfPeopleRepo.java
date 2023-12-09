package com.mashang.useraccount.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.useraccount.domain.TeamNumberOfPeople;

public interface TeamNumberOfPeopleRepo
		extends JpaRepository<TeamNumberOfPeople, String>, JpaSpecificationExecutor<TeamNumberOfPeople> {

}
