package com.mashang.useraccount.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.useraccount.domain.BankCard;

import lombok.Data;

@Data
public class AddOrUpdateBankCardParam {

	private String id;

	@NotBlank
	private String openAccountBank;

	@NotBlank
	private String accountHolder;

	@NotBlank
	private String bankCardAccount;

	public BankCard convertToPo() {
		BankCard po = new BankCard();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setCreateTime(new Date());
		po.setLatelyModifyTime(po.getCreateTime());
		po.setDeletedFlag(false);
		return po;
	}

}
