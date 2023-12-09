package com.mashang.useraccount.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.mashang.common.auth.GoogleAuthInfoVO;
import com.mashang.common.auth.GoogleAuthenticator;
import com.mashang.common.exception.BizError;
import com.mashang.common.exception.BizException;
import com.mashang.common.valid.ParamValid;
import com.mashang.common.vo.PageResult;
import com.mashang.constants.Constant;
import com.mashang.gatheringcode.domain.GatheringCode;
import com.mashang.gatheringcode.repo.GatheringCodeRepo;
import com.mashang.mastercontrol.domain.RegisterSetting;
import com.mashang.mastercontrol.repo.RegisterSettingRepo;
import com.mashang.merchant.domain.GatheringChannel;
import com.mashang.merchant.domain.QueueRecord;
import com.mashang.merchant.repo.GatheringChannelRepo;
import com.mashang.merchant.repo.QueueRecordRepo;
import com.mashang.useraccount.domain.AccountChangeLog;
import com.mashang.useraccount.domain.AccountReceiveOrderChannel;
import com.mashang.useraccount.domain.TeamNumberOfPeople;
import com.mashang.useraccount.domain.UserAccount;
import com.mashang.useraccount.param.AccountChangeLogQueryCondParam;
import com.mashang.useraccount.param.AddUserAccountParam;
import com.mashang.useraccount.param.AdjustLowerLevelPointParam;
import com.mashang.useraccount.param.BindMobileParam;
import com.mashang.useraccount.param.ExchangeQuotaParam;
import com.mashang.useraccount.param.ForgetPwdAndModifyPwdParam;
import com.mashang.useraccount.param.Level1MemberQueryCondParam;
import com.mashang.useraccount.param.ModifyLoginPwdParam;
import com.mashang.useraccount.param.ModifyMoneyPwdParam;
import com.mashang.useraccount.param.UserAccountEditParam;
import com.mashang.useraccount.param.UserAccountQueryCondParam;
import com.mashang.useraccount.param.UserAccountRegisterParam;
import com.mashang.useraccount.repo.AccountChangeLogRepo;
import com.mashang.useraccount.repo.AccountReceiveOrderChannelRepo;
import com.mashang.useraccount.repo.TeamNumberOfPeopleRepo;
import com.mashang.useraccount.repo.UserAccountRepo;
import com.mashang.useraccount.vo.AccountChangeLogVO;
import com.mashang.useraccount.vo.LoginAccountInfoVO;
import com.mashang.useraccount.vo.MemberAccountInfoVO;
import com.mashang.useraccount.vo.TeamNumberOfPeopleVO;
import com.mashang.useraccount.vo.UserAccountDetailsInfoVO;
import com.mashang.useraccount.vo.UserAccountInfoVO;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@Service
public class UserAccountService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private UserAccountRepo userAccountRepo;

	@Autowired
	private AccountChangeLogRepo accountChangeLogRepo;

	@Autowired
	private RegisterSettingRepo registerSettingRepo;

	@Autowired
	private AccountReceiveOrderChannelRepo accountReceiveOrderChannelRepo;

	@Autowired
	private QueueRecordRepo queueRecordRepo;

	@Autowired
	private GatheringCodeRepo gatheringCodeRepo;

	@Autowired
	private TeamNumberOfPeopleRepo teamNumberOfPeopleRepo;

	@Autowired
	private GatheringChannelRepo gatheringChannelRepo;

	@ParamValid
	@Lock(keys = "'exchangeQuota_' + #param.userAccountId")
	@Transactional
	public void exchangeQuota(ExchangeQuotaParam param) {
		UserAccount userAccount = userAccountRepo.getOne(param.getUserAccountId());
		if (Constant.会员额度类型_余额.equals(param.getTransferOutType()) && userAccount.getCashDeposit() <= 0) {
			throw new BizException("余额不足");
		}
		if (Constant.会员额度类型_代充余额.equals(param.getTransferOutType()) && userAccount.getServiceProviderAmount() <= 0) {
			throw new BizException("余额不足");
		}
		if (Constant.会员额度类型_余额.equals(param.getTransferOutType())
				&& Constant.会员额度类型_代充余额.equals(param.getTransferInType())) {
			double balance = NumberUtil.round(userAccount.getCashDeposit() - param.getQuantity(), 2).doubleValue();
			if (balance < 0) {
				throw new BizException("余额不足");
			}
			double serviceProviderAmount = NumberUtil
					.round(userAccount.getServiceProviderAmount() + param.getQuantity(), 2).doubleValue();
			userAccount.setCashDeposit(balance);
			userAccount.setServiceProviderAmount(serviceProviderAmount);
			userAccountRepo.save(userAccount);
			accountChangeLogRepo.save(AccountChangeLog.buildWithBalanceTransferOutServiceProviderAmount(userAccount,
					param.getQuantity()));
		} else if (Constant.会员额度类型_代充余额.equals(param.getTransferOutType())
				&& Constant.会员额度类型_余额.equals(param.getTransferInType())) {
			double serviceProviderAmount = NumberUtil
					.round(userAccount.getServiceProviderAmount() - param.getQuantity(), 2).doubleValue();
			if (serviceProviderAmount < 0) {
				throw new BizException("余额不足");
			}
			double balance = NumberUtil.round(userAccount.getCashDeposit() + param.getQuantity(), 2).doubleValue();
			userAccount.setServiceProviderAmount(serviceProviderAmount);
			userAccount.setCashDeposit(balance);
			userAccountRepo.save(userAccount);
			accountChangeLogRepo.save(
					AccountChangeLog.buildWithServiceProviderAmountTransferInBalance(userAccount, param.getQuantity()));
		}
	}

	@ParamValid
	@Transactional
	public void adjustLowerLevelPoint(String superiorAccountId, @NotEmpty List<AdjustLowerLevelPointParam> params) {
		UserAccount superiorAccount = userAccountRepo.getOne(superiorAccountId);
		for (AdjustLowerLevelPointParam param : params) {
			AccountReceiveOrderChannel accountReceiveOrderChannel = accountReceiveOrderChannelRepo
					.getOne(param.getId());
			UserAccount userAccount = accountReceiveOrderChannel.getUserAccount();
			if (!userAccount.getInviterId().startsWith(superiorAccount.getId())) {
				throw new BizException("无权调整该账号的点数");
			}
			AccountReceiveOrderChannel superiorChannel = accountReceiveOrderChannelRepo.findByUserAccountIdAndChannelId(
					superiorAccount.getId(), accountReceiveOrderChannel.getChannelId());
			if (superiorChannel == null) {
				throw new BizException(BizError.参数异常 + "", MessageFormat.format("[{0}]通道未设置,无法调整下级的点数",
						accountReceiveOrderChannel.getChannel().getChannelName()));
			}
			if (param.getRebate() > superiorChannel.getRebate()) {
				throw new BizException("下级账号的点数不能大于上级账号");
			}
			accountReceiveOrderChannel.setRebate(param.getRebate());
			accountReceiveOrderChannelRepo.save(accountReceiveOrderChannel);
		}
	}

	@Transactional(readOnly = true)
	public String getInviteCode(String userAccountId) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		return userAccount.getInviteCode();
	}

	@Lock(keys = "'updateInviteCode_' + #userAccountId")
	@Transactional
	public void updateInviteCode(String userAccountId) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		String inviteCode = userAccount.getInviteCode();
		if (StrUtil.isNotBlank(inviteCode)) {
			return;
		}
		inviteCode = userAccountId.substring(userAccountId.length() - 6, userAccountId.length());
		while (userAccountRepo.findTopByInviteCodeAndDeletedFlagIsFalse(inviteCode) != null) {
			inviteCode = userAccountId.substring(userAccountId.length() - 6, userAccountId.length())
					+ RandomUtil.randomNumbers(2);
		}
		userAccount.setInviteCode(inviteCode);
		userAccountRepo.save(userAccount);
	}

	@Lock(keys = "'sendSmsCode_' + #mobile")
	@Transactional
	public void sendSmsCode(@NotBlank String clientIP, @NotBlank String mobile) {
		String smsCodeGet = redisTemplate.opsForValue().get("SMS_CODE_GET" + mobile);
		if (StrUtil.isNotBlank(smsCodeGet)) {
			throw new BizException("请不要频繁获取验证码");
		}
		redisTemplate.opsForValue().set("SMS_CODE_GET" + mobile, "1", 60, TimeUnit.SECONDS);
		String ipEverydayGetNumKey = "IP_EVERYDAY_GET_SMS_CODE_NUM_"
				+ DateUtil.format(new Date(), DatePattern.NORM_DATE_FORMAT) + clientIP;
		String ipEverydayGetNumStr = redisTemplate.opsForValue().get(ipEverydayGetNumKey);
		int ipEverydayGetNum = 0;
		if (StrUtil.isNotBlank(ipEverydayGetNumStr)) {
			ipEverydayGetNum = Integer.parseInt(ipEverydayGetNumStr);
			if (ipEverydayGetNum >= 10) {
				throw new BizException("获取验证码过于频繁,请第二天再操作");
			}
		}
		ipEverydayGetNum++;
		redisTemplate.opsForValue().set(ipEverydayGetNumKey, String.valueOf(ipEverydayGetNum), 30, TimeUnit.HOURS);

		String smsCode = RandomUtil.randomNumbers(4);
		try {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("user", "jiamin");
			paramMap.put("pass", "25d55ad283aa400af464c76d713c07ad");
			paramMap.put("phone", mobile);
			paramMap.put("code", smsCode);
			String returnResult = HttpUtil.get("https://1mao.vip/api/send_sms", paramMap, 9000);
			JSONObject jsonObject = JSONUtil.parseObj(returnResult);
			String returnCode = jsonObject.getStr("code");
			if (!"0".equals(returnCode)) {
				log.error("短信接口异常:" + jsonObject.getStr("msg"));
				throw new BizException(jsonObject.getStr("msg"));
			}
			redisTemplate.opsForValue().set("SMS_CODE_" + mobile, smsCode, 60 * 5, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new BizException(e.getMessage());
		}
	}

	@ParamValid
	@Transactional(readOnly = true)
	public PageResult<TeamNumberOfPeopleVO> findLevel1MemberByPage(Level1MemberQueryCondParam param) {
		UserAccount userAccount = userAccountRepo.getOne(param.getCurrentAccountId());
		UserAccount lowerLevelAccount = userAccountRepo.getOne(param.getLowerLevelId());
		if (!lowerLevelAccount.getAccountLevelPath().startsWith(userAccount.getAccountLevelPath())) {
			throw new BizException(BizError.无权操作);
		}
		Specification<TeamNumberOfPeople> spec = new Specification<TeamNumberOfPeople>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<TeamNumberOfPeople> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				Predicate predicate2 = builder.equal(root.get("inviterId"), param.getLowerLevelId());
				predicates.add(predicate2);
				if (StrUtil.isNotEmpty(param.getUserName())) {
					predicates.add(builder.like(root.get("userName"), "%" + param.getUserName() + "%"));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<TeamNumberOfPeople> result = teamNumberOfPeopleRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.asc("registeredTime"))));
		PageResult<TeamNumberOfPeopleVO> pageResult = new PageResult<>(
				TeamNumberOfPeopleVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public TeamNumberOfPeopleVO getTeamNumberOfPeople(@NotBlank String userAccountId, @NotBlank String lowerLevelId) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		UserAccount lowerLevelAccount = userAccountRepo.getOne(lowerLevelId);
		if (!lowerLevelAccount.getAccountLevelPath().startsWith(userAccount.getAccountLevelPath())) {
			throw new BizException(BizError.无权操作);
		}
		TeamNumberOfPeople teamNumberOfPeople = teamNumberOfPeopleRepo.getOne(lowerLevelId);
		return TeamNumberOfPeopleVO.convertFor(teamNumberOfPeople);
	}

	public void bindGoogleAuth(String id, String googleSecretKey, String googleVerCode) {
		if (!GoogleAuthenticator.checkCode(googleSecretKey, googleVerCode, System.currentTimeMillis())) {
			throw new BizException(BizError.谷歌验证码不正确);
		}
		UserAccount userAccount = userAccountRepo.getOne(id);
		userAccount.bindGoogleAuth(googleSecretKey);
		userAccountRepo.save(userAccount);
	}

	@Transactional
	public void unBindGoogleAuth(String id) {
		UserAccount account = userAccountRepo.getOne(id);
		account.unBindGoogleAuth();
		userAccountRepo.save(account);
	}

	public void unBindGoogleAuth(String id, String googleVerCode) {
		UserAccount userAccount = userAccountRepo.getOne(id);
		if (!GoogleAuthenticator.checkCode(userAccount.getGoogleSecretKey(), googleVerCode,
				System.currentTimeMillis())) {
			throw new BizException(BizError.谷歌验证码不正确);
		}
		unBindGoogleAuth(id);
	}

	@Transactional(readOnly = true)
	public GoogleAuthInfoVO getGoogleAuthInfo(@NotBlank String userAccountId) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		return GoogleAuthInfoVO.convertFor(userAccount.getUserName(), userAccount.getGoogleSecretKey(),
				userAccount.getGoogleAuthBindTime());
	}

	@Lock(keys = "'receiveOrderState_' + #userAccountId")
	@Transactional
	public void updateReceiveOrderStateWithMember(@NotBlank String userAccountId, @NotBlank String receiveOrderState) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		if (Constant.接单状态_禁止接单.equals(userAccount.getReceiveOrderState())) {
			throw new BizException(BizError.账号已被禁止接单);
		}
		updateReceiveOrderStateInner(userAccountId, receiveOrderState);
	}

	@Lock(keys = "'receiveOrderState_' + #userAccountId")
	@Transactional
	public void updateReceiveOrderStateInner(@NotBlank String userAccountId, @NotBlank String receiveOrderState) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		if (Constant.接单状态_正在接单.equals(receiveOrderState)) {
			if (Constant.账号状态_禁用.equals(userAccount.getState())) {
				throw new BizException(BizError.账号已被禁用);
			}
			if (StrUtil.isBlank(userAccount.getMobile())) {
				throw new BizException("请先绑定手机号");
			}
			Double cashDeposit = userAccount.getCashDeposit();
			if (cashDeposit != null && cashDeposit <= 0) {
				throw new BizException("余额不足");
			}
			List<AccountReceiveOrderChannel> channels = accountReceiveOrderChannelRepo
					.findByUserAccountId(userAccountId);
			if (CollectionUtil.isEmpty(channels)) {
				throw new BizException(BizError.未配置接单通道无法接单);
			}
			List<String> gatheringChannelCodes = new ArrayList<String>();
			for (AccountReceiveOrderChannel channel : channels) {
				gatheringChannelCodes.add(channel.getChannel().getChannelCode());
			}
			List<GatheringCode> gatheringCodes = gatheringCodeRepo
					.findByUserAccountIdAndGatheringChannelChannelCodeInAndStateAndInUseTrueAndDeletedFlagFalse(
							userAccountId, gatheringChannelCodes, Constant.收款码状态_正常);
			if (CollectionUtil.isEmpty(gatheringCodes)) {
				throw new BizException(BizError.该账号未上传或未启用收款方式无法接单);
			}
		}
		userAccount.setReceiveOrderState(receiveOrderState);
		userAccountRepo.save(userAccount);

		if (Constant.接单状态_正在接单.equals(receiveOrderState)) {
			QueueRecord queueRecord = queueRecordRepo.findTopByUserAccountIdAndUsedIsFalse(userAccountId);
			if (queueRecord != null) {
				return;
			}
			queueRecord = QueueRecord.build(userAccount);
			queueRecordRepo.save(queueRecord);
		} else {
			QueueRecord queueRecord = queueRecordRepo.findTopByUserAccountIdAndUsedIsFalse(userAccountId);
			if (queueRecord == null) {
				return;
			}
			queueRecord.used("停止接单退出队列");
			queueRecord.setMarkRead(true);
			queueRecordRepo.save(queueRecord);
		}
	}

	/**
	 * 更新最近登录时间
	 */
	@Transactional
	public void updateLatelyLoginTime(String userAccountId) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		userAccount.setLatelyLoginTime(new Date());
		userAccountRepo.save(userAccount);
	}

	@Transactional
	public Boolean verifyMobile(String mobile) {
		UserAccount existUserAccount = userAccountRepo.findByMobileAndDeletedFlagIsFalse(mobile);
		return existUserAccount != null;
	}

	@Transactional
	public Boolean verifySmsCode(String mobile, String smsCode) {
		UserAccount existUserAccount = userAccountRepo.findByMobileAndDeletedFlagIsFalse(mobile);
		if (existUserAccount == null) {
			throw new BizException("短信验证码不正确");
		}
		String smsCodeTmp = redisTemplate.opsForValue().get("SMS_CODE_" + mobile);
		if (!smsCode.equals(smsCodeTmp)) {
			throw new BizException("短信验证码不正确");
		}
		return true;
	}

	@ParamValid
	@Transactional
	public void updateUserAccount(UserAccountEditParam param) {
		UserAccount existUserAccount = userAccountRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
		if (existUserAccount != null && !existUserAccount.getId().equals(param.getId())) {
			throw new BizException(BizError.账号已存在);
		}
		UserAccount userAccount = userAccountRepo.getOne(param.getId());
		BeanUtils.copyProperties(param, userAccount);
		userAccountRepo.save(userAccount);
	}

	@Transactional(readOnly = true)
	public UserAccountDetailsInfoVO findUserAccountDetailsInfoById(String userAccountId) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		return UserAccountDetailsInfoVO.convertFor(userAccount);
	}

	@Transactional(readOnly = true)
	public PageResult<UserAccountDetailsInfoVO> findUserAccountDetailsInfoByPage(UserAccountQueryCondParam param) {
		Specification<UserAccount> spec = new Specification<UserAccount>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<UserAccount> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("deletedFlag"), false));
				if (CollectionUtil.isNotEmpty(param.getAccountTypes())) {
					predicates.add(root.get("accountType").in(param.getAccountTypes()));
				}
				if (StrUtil.isNotEmpty(param.getUserName())) {
					predicates.add(builder.equal(root.get("userName"), param.getUserName()));
				}
				if (StrUtil.isNotEmpty(param.getRealName())) {
					predicates.add(builder.equal(root.get("realName"), param.getRealName()));
				}
				if (StrUtil.isNotEmpty(param.getMobile())) {
					predicates.add(builder.equal(root.get("mobile"), param.getMobile()));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<UserAccount> result = userAccountRepo.findAll(spec, PageRequest.of(param.getPageNum() - 1,
				param.getPageSize(), Sort.by(Direction.fromString(param.getDirection()), param.getPropertie())));
		PageResult<UserAccountDetailsInfoVO> pageResult = new PageResult<>(
				UserAccountDetailsInfoVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@ParamValid
	@Transactional
	public void modifyLoginPwd(ModifyLoginPwdParam param) {
		UserAccount userAccount = userAccountRepo.getOne(param.getUserAccountId());
		BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
		if (!pwdEncoder.matches(param.getOldLoginPwd(), userAccount.getLoginPwd())) {
			throw new BizException(BizError.旧的登录密码不正确);
		}
		modifyLoginPwd(param.getUserAccountId(), param.getNewLoginPwd());
	}

	@Lock(keys = { "'mobile_' + #param.mobile" })
	@Transactional
	public void bindMobile(BindMobileParam param) {
		UserAccount currentAccount = userAccountRepo.getOne(param.getUserAccountId());
		if (StrUtil.isNotBlank(currentAccount.getMobile())) {
			throw new BizException("已绑定手机号,如需更换请联系客服");
		}
		UserAccount mobileAccount = userAccountRepo.findByMobileAndDeletedFlagIsFalse(param.getMobile());
		if (mobileAccount != null) {
			throw new BizException("该手机号已被使用");
		}
		String smsCode = redisTemplate.opsForValue().get("SMS_CODE_" + param.getMobile());
		if (!param.getSmsCode().equals(smsCode)) {
			throw new BizException("短信验证码不正确");
		}
		currentAccount.setMobile(param.getMobile());
		userAccountRepo.save(currentAccount);
	}

	@Transactional
	public void forgetPwdAndModifyPwd(ForgetPwdAndModifyPwdParam param) {
		String smsCode = redisTemplate.opsForValue().get("SMS_CODE_" + param.getMobile());
		if (!param.getSmsCode().equals(smsCode)) {
			throw new BizException("短信验证码不正确");
		}
		UserAccount userAccount = userAccountRepo.findByUserNameAndDeletedFlagIsFalse(param.getMobile());
		if (userAccount == null) {
			throw new BizException("账号不存在");
		}
		modifyLoginPwd(userAccount.getId(), param.getNewLoginPwd());
	}

	@Transactional
	public void modifyLoginPwd(@NotBlank String userAccountId, @NotBlank String newLoginPwd) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		userAccount.setLoginPwd(new BCryptPasswordEncoder().encode(newLoginPwd));
		userAccountRepo.save(userAccount);
	}

	@ParamValid
	@Transactional
	public void modifyMoneyPwd(ModifyMoneyPwdParam param) {
		UserAccount userAccount = userAccountRepo.getOne(param.getUserAccountId());
		BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
		if (!pwdEncoder.matches(param.getOldMoneyPwd(), userAccount.getMoneyPwd())) {
			throw new BizException(BizError.旧的资金密码不正确);
		}
		String newMoneyPwd = pwdEncoder.encode(param.getNewMoneyPwd());
		userAccount.setMoneyPwd(newMoneyPwd);
		userAccountRepo.save(userAccount);
	}

	@ParamValid
	@Transactional
	public void modifyMoneyPwd(@NotBlank String userAccountId, @NotBlank String newMoneyPwd) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		userAccount.setMoneyPwd(new BCryptPasswordEncoder().encode(newMoneyPwd));
		userAccountRepo.save(userAccount);
	}

	@Transactional(readOnly = true)
	public LoginAccountInfoVO getLoginAccountInfo(String userName) {
		return LoginAccountInfoVO.convertFor(userAccountRepo.findByUserNameAndDeletedFlagIsFalse(userName));
	}
	
	@Transactional(readOnly = true)
	public LoginAccountInfoVO getLoginAccountInfoByMobile(String userName) {
		return LoginAccountInfoVO.convertFor(userAccountRepo.findByMobileAndDeletedFlagIsFalse(userName));
	}

	@Transactional(readOnly = true)
	public MemberAccountInfoVO getMemberAccountInfo(String userAccountId) {
		return MemberAccountInfoVO.convertFor(userAccountRepo.getOne(userAccountId));
	}

	@Transactional(readOnly = true)
	public UserAccountInfoVO getUserAccountInfo(String userAccountId) {
		return UserAccountInfoVO.convertFor(userAccountRepo.getOne(userAccountId));
	}

	@ParamValid
	@Transactional
	public void addUserAccount(AddUserAccountParam param) {
		UserAccount userAccount = userAccountRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
		if (userAccount != null) {
			throw new BizException(BizError.账号已存在);
		}
		String encodePwd = new BCryptPasswordEncoder().encode(param.getLoginPwd());
		param.setLoginPwd(encodePwd);
		UserAccount newUserAccount = param.convertToPo();
		if (StrUtil.isNotBlank(param.getInviterUserName())) {
			UserAccount inviter = userAccountRepo.findByUserNameAndDeletedFlagIsFalse(param.getInviterUserName());
			if (inviter == null) {
				throw new BizException(BizError.邀请人不存在);
			}
			newUserAccount.setInviterId(inviter.getId());
			newUserAccount.setAccountLevel(inviter.getAccountLevel() + 1);
			newUserAccount.setAccountLevelPath(inviter.getAccountLevelPath() + "." + newUserAccount.getId());
		}
		userAccountRepo.save(newUserAccount);
	}

	@Lock(keys = { "'userName_' + #param.userName", "'mobile_' + #param.mobile" })
	@ParamValid
	@Transactional
	public void register(UserAccountRegisterParam param) {
		RegisterSetting setting = registerSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (!setting.getRegisterFun()) {
			throw new BizException(BizError.未开放注册功能);
		}
		UserAccount existUserNameAccount = userAccountRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
		if (existUserNameAccount != null) {
			throw new BizException(BizError.账号已存在);
		}
		UserAccount existMobileAccount = userAccountRepo.findByMobileAndDeletedFlagIsFalse(param.getMobile());
		if (existMobileAccount != null) {
			throw new BizException("手机号已存在");
		}
//		String smsCodeTmp = redisTemplate.opsForValue().get("SMS_CODE_" + param.getMobile());
//		if (!param.getSmsCode().equals(smsCodeTmp)) {
//			throw new BizException("短信验证码不正确");
//		}
		param.setLoginPwd(new BCryptPasswordEncoder().encode(param.getLoginPwd()));
		param.setMoneyPwd(new BCryptPasswordEncoder().encode(param.getMoneyPwd()));
		UserAccount newUserAccount = param.convertToPo();
		if (setting.getInviteRegisterMode()) {
			UserAccount inviter = userAccountRepo.findTopByInviteCodeAndDeletedFlagIsFalse(param.getInviteCode());
			if (inviter == null) {
				throw new BizException(BizError.业务异常.getCode(), "邀请码不存在或已失效");
			}
			newUserAccount.updateInviteInfo(inviter);
		}
		userAccountRepo.save(newUserAccount);
		if (setting.getInviteRegisterMode()) {
			UserAccount inviter = userAccountRepo.findTopByInviteCodeAndDeletedFlagIsFalse(param.getInviteCode());
			List<AccountReceiveOrderChannel> inviterChannels = accountReceiveOrderChannelRepo
					.findByUserAccountIdAndChannelDeletedFlagFalse(inviter.getId());
			for (AccountReceiveOrderChannel inviterChannel : inviterChannels) {
				AccountReceiveOrderChannel accountReceiveOrderChannel = AccountReceiveOrderChannel.build(0d,
						inviterChannel.getChannelId(), newUserAccount.getId());
				accountReceiveOrderChannelRepo.save(accountReceiveOrderChannel);
			}
		} else {
			List<GatheringChannel> gatheringChannels = gatheringChannelRepo.findByEnabledAndDeletedFlagIsFalse(true);
			for (GatheringChannel gatheringChannel : gatheringChannels) {
				AccountReceiveOrderChannel accountReceiveOrderChannel = AccountReceiveOrderChannel
						.build(gatheringChannel, newUserAccount.getId());
				accountReceiveOrderChannelRepo.save(accountReceiveOrderChannel);
			}
		}
	}

	public Specification<AccountChangeLog> buildAccountChangeLogQueryCond(AccountChangeLogQueryCondParam param) {
		Specification<AccountChangeLog> spec = new Specification<AccountChangeLog>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<AccountChangeLog> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (param.getStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("accountChangeTime").as(Date.class),
							DateUtil.beginOfDay(param.getStartTime())));
				}
				if (param.getEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("accountChangeTime").as(Date.class),
							DateUtil.endOfDay(param.getEndTime())));
				}
				if (StrUtil.isNotEmpty(param.getAccountChangeType())) {
					predicates.add(builder.equal(root.get("accountChangeType"), param.getAccountChangeType()));
				}
				if (StrUtil.isNotEmpty(param.getUserAccountId())) {
					predicates.add(builder.equal(root.get("userAccountId"), param.getUserAccountId()));
				}
				if (StrUtil.isNotEmpty(param.getUserName())) {
					predicates.add(builder.equal(root.join("userAccount", JoinType.INNER).get("userName"),
							param.getUserName()));
				}

				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

	@Transactional(readOnly = true)
	public List<AccountChangeLogVO> findAccountChangeLog(AccountChangeLogQueryCondParam param) {
		Specification<AccountChangeLog> spec = buildAccountChangeLogQueryCond(param);
		List<AccountChangeLog> result = accountChangeLogRepo.findAll(spec,
				Sort.by(Sort.Order.desc("accountChangeTime"), Sort.Order.desc("id")));
		return AccountChangeLogVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public PageResult<AccountChangeLogVO> findAccountChangeLogByPage(AccountChangeLogQueryCondParam param) {
		Specification<AccountChangeLog> spec = buildAccountChangeLogQueryCond(param);
		Page<AccountChangeLog> result = accountChangeLogRepo.findAll(spec, PageRequest.of(param.getPageNum() - 1,
				param.getPageSize(), Sort.by(Sort.Order.desc("accountChangeTime"), Sort.Order.desc("id"))));
		PageResult<AccountChangeLogVO> pageResult = new PageResult<>(AccountChangeLogVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void delUserAccount(@NotBlank String userAccountId) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		userAccount.deleted();
		userAccountRepo.save(userAccount);
		redisTemplate.opsForList().leftPush(Constant.已删除账号ID, userAccountId);
	}

	@ParamValid
	@Transactional
	public void addCashDeposit(@NotBlank String userAccountId,
			@NotNull @DecimalMin(value = "0", inclusive = true) Double cashDeposit, String note) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		double cashDepositAfter = userAccount.getCashDeposit() + cashDeposit;
		userAccount.setCashDeposit(NumberUtil.round(cashDepositAfter, 2).doubleValue());
		userAccountRepo.save(userAccount);
		accountChangeLogRepo
				.save(AccountChangeLog.buildWithBackgroundAdjustCashDeposit(userAccount, cashDeposit, note));
	}

	@ParamValid
	@Transactional
	public void reduceCashDeposit(@NotBlank String userAccountId,
			@NotNull @DecimalMin(value = "0", inclusive = true) Double cashDeposit, String note) {
		UserAccount userAccount = userAccountRepo.getOne(userAccountId);
		double cashDepositAfter = userAccount.getCashDeposit() - cashDeposit;
		if (cashDepositAfter < 0) {
			throw new BizException(BizError.业务异常.getCode(), "余额不能少于0");
		}
		userAccount.setCashDeposit(NumberUtil.round(cashDepositAfter, 2).doubleValue());
		userAccountRepo.save(userAccount);
		accountChangeLogRepo
				.save(AccountChangeLog.buildWithBackgroundAdjustCashDeposit(userAccount, -cashDeposit, note));
	}

}
