package com.mashang.gatheringcode.service;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.common.HybridBinarizer;
import com.mashang.common.exception.BizError;
import com.mashang.common.exception.BizException;
import com.mashang.common.valid.ParamValid;
import com.mashang.common.vo.PageResult;
import com.mashang.constants.Constant;
import com.mashang.gatheringcode.domain.GatheringCode;
import com.mashang.gatheringcode.param.GatheringCodeParam;
import com.mashang.gatheringcode.param.GatheringCodeQueryCondParam;
import com.mashang.gatheringcode.repo.GatheringCodeRepo;
import com.mashang.gatheringcode.vo.GatheringCodeVO;
import com.mashang.mastercontrol.domain.ReceiveOrderRiskSetting;
import com.mashang.mastercontrol.repo.ReceiveOrderRiskSettingRepo;
import com.mashang.merchant.domain.GatheringChannel;
import com.mashang.merchant.repo.GatheringChannelRepo;
import com.mashang.storage.service.StorageService;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.BufferedImageLuminanceSource;

@Validated
@Service
public class GatheringCodeService {

	@Autowired
	private StorageService storageService;

	@Autowired
	private GatheringCodeRepo gatheringCodeRepo;

	@Autowired
	private GatheringChannelRepo gatheringChannelRepo;

	@Autowired
	private ReceiveOrderRiskSettingRepo receiveOrderRiskSettingRepo;

	@Transactional
	public void switchGatheringCode(List<String> gatheringCodeIds, String userAccountId) {
		List<GatheringCode> gatheringCodes = gatheringCodeRepo.findByUserAccountIdAndDeletedFlagFalse(userAccountId);
		for (GatheringCode gatheringCode : gatheringCodes) {
			boolean inUse = false;
			for (String gatheringCodeId : gatheringCodeIds) {
				if (gatheringCode.getId().equals(gatheringCodeId)) {
					inUse = true;
					break;
				}
			}
			gatheringCode.setInUse(inUse);
			gatheringCodeRepo.save(gatheringCode);
		}
	}

