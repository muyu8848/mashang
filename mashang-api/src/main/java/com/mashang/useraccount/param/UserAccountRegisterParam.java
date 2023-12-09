package com.mashang.useraccount.param;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.constants.Constant;
import com.mashang.useraccount.domain.UserAccount;

import lombok.Data;

@Data
public class UserAccountRegisterParam {

	private String inviteCode;

	@NotBlank
	private String userName;

	@NotBlank
	private String realName;

	@NotBlank
	private String mobile;

//	@NotBlank
//	private String smsCode;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z][A-Za-z0-9]{5,14}$")
	private String loginPwd;

	@NotBlank
	private String moneyPwd;

	public UserAccount convertToPo() {
		UserAccount po = new UserAccount();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setInviteCode(po.getId().substring(po.getId().length() - 6, po.getId().length()));
		po.setState(Constant.账号状态_启用);
		po.setDeletedFlag(false);
		po.setRegisteredTime(new Date());
		po.setAccountType(Constant.账号类型_会员);
		po.setReceiveOrderState(Constant.接单状态_停止接单);
		po.setCashDeposit(0d);
		po.setFreezeAmount(0d);
		po.setServiceProviderAmount(0d);
		po.setServiceProviderFreezeAmount(0d);
		po.setAccountLevel(1);
		po.setAccountLevelPath(po.getId());
		return po;
	}

}
