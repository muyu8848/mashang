package com.mashang.dataclean.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mashang.common.valid.ParamValid;
import com.mashang.dataclean.param.DataCleanParam;
import com.mashang.gatheringcode.repo.GatheringCodeRepo;
import com.mashang.income.repo.IncomeRecordRepo;
import com.mashang.merchant.repo.ActualIncomeRecordRepo;
import com.mashang.merchant.repo.FreezeRecordRepo;
import com.mashang.merchant.repo.MerchantAccountChangeLogRepo;
import com.mashang.merchant.repo.MerchantOrderRepo;
import com.mashang.merchant.repo.MerchantSettlementRecordRepo;
import com.mashang.merchant.repo.QueueRecordRepo;
import com.mashang.rechargewithdraw.repo.RechargeOrderRepo;
import com.mashang.rechargewithdraw.repo.WithdrawRecordRepo;
import com.mashang.useraccount.repo.AccountChangeLogRepo;
import com.mashang.useraccount.repo.LoginLogRepo;
import com.mashang.useraccount.repo.OperLogRepo;

import cn.hutool.core.date.DateUtil;

@Service
public class DataCleanService {

	@Autowired
	private MerchantOrderRepo merchantOrderRepo;

	@Autowired
	private ActualIncomeRecordRepo actualIncomeRecordRepo;

	@Autowired
	private MerchantSettlementRecordRepo merchantSettlementRecordRepo;

	@Autowired
	private QueueRecordRepo queueRecordRepo;

	@Autowired
	private RechargeOrderRepo rechargeOrderRepo;

	@Autowired
	private WithdrawRecordRepo withdrawRecordRepo;

	@Autowired
	private GatheringCodeRepo gatheringCodeRepo;

	@Autowired
	private AccountChangeLogRepo accountChangeLogRepo;

	@Autowired
	private LoginLogRepo loginLogRepo;

	@Autowired
	private FreezeRecordRepo freezeRecordRepo;

	@Autowired
	private OperLogRepo operLogRepo;
	
	@Autowired
	private MerchantAccountChangeLogRepo merchantAccountChangeLogRepo;
	
	@Autowired
	private IncomeRecordRepo incomeRecordRepo;

	@ParamValid
	@Transactional
	public void dataClean(DataCleanParam param) {
		List<String> dataTypes = param.getDataTypes();
		Date startTime = DateUtil.beginOfDay(param.getStartTime());
		Date endTime = DateUtil.endOfDay(param.getEndTime());
		if (dataTypes.contains("merchantOrder")) {
			actualIncomeRecordRepo.deleteByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(startTime, endTime);
			freezeRecordRepo.deleteByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(startTime, endTime);
			merchantOrderRepo.deleteBySubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(startTime, endTime);
		}
		if (dataTypes.contains("incomeRecord")) {
			incomeRecordRepo.deleteByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(startTime, endTime);
		}
		if (dataTypes.contains("merchantSettlementRecord")) {
			merchantSettlementRecordRepo.deleteByApplyTimeGreaterThanEqualAndApplyTimeLessThanEqual(startTime, endTime);
		}
		if (dataTypes.contains("queueRecord")) {
			queueRecordRepo.deleteByQueueTimeGreaterThanEqualAndQueueTimeLessThanEqual(startTime, endTime);
		}
		if (dataTypes.contains("rechargeOrder")) {
			rechargeOrderRepo.deleteBySubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(startTime, endTime);
		}
		if (dataTypes.contains("withdrawRecord")) {
			withdrawRecordRepo.deleteBySubmitTimeGreaterThanEqualAndSubmitTimeLessThanEqual(startTime, endTime);
		}
		if (dataTypes.contains("gatheringCode")) {
			gatheringCodeRepo.deleteByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(startTime, endTime);
		}
		if (dataTypes.contains("accountChangeLog")) {
			accountChangeLogRepo.deleteByAccountChangeTimeGreaterThanEqualAndAccountChangeTimeLessThanEqual(startTime,
					endTime);
		}
		if (dataTypes.contains("merchantAccountChangeLog")) {
			merchantAccountChangeLogRepo.deleteByAccountChangeTimeGreaterThanEqualAndAccountChangeTimeLessThanEqual(startTime,
					endTime);
		}
		if (dataTypes.contains("loginLog")) {
			loginLogRepo.deleteByLoginTimeGreaterThanEqualAndLoginTimeLessThanEqual(startTime, endTime);
		}
		if (dataTypes.contains("operLog")) {
			operLogRepo.deleteByOperTimeGreaterThanEqualAndOperTimeLessThanEqual(startTime, endTime);
		}
	}

}
