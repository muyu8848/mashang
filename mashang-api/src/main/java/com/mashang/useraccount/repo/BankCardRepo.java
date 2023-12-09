package com.mashang.useraccount.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.mashang.useraccount.domain.BankCard;

public interface BankCardRepo extends JpaRepository<BankCard, String>, JpaSpecificationExecutor<BankCard> {

	List<BankCard> findByUserAccountIdAndDeletedFlagFalse(String userAccountId);

	BankCard findByIdAndUserAccountId(String id, String userAccountId);

}
