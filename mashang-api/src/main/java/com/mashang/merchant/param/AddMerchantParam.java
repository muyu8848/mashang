package com.mashang.merchant.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.constants.Constant;
import com.mashang.merchant.domain.Merchant;

import lombok.Data;

@Data
public class AddMerchantParam {

	@NotBlank
	private String userName;

	@NotBlank
	private String loginPwd;

	@NotBlank
	private String merchantName;

	@NotBlank
	private String accountType;

	private String inviterUserName;

	@NotBlank
	private String secretKey;

	public Merchant convertToPo() {
		Merchant po = new Merchant();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setDeletedFlag(false);
		po.setAccountLevel(0);
		po.setAccountLevelPath(po.getId());
		po.setCreateTime(new Date());
		po.setWithdrawableAmount(0d);
		po.setFreezeFund(0d);
		po.setMoneyPwd(po.getLoginPwd());
		po.setState(Constant.账号状态_启用);
		return po;
	}

}
