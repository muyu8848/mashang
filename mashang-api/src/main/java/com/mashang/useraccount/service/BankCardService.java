package com.mashang.useraccount.service;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.mashang.common.exception.BizError;
import com.mashang.common.exception.BizException;
import com.mashang.common.valid.ParamValid;
import com.mashang.useraccount.domain.BankCard;
import com.mashang.useraccount.param.AddOrUpdateBankCardParam;
import com.mashang.useraccount.repo.BankCardRepo;
import com.mashang.useraccount.vo.BankCardVO;

import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class BankCardService {

	@Autowired
	private BankCardRepo bankCardRepo;

	@Transactional(readOnly = true)
	public BankCardVO findMyBankCardById(@NotBlank String id, @NotBlank String userAccountId) {
		return BankCardVO.convertFor(bankCardRepo.findByIdAndUserAccountId(id, userAccountId));
	}

	@Transactional(readOnly = true)
	public List<BankCardVO> findBankCard(@NotBlank String userAccountId) {
		return BankCardVO.convertFor(bankCardRepo.findByUserAccountIdAndDeletedFlagFalse(userAccountId));
	}

	@Transactional
	public void delBankCard(@NotBlank String id) {
		BankCard bankCard = bankCardRepo.getOne(id);
		delBankCard(bankCard.getId(), bankCard.getUserAccountId());
	}

	@Transactional
	public void delBankCard(@NotBlank String id, @NotBlank String userAccountId) {
		BankCard bankCard = bankCardRepo.findByIdAndUserAccountId(id, userAccountId);
		bankCard.deleted();
		bankCardRepo.save(bankCard);
	}

	@ParamValid
	@Transactional
	public String addOrUpdateBankCard(AddOrUpdateBankCardParam param, String userAccountId) {
		// 新增
		if (StrUtil.isBlank(param.getId())) {
			BankCard bankCard = param.convertToPo();
			bankCard.setUserAccountId(userAccountId);
			bankCardRepo.save(bankCard);
			return bankCard.getId();
		}
		// 修改
		else {
			BankCard bankCard = bankCardRepo.getOne(param.getId());
			if (!bankCard.getUserAccountId().equals(userAccountId)) {
				throw new BizException(BizError.无权修改银行卡);
			}
			BeanUtils.copyProperties(param, bankCard);
			bankCard.setLatelyModifyTime(new Date());
			bankCardRepo.save(bankCard);
			return bankCard.getId();
		}
	}

}