	@Transactional
	public void delMyGatheringCodeById(String id, String userAccountId) {
		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);
		if (!userAccountId.equals(gatheringCode.getUserAccountId())) {
			throw new BizException(BizError.无权删除数据);
		}
		delGatheringCodeById(id);
	}

	@Transactional
	public void delGatheringCodeById(String id) {
		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);
		gatheringCode.deleted();
		gatheringCodeRepo.save(gatheringCode);
	}

	@Transactional(readOnly = true)
	public GatheringCodeVO findGatheringCodeUsageById(String id) {
		return GatheringCodeVO.convertFor(gatheringCodeRepo.getOne(id));
	}

	@Transactional(readOnly = true)
	public List<GatheringCodeVO> findAllGatheringCode(String userAccountId) {
		return GatheringCodeVO.convertFor(gatheringCodeRepo.findByUserAccountIdAndDeletedFlagFalse(userAccountId));
	}

	@Transactional(readOnly = true)
	public List<GatheringCodeVO> findAllNormalGatheringCode(String userAccountId) {
		return GatheringCodeVO.convertFor(
				gatheringCodeRepo.findByUserAccountIdAndStateAndDeletedFlagFalse(userAccountId, Constant.收款码状态_正常));
	}

	@Transactional(readOnly = true)
	public PageResult<GatheringCodeVO> findGatheringCodeByPage(GatheringCodeQueryCondParam param) {
		Specification<GatheringCode> spec = new Specification<GatheringCode>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<GatheringCode> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("deletedFlag"), false));
				if (StrUtil.isNotEmpty(param.getState())) {
					predicates.add(builder.equal(root.get("state"), param.getState()));
				}
				if (StrUtil.isNotEmpty(param.getGatheringChannelId())) {
					predicates.add(builder.equal(root.get("gatheringChannelId"), param.getGatheringChannelId()));
				}
				if (StrUtil.isNotEmpty(param.getPayee())) {
					predicates.add(builder.equal(root.get("payee"), param.getPayee()));
				}
				if (StrUtil.isNotEmpty(param.getUserName())) {
					predicates.add(builder.equal(root.join("userAccount", JoinType.INNER).get("userName"),
							param.getUserName()));
				}
				if (StrUtil.isNotEmpty(param.getUserAccountId())) {
					predicates.add(builder.equal(root.get("userAccountId"), param.getUserAccountId()));
				}
				if (StrUtil.isNotEmpty(param.getDetailInfo())) {
					Predicate or = builder.or(builder.like(root.get("payee"), "%" + param.getDetailInfo() + "%"),
							builder.like(root.get("realName"), "%" + param.getDetailInfo() + "%"),
							builder.like(root.get("account"), "%" + param.getDetailInfo() + "%"));
					Predicate and = builder.and(or);
					predicates.add(and);
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<GatheringCode> result = gatheringCodeRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<GatheringCodeVO> pageResult = new PageResult<>(GatheringCodeVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public List<GatheringCodeVO> findAllGatheringCode() {
		return GatheringCodeVO.convertFor(gatheringCodeRepo.findAll());
	}

	@Transactional(readOnly = true)
	public PageResult<GatheringCodeVO> findTop5TodoAuditGatheringCodeByPage() {
		Specification<GatheringCode> spec = new Specification<GatheringCode>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<GatheringCode> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("state"), Constant.收款码状态_待审核));
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<GatheringCode> result = gatheringCodeRepo.findAll(spec,
				PageRequest.of(0, 5, Sort.by(Sort.Order.desc("initiateAuditTime"))));
		PageResult<GatheringCodeVO> pageResult = new PageResult<>(GatheringCodeVO.convertFor(result.getContent()), 1, 5,
				result.getTotalElements());
		return pageResult;
	}

	@Transactional
	public void updateToNormalState(String id) {
		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);
		gatheringCode.setAuditType(null);
		gatheringCode.setInitiateAuditTime(null);
		gatheringCode.setState(Constant.收款码状态_正常);
		gatheringCodeRepo.save(gatheringCode);
	}

	@Transactional
	public void updateInUseFlag(String id, Boolean inUse) {
		GatheringCode gatheringCode = gatheringCodeRepo.getOne(id);
		gatheringCode.setInUse(inUse);
		gatheringCodeRepo.save(gatheringCode);
	}

	@ParamValid
	@Transactional
	public void addGatheringCodeInner(GatheringCodeParam param, String userAccountId) {
		GatheringChannel gatheringChannel = gatheringChannelRepo.getOne(param.getGatheringChannelId());
		if (gatheringChannel.getAddGatheringCodeSetLimit()) {
			if (param.getMinAmount() <= 0) {
				throw new BizException(BizError.参数异常.getCode(), "最低限额必须要大于0");
			}
			if (param.getMaxAmount() <= 0) {
				throw new BizException(BizError.参数异常.getCode(), "最高限额必须要大于0");
			}
			if (param.getMinAmount() > param.getMaxAmount()) {
				throw new BizException(BizError.参数异常.getCode(), "最低限额不能大于最高限额");
			}
		}
		if (Constant.收款通道_银行卡.equals(gatheringChannel.getChannelCode())
				|| Constant.收款通道_支付宝转卡.equals(gatheringChannel.getChannelCode())
				|| Constant.收款通道_微信转卡.equals(gatheringChannel.getChannelCode())
				|| Constant.收款通道_财付通转卡.equals(gatheringChannel.getChannelCode())) {
			if (StrUtil.isBlank(param.getOpenAccountBank())) {
				throw new BizException(BizError.参数异常.getCode(), "银行不能为空");
			}
			if (StrUtil.isBlank(param.getAccountHolder())) {
				throw new BizException(BizError.参数异常.getCode(), "开户人不能为空");
			}
			if (StrUtil.isBlank(param.getBankCardAccount())) {
				throw new BizException(BizError.参数异常.getCode(), "卡号不能为空");
			}
		} else if (Constant.收款通道_微信.equals(gatheringChannel.getChannelCode())
				|| Constant.收款通道_微信红包.equals(gatheringChannel.getChannelCode())
				|| Constant.收款通道_支付宝.equals(gatheringChannel.getChannelCode())
				|| Constant.收款通道_闲鱼代付.equals(gatheringChannel.getChannelCode())) {
			if (StrUtil.isBlank(param.getPayee())) {
				throw new BizException(BizError.参数异常.getCode(), "收款人不能为空");
			}
			if (Constant.收款通道_支付宝.equals(gatheringChannel.getChannelCode())) {
				if (StrUtil.isBlank(param.getAccount())) {
					throw new BizException(BizError.参数异常.getCode(), "支付宝账号不能为空");
				}
				if (StrUtil.isBlank(param.getRealName())) {
					throw new BizException(BizError.参数异常.getCode(), "姓名不能为空");
				}
			}
			if (StrUtil.isBlank(param.getStorageId())) {
				throw new BizException(BizError.参数异常.getCode(), "收款码不能为空");
			}
			Resource resource = storageService.loadAsResource(param.getStorageId());
			try {
				BufferedImage bufferedImage = ImageIO.read(resource.getInputStream());
				BinaryBitmap image = new BinaryBitmap(
						new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
				String codeContent = new MultiFormatReader().decode(image).getText();
				param.setCodeContent(codeContent);
			} catch (Exception e) {
			}
		} else if (Constant.收款通道_微信手机转账.equals(gatheringChannel.getChannelCode())) {
			if (StrUtil.isBlank(param.getMobile())) {
				throw new BizException(BizError.参数异常.getCode(), "手机号不能为空");
			}
			if (StrUtil.isBlank(param.getRealName())) {
				throw new BizException(BizError.参数异常.getCode(), "姓名不能为空");
			}
		} else if (Constant.收款通道_支付宝转账.equals(gatheringChannel.getChannelCode())) {
			if (StrUtil.isBlank(param.getAccount())) {
				throw new BizException(BizError.参数异常.getCode(), "账号不能为空");
			}
			if (StrUtil.isBlank(param.getRealName())) {
				throw new BizException(BizError.参数异常.getCode(), "姓名不能为空");
			}
		} else if (Constant.收款通道_支付宝id转账.equals(gatheringChannel.getChannelCode())
				|| Constant.收款通道_小钱袋.equals(gatheringChannel.getChannelCode())) {
			if (StrUtil.isBlank(param.getAccount())) {
				throw new BizException(BizError.参数异常.getCode(), "账号不能为空");
			}
			if (StrUtil.isBlank(param.getAlipayId())) {
				throw new BizException(BizError.参数异常.getCode(), "支付宝id不能为空");
			}
		} else if (Constant.收款通道_TRC20.equals(gatheringChannel.getChannelCode())) {
			if (StrUtil.isBlank(param.getAddress())) {
				throw new BizException(BizError.参数异常.getCode(), "钱包地址为空");
			}
		}
		GatheringCode gatheringCode = param.convertToPo(userAccountId);
		ReceiveOrderRiskSetting receiveOrderRisk = receiveOrderRiskSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (receiveOrderRisk.getAuditGatheringCode()) {
			gatheringCode.initiateAudit(Constant.收款码审核类型_新增);
		}
		gatheringCodeRepo.save(gatheringCode);
	}

}
