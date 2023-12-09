package com.mashang.useraccount.vo;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mashang.merchant.vo.LoginMerchantInfoVO;

public class MerchantAccountDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;

	private String userName;

	private String loginPwd;

	private String merchantName;

	public MerchantAccountDetails(LoginMerchantInfoVO loginMerchantInfo) {
		if (loginMerchantInfo != null) {
			this.id = loginMerchantInfo.getId();
			this.userName = loginMerchantInfo.getUserName();
			this.loginPwd = loginMerchantInfo.getLoginPwd();
			this.merchantName = loginMerchantInfo.getMerchantName();
		}
	}

	public String getMerchantId() {
		return this.id;
	}

	/**
	 * 获取登陆用户账号商户名
	 * 
	 * @return
	 */
	public String getMerchantName() {
		return this.merchantName;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return this.loginPwd;
	}

	@Override
	public String getUsername() {
		return this.userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
