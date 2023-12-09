package com.mashang.useraccount.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.constants.Constant;
import com.mashang.useraccount.domain.UserAccount;

import lombok.Data;

@Data
public class AddUserAccountParam {

	private String inviterUserName;

	@NotBlank
	private String userName;

	@NotBlank
	private String realName;
	
	private String mobile;

	private String accountType;

	@NotBlank
	private String loginPwd;

	public UserAccount convertToPo() {
		UserAccount po = new UserAccount();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setInviteCode(po.getId().substring(po.getId().length() - 6, po.getId().length()));
		po.setState(Constant.账号状态_启用);
		po.setDeletedFlag(false);
		po.setAccountLevel(0);
		po.setAccountLevelPath(po.getId());
		po.setCashDeposit(0d);
		po.setFreezeAmount(0d);
		po.setServiceProviderAmount(0d);
		po.setServiceProviderFreezeAmount(0d);
		po.setRegisteredTime(new Date());
		po.setMoneyPwd(po.getLoginPwd());
		po.setReceiveOrderState(Constant.接单状态_停止接单);
		return po;
	}

}
