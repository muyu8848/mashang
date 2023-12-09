package com.mashang.merchant.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
import com.mashang.merchant.domain.Merchant;
import com.mashang.merchant.domain.MerchantAccountChangeLog;
import com.mashang.merchant.domain.MerchantFreezeFundRecord;
import com.mashang.merchant.param.AddMerchantParam;
import com.mashang.merchant.param.FreezeFundParam;
import com.mashang.merchant.param.MerchantAccountChangeLogQueryCondParam;
import com.mashang.merchant.param.MerchantEditParam;
import com.mashang.merchant.param.MerchantFreezeFundRecordQueryCondParam;
import com.mashang.merchant.param.MerchantQueryCondParam;
import com.mashang.merchant.param.ModifyLoginPwdParam;
import com.mashang.merchant.param.ModifyMoneyPwdParam;
import com.mashang.merchant.repo.MerchantAccountChangeLogRepo;
import com.mashang.merchant.repo.MerchantFreezeFundRecordRepo;
import com.mashang.merchant.repo.MerchantRepo;
import com.mashang.merchant.vo.LoginMerchantInfoVO;
import com.mashang.merchant.vo.MerchantAccountChangeLogVO;
import com.mashang.merchant.vo.MerchantFreezeFundRecordVO;
import com.mashang.merchant.vo.MerchantVO;
import com.zengtengpeng.annotation.Lock;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class MerchantService {

	@Autowired
	private MerchantRepo merchantRepo;

	@Autowired
	private MerchantAccountChangeLogRepo merchantAccountChangeLogRepo;

	@Autowired
	private MerchantFreezeFundRecordRepo merchantFreezeFundRecordRepo;

	@Transactional
	public void unBindGoogleAuth(String id) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.unBindGoogleAuth();
		merchantRepo.save(merchant);
	}

	@Transactional(readOnly = true)
	public Double getMerchantFreezeFund(@NotBlank String merchantId) {
		Double merchantFreezeFund = 0d;
		List<MerchantFreezeFundRecord> records = merchantFreezeFundRecordRepo
				.findByMerchantIdAndReleaseFlagFalse(merchantId);
		for (MerchantFreezeFundRecord record : records) {
			merchantFreezeFund += record.getFreezeFund();
		}
		merchantFreezeFund = NumberUtil.round(merchantFreezeFund, 2).doubleValue();
		return merchantFreezeFund;
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantFreezeFundRecordVO> findMerchantFreezeFundRecordByPage(
			MerchantFreezeFundRecordQueryCondParam param) {
		Specification<MerchantFreezeFundRecord> spec = new Specification<MerchantFreezeFundRecord>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantFreezeFundRecord> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (StrUtil.isNotBlank(param.getMerchantNum())) {
					predicates.add(builder.equal(root.join("merchant", JoinType.INNER).get("userName"),
							param.getMerchantNum()));
				}
				if (StrUtil.isNotEmpty(param.getMerchantId())) {
					predicates.add(builder.equal(root.get("merchantId"), param.getMerchantId()));
				}
				if (StrUtil.isNotEmpty(param.getOrderNo())) {
					predicates.add(builder.equal(root.get("orderNo"), param.getOrderNo()));
				}
				if (param.getReleaseFlag() != null) {
					predicates.add(builder.equal(root.get("releaseFlag"), param.getReleaseFlag()));
				}
				if (param.getCreateTimeStart() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.beginOfDay(param.getCreateTimeStart())));
				}
				if (param.getCreateTimeEnd() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("createTime").as(Date.class),
							DateUtil.endOfDay(param.getCreateTimeEnd())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<MerchantFreezeFundRecord> result = merchantFreezeFundRecordRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<MerchantFreezeFundRecordVO> pageResult = new PageResult<>(
				MerchantFreezeFundRecordVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	@Lock(keys = "'releaseFreezeFund_' + #id")
	@ParamValid
	@Transactional
	public void releaseFreezeFund(String id) {
		MerchantFreezeFundRecord merchantFreezeFundRecord = merchantFreezeFundRecordRepo.getOne(id);
		if (merchantFreezeFundRecord.getReleaseFlag()) {
			throw new BizException(BizError.业务异常.getCode(), "记录已解冻");
		}
		merchantFreezeFundRecord.setReleaseFlag(true);
		merchantFreezeFundRecordRepo.save(merchantFreezeFundRecord);
		Merchant merchant = merchantFreezeFundRecord.getMerchant();
		double withdrawableAmount = NumberUtil
				.round(merchant.getWithdrawableAmount() + merchantFreezeFundRecord.getFreezeFund(), 2).doubleValue();
		merchant.setWithdrawableAmount(withdrawableAmount);
		merchant.setFreezeFund(
				NumberUtil.round(merchant.getFreezeFund() - merchantFreezeFundRecord.getFreezeFund(), 2).doubleValue());
		merchantRepo.save(merchant);
		merchantAccountChangeLogRepo
				.save(MerchantAccountChangeLog.buildWithReleaseFreezeFund(merchant, merchantFreezeFundRecord));
	}

	@ParamValid
	@Transactional
	public void freezeFund(FreezeFundParam param) {
		Merchant merchant = merchantRepo.getOne(param.getMerchantId());
		double withdrawableAmount = NumberUtil.round(merchant.getWithdrawableAmount() - param.getFreezeFund(), 2)
				.doubleValue();
		if (withdrawableAmount < 0) {
			throw new BizException(BizError.可提现金额不足);
		}
		merchant.setWithdrawableAmount(withdrawableAmount);
		merchant.setFreezeFund(NumberUtil.round(merchant.getFreezeFund() + param.getFreezeFund(), 2).doubleValue());
		merchantRepo.save(merchant);
		MerchantFreezeFundRecord merchantFreezeFundRecord = param.convertToPo();
		merchantFreezeFundRecordRepo.save(merchantFreezeFundRecord);
		merchantAccountChangeLogRepo
				.save(MerchantAccountChangeLog.buildWithFreezeFund(merchant, merchantFreezeFundRecord));
	}

	@Transactional(readOnly = true)
	public List<MerchantAccountChangeLogVO> findAccountChangeLog(MerchantAccountChangeLogQueryCondParam param) {
		Specification<MerchantAccountChangeLog> spec = buildAccountChangeLogQueryCond(param);
		List<MerchantAccountChangeLog> result = merchantAccountChangeLogRepo.findAll(spec,
				Sort.by(Sort.Order.desc("accountChangeTime"), Sort.Order.desc("id")));
		return MerchantAccountChangeLogVO.convertFor(result);
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantAccountChangeLogVO> findAccountChangeLogByPage(
			MerchantAccountChangeLogQueryCondParam param) {
		Specification<MerchantAccountChangeLog> spec = buildAccountChangeLogQueryCond(param);
		Page<MerchantAccountChangeLog> result = merchantAccountChangeLogRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(),
						Sort.by(Sort.Order.desc("accountChangeTime"), Sort.Order.desc("id"))));
		PageResult<MerchantAccountChangeLogVO> pageResult = new PageResult<>(
				MerchantAccountChangeLogVO.convertFor(result.getContent()), param.getPageNum(), param.getPageSize(),
				result.getTotalElements());
		return pageResult;
	}

	public Specification<MerchantAccountChangeLog> buildAccountChangeLogQueryCond(
			MerchantAccountChangeLogQueryCondParam param) {
		Specification<MerchantAccountChangeLog> spec = new Specification<MerchantAccountChangeLog>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantAccountChangeLog> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (param.getStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("accountChangeTime").as(Date.class),
							DateUtil.beginOfDay(param.getStartTime())));
				}
				if (param.getEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("accountChangeTime").as(Date.class),
							DateUtil.endOfDay(param.getEndTime())));
				}
				if (StrUtil.isNotEmpty(param.getAccountChangeTypeCode())) {
					predicates.add(builder.equal(root.get("accountChangeTypeCode"), param.getAccountChangeTypeCode()));
				}
				if (StrUtil.isNotEmpty(param.getMerchantId())) {
					predicates.add(builder.equal(root.get("merchantId"), param.getMerchantId()));
				}
				if (StrUtil.isNotEmpty(param.getUserName())) {
					predicates.add(
							builder.equal(root.join("merchant", JoinType.INNER).get("userName"), param.getUserName()));
				}

				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

	@Transactional
	public void bindGoogleAuth(String id, String googleSecretKey, String googleVerCode) {
		if (!GoogleAuthenticator.checkCode(googleSecretKey, googleVerCode, System.currentTimeMillis())) {
			throw new BizException(BizError.谷歌验证码不正确);
		}
		Merchant merchant = merchantRepo.getOne(id);
		merchant.bindGoogleAuth(googleSecretKey);
		merchantRepo.save(merchant);
	}

	public GoogleAuthInfoVO getGoogleAuthInfo(String id) {
		Merchant merchant = merchantRepo.getOne(id);
		return GoogleAuthInfoVO.convertFor(merchant.getUserName(), merchant.getGoogleSecretKey(),
				merchant.getGoogleAuthBindTime());
	}

	@ParamValid
	@Transactional
	public void addBalance(@NotBlank String merchantId,
			@NotNull @DecimalMin(value = "0", inclusive = true) Double amount, String note) {
		Merchant merchant = merchantRepo.getOne(merchantId);
		double amountAfter = merchant.getWithdrawableAmount() + amount;
		merchant.setWithdrawableAmount(NumberUtil.round(amountAfter, 2).doubleValue());
		merchantRepo.save(merchant);
		merchantAccountChangeLogRepo
				.save(MerchantAccountChangeLog.buildAdjustWithdrawableAmount(merchant, amount, note));
	}

	@ParamValid
	@Transactional
	public void reduceBalance(@NotBlank String merchantId,
			@NotNull @DecimalMin(value = "0", inclusive = true) Double amount, String note) {
		Merchant merchant = merchantRepo.getOne(merchantId);
		double amountAfter = NumberUtil.round(merchant.getWithdrawableAmount() - amount, 2).doubleValue();
		if (amountAfter < 0) {
			throw new BizException(BizError.业务异常.getCode(), "余额不能少于0");
		}
		merchant.setWithdrawableAmount(amountAfter);
		merchantRepo.save(merchant);
		merchantAccountChangeLogRepo
				.save(MerchantAccountChangeLog.buildAdjustWithdrawableAmount(merchant, -amount, note));
	}

	@ParamValid
	@Transactional
	public void modifyLoginPwd(ModifyLoginPwdParam param) {
		Merchant merchant = merchantRepo.getOne(param.getMerchantId());
		BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
		if (!pwdEncoder.matches(param.getOldLoginPwd(), merchant.getLoginPwd())) {
			throw new BizException(BizError.旧的登录密码不正确);
		}
		modifyLoginPwd(merchant.getId(), param.getNewLoginPwd());
	}

	@ParamValid
	@Transactional
	public void modifyMoneyPwd(ModifyMoneyPwdParam param) {
		Merchant merchant = merchantRepo.getOne(param.getMerchantId());
		BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
		if (!pwdEncoder.matches(param.getOldMoneyPwd(), merchant.getMoneyPwd())) {
			throw new BizException(BizError.旧的资金密码不正确);
		}
		modifyMoneyPwd(merchant.getId(), param.getNewMoneyPwd());
	}

	@Transactional(readOnly = true)
	public List<MerchantVO> findAllMerchant() {
		return MerchantVO.convertFor(merchantRepo.findByDeletedFlagIsFalse());
	}

	@Transactional(readOnly = true)
	public List<MerchantVO> findAllMerchantAgent() {
		return MerchantVO.convertFor(merchantRepo.findByAccountTypeAndDeletedFlagIsFalse(Constant.商户账号类型_商户代理));
	}

	@Transactional(readOnly = true)
	public MerchantVO getMerchantInfo(String id) {
		return MerchantVO.convertFor(merchantRepo.getOne(id));
	}

	/**
	 * 更新最近登录时间
	 */
	@Transactional
	public void updateLatelyLoginTime(String id) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.setLatelyLoginTime(new Date());
		merchantRepo.save(merchant);
	}

	@Transactional(readOnly = true)
	public LoginMerchantInfoVO getLoginMerchantInfo(String userName) {
		return LoginMerchantInfoVO.convertFor(merchantRepo.findByUserNameAndDeletedFlagIsFalse(userName));
	}

	@Transactional
	public void modifyLoginPwd(@NotBlank String id, @NotBlank String newLoginPwd) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.setLoginPwd(new BCryptPasswordEncoder().encode(newLoginPwd));
		merchantRepo.save(merchant);
	}

	@Transactional
	public void modifyMoneyPwd(@NotBlank String id, @NotBlank String newMoneyPwd) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.setMoneyPwd(new BCryptPasswordEncoder().encode(newMoneyPwd));
		merchantRepo.save(merchant);
	}

	@Transactional(readOnly = true)
	public MerchantVO findLowerLevelMerchantById(@NotBlank String id, @NotBlank String inviterId) {
		MerchantVO vo = findMerchantById(id);
		if (!inviterId.equals(vo.getInviterId())) {
			throw new BizException(BizError.无权操作);
		}
		return vo;
	}

	@Transactional(readOnly = true)
	public MerchantVO findMerchantById(@NotBlank String id) {
		return MerchantVO.convertFor(merchantRepo.getOne(id));
	}

	@Transactional
	public void delMerchantById(@NotBlank String id) {
		Merchant merchant = merchantRepo.getOne(id);
		merchant.setDeletedFlag(true);
		merchantRepo.save(merchant);
	}

	@ParamValid
	@Transactional
	public void addLowerLevelMerchant(AddMerchantParam param, @NotBlank String inviterUserName) {
		param.setInviterUserName(inviterUserName);
		addMerchant(param);
	}

	@ParamValid
	@Transactional
	public void addMerchant(AddMerchantParam param) {
		Merchant merchantWithUserName = merchantRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
		if (merchantWithUserName != null) {
			throw new BizException(BizError.用户名已使用);
		}
		Merchant merchantWithName = merchantRepo.findByMerchantNameAndDeletedFlagIsFalse(param.getMerchantName());
		if (merchantWithName != null) {
			throw new BizException(BizError.商户名称已使用);
		}
		param.setLoginPwd(new BCryptPasswordEncoder().encode(param.getLoginPwd()));
		Merchant newMerchant = param.convertToPo();
		if (StrUtil.isNotBlank(param.getInviterUserName())) {
			Merchant inviter = merchantRepo.findByUserNameAndDeletedFlagIsFalse(param.getInviterUserName());
			if (inviter == null) {
				throw new BizException("上级商户不存在");
			}
			if (!Constant.商户账号类型_商户代理.equals(inviter.getAccountType())) {
				throw new BizException("只有代理商才能开下级商户");
			}
			newMerchant.setInviterId(inviter.getId());
			newMerchant.setAccountLevel(inviter.getAccountLevel() + 1);
			newMerchant.setAccountLevelPath(inviter.getAccountLevelPath() + "." + newMerchant.getId());

		}
		merchantRepo.save(newMerchant);
	}

	@ParamValid
	@Transactional
	public void updateMerchant(MerchantEditParam param) {
		Merchant merchantWithUserName = merchantRepo.findByUserNameAndDeletedFlagIsFalse(param.getUserName());
		if (merchantWithUserName != null && !merchantWithUserName.getId().equals(param.getId())) {
			throw new BizException(BizError.用户名已使用);
		}
		Merchant merchantWithName = merchantRepo.findByMerchantNameAndDeletedFlagIsFalse(param.getMerchantName());
		if (merchantWithName != null && !merchantWithName.getId().equals(param.getId())) {
			throw new BizException(BizError.商户名称已使用);
		}
		Merchant merchant = merchantRepo.getOne(param.getId());
		BeanUtils.copyProperties(param, merchant);
		merchantRepo.save(merchant);
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantVO> findMerchantByPageWithInviter(MerchantQueryCondParam param) {
		return findMerchantByPage(param);
	}

	@Transactional(readOnly = true)
	public PageResult<MerchantVO> findMerchantByPage(MerchantQueryCondParam param) {
		Specification<Merchant> spec = new Specification<Merchant>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<Merchant> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("deletedFlag"), false));
				if (StrUtil.isNotBlank(param.getUserName())) {
					predicates.add(builder.equal(root.get("userName"), param.getUserName()));
				}
				if (StrUtil.isNotBlank(param.getInviterId())) {
					predicates.add(builder.equal(root.get("inviterId"), param.getInviterId()));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<Merchant> result = merchantRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<MerchantVO> pageResult = new PageResult<>(MerchantVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

}
