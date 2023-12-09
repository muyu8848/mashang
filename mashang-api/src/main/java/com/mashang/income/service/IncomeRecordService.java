package com.mashang.income.service;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.mashang.constants.Constant;
import com.mashang.income.domain.IncomeRecord;
import com.mashang.income.repo.IncomeRecordRepo;
import com.mashang.useraccount.domain.AccountChangeLog;
import com.mashang.useraccount.domain.UserAccount;
import com.mashang.useraccount.repo.AccountChangeLogRepo;
import com.mashang.useraccount.repo.UserAccountRepo;

import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
public class IncomeRecordService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private UserAccountRepo userAccountRepo;

	@Autowired
	private IncomeRecordRepo incomeRecordRepo;

	@Autowired
	private AccountChangeLogRepo accountChangeLogRepo;

	@Transactional(readOnly = true)
	public void autoNoticeIncomeRecordSettlement() {
		List<IncomeRecord> incomeRecords = incomeRecordRepo.findBySettlementTimeIsNullAndAvailableFlagTrue();
		for (IncomeRecord incomeRecord : incomeRecords) {
			redisTemplate.opsForList().leftPush(Constant.收益记录ID, incomeRecord.getId());
		}
	}

	@Transactional(readOnly = true)
	public void noticeIncomeSettlement(@NotBlank String orderId) {
		List<IncomeRecord> incomeRecords = incomeRecordRepo.findByBizIdAndAvailableFlagTrue(orderId);
		for (IncomeRecord incomeRecord : incomeRecords) {
			redisTemplate.opsForList().leftPush(Constant.收益记录ID, incomeRecord.getId());
		}
	}

	@Transactional
	public void incomeSettlement(@NotBlank String orderIncomeId) {
		IncomeRecord incomeRecord = incomeRecordRepo.getOne(orderIncomeId);
		if (incomeRecord.getSettlementTime() != null) {
			log.warn("当前的收益已结算,无法重复结算;id:{}", orderIncomeId);
			return;
		}
		incomeRecord.settlement();
		incomeRecordRepo.save(incomeRecord);
		UserAccount userAccount = incomeRecord.getUserAccount();
		double income = userAccount.getCashDeposit() + incomeRecord.getIncome();
		userAccount.setCashDeposit(NumberUtil.round(income, 2).doubleValue());
		userAccountRepo.save(userAccount);
		if (Constant.收益类型_代收收益.equals(incomeRecord.getIncomeType())) {
			accountChangeLogRepo.save(AccountChangeLog.buildWithCollectForAnotherIncome(userAccount, incomeRecord));
		}
		if (Constant.收益类型_代收团队收益.equals(incomeRecord.getIncomeType())) {
			accountChangeLogRepo.save(AccountChangeLog.buildWithCollectForAnotherTeamIncome(userAccount, incomeRecord));
		}
		if (Constant.收益类型_下发代付收益.equals(incomeRecord.getIncomeType())) {
			accountChangeLogRepo.save(AccountChangeLog.buildWithDistributePayoutIncome(userAccount, incomeRecord));
		}
		if (Constant.收益类型_代充收益.equals(incomeRecord.getIncomeType())) {
			accountChangeLogRepo
					.save(AccountChangeLog.buildWithServiceProviderRechargeIncome(userAccount, incomeRecord));
		}
	}

}
