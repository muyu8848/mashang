package com.mashang.merchant.service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.mashang.common.exception.BizError;
import com.mashang.common.exception.BizException;
import com.mashang.common.valid.ParamValid;
import com.mashang.common.vo.PageResult;
import com.mashang.merchant.domain.GatheringChannel;
import com.mashang.merchant.domain.GatheringChannelRate;
import com.mashang.merchant.domain.Merchant;
import com.mashang.merchant.param.AddOrUpdateGatheringChannelParam;
import com.mashang.merchant.param.GatheringChannelQueryCondParam;
import com.mashang.merchant.param.GatheringChannelRateParam;
import com.mashang.merchant.repo.GatheringChannelRateRepo;
import com.mashang.merchant.repo.GatheringChannelRepo;
import com.mashang.merchant.repo.MerchantRepo;
import com.mashang.merchant.vo.GatheringChannelRateVO;
import com.mashang.merchant.vo.GatheringChannelVO;

import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class GatheringChannelService {

	@Autowired
	private GatheringChannelRepo gatheringChannelRepo;

	@Autowired
	private GatheringChannelRateRepo gatheringChannelRateRepo;

	@Autowired
	private MerchantRepo merchantRepo;

	@Transactional(readOnly = true)
	public List<GatheringChannelRateVO> findLowerLevelGatheringChannelRate(@NotBlank String merchantId,
			@NotBlank String inviterId) {
		Merchant merchant = merchantRepo.getOne(merchantId);
		if (!inviterId.equals(merchant.getInviterId())) {
			throw new BizException(BizError.无权操作);
		}
		return findGatheringChannelRateByMerchantId(merchantId);
	}

	@Transactional(readOnly = true)
	public List<GatheringChannelRateVO> findGatheringChannelRateByMerchantId(@NotBlank String merchantId) {
		return GatheringChannelRateVO.convertFor(gatheringChannelRateRepo.findByMerchantId(merchantId));
	}

	@Transactional
	public void saveLowerLevelGatheringChannelRate(@NotBlank String merchantId, List<GatheringChannelRateParam> params,
			@NotBlank String inviterId) {
		Merchant merchant = merchantRepo.getOne(merchantId);
		if (!inviterId.equals(merchant.getInviterId())) {
			throw new BizException(BizError.无权操作);
		}
		saveGatheringChannelRate(merchantId, params);
	}

	@Transactional
	public void saveGatheringChannelRate(@NotBlank String merchantId, List<GatheringChannelRateParam> params) {
		Map<String, String> map = new HashMap<>();
		for (GatheringChannelRateParam param : params) {
			if (param.getMinAmount() > param.getMaxAmount()) {
				throw new BizException(BizError.通道限额范围无效);
			}
			if (map.get(param.getChannelId()) != null) {
				throw new BizException(BizError.不能设置重复的接单通道);
			}
			map.put(param.getChannelId(), param.getChannelId());
		}
		Merchant merchant = merchantRepo.getOne(merchantId);
		for (GatheringChannelRateParam param : params) {
			if (StrUtil.isNotBlank(merchant.getInviterId())) {
				GatheringChannelRate inviterRate = gatheringChannelRateRepo
						.findByMerchantIdAndChannelId(merchant.getInviterId(), param.getChannelId());
				if (inviterRate == null) {
					GatheringChannel channel = gatheringChannelRepo.getOne(param.getChannelId());
					throw new BizException(BizError.业务异常.getCode(),
							MessageFormat.format("请先为代理商配置通道:{0}", channel.getChannelName()));
				}
				if (inviterRate != null && (inviterRate.getRate() > param.getRate())) {
					throw new BizException(BizError.业务异常.getCode(),
							MessageFormat.format("通道[{0}]的费率不能低于代理商的费率,代理商的费率为:{1}",
									inviterRate.getChannel().getChannelName(), inviterRate.getRate()));
				}
				if (inviterRate != null && inviterRate.getMinAmount() > param.getMinAmount()) {
					throw new BizException(BizError.业务异常.getCode(),
							MessageFormat.format("通道[{0}]的最小金额不能低于代理商的最小金额,代理商的最小金额为:{1}",
									inviterRate.getChannel().getChannelName(), inviterRate.getMinAmount()));
				}
				if (inviterRate != null && inviterRate.getMaxAmount() < param.getMaxAmount()) {
					throw new BizException(BizError.业务异常.getCode(),
							MessageFormat.format("通道[{0}]的最大金额不能低于代理商的最大金额,代理商的最大金额为:{1}",
									inviterRate.getChannel().getChannelName(), inviterRate.getMaxAmount()));
				}
			}
		}
		List<GatheringChannelRate> rates = gatheringChannelRateRepo.findByMerchantId(merchantId);
		for (GatheringChannelRate rate : rates) {
			gatheringChannelRateRepo.delete(rate);
		}
		for (GatheringChannelRateParam param : params) {
			GatheringChannelRate rate = param.convertToPo(merchantId);
			gatheringChannelRateRepo.save(rate);
		}
	}

	@ParamValid
	@Transactional
	public void addOrUpdateGatheringChannel(AddOrUpdateGatheringChannelParam param) {
		GatheringChannel existGatheringChannel = gatheringChannelRepo
				.findByChannelCodeAndDeletedFlagIsFalse(param.getChannelCode());
		if (existGatheringChannel != null && !existGatheringChannel.getId().equals(param.getId())) {
			throw new BizException(BizError.收款通道已使用);
		}

		if (StrUtil.isNotBlank(param.getId())) {
			GatheringChannel gatheringChannel = gatheringChannelRepo.getOne(param.getId());
			BeanUtils.copyProperties(param, gatheringChannel);
			gatheringChannelRepo.save(gatheringChannel);
		} else {
			GatheringChannel gatheringChannel = param.convertToPo();
			gatheringChannelRepo.save(gatheringChannel);
		}
	}

	@Transactional(readOnly = true)
	public GatheringChannelVO findGatheringChannelById(@NotBlank String id) {
		return GatheringChannelVO.convertFor(gatheringChannelRepo.getOne(id));
	}

	@Transactional
	public void delGatheringChannelById(@NotBlank String id) {
		GatheringChannel gatheringChannel = gatheringChannelRepo.getOne(id);
		gatheringChannel.deleted();
		gatheringChannelRepo.save(gatheringChannel);
	}

	@Transactional(readOnly = true)
	public PageResult<GatheringChannelVO> findGatheringChannelByPage(GatheringChannelQueryCondParam param) {
		Specification<GatheringChannel> spec = new Specification<GatheringChannel>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<GatheringChannel> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("deletedFlag"), false));
				if (StrUtil.isNotBlank(param.getChannelCode())) {
					predicates.add(builder.like(root.get("channelCode"), "%" + param.getChannelCode() + "%"));
				}
				if (StrUtil.isNotBlank(param.getChannelName())) {
					predicates.add(builder.like(root.get("channelName"), "%" + param.getChannelName() + "%"));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		Page<GatheringChannel> result = gatheringChannelRepo.findAll(spec,
				PageRequest.of(param.getPageNum() - 1, param.getPageSize(), Sort.by(Sort.Order.desc("createTime"))));
		PageResult<GatheringChannelVO> pageResult = new PageResult<>(GatheringChannelVO.convertFor(result.getContent()),
				param.getPageNum(), param.getPageSize(), result.getTotalElements());
		return pageResult;
	}

	@Transactional(readOnly = true)
	public List<GatheringChannelVO> findAllGatheringChannel() {
		return GatheringChannelVO.convertFor(gatheringChannelRepo.findByDeletedFlagIsFalse());
	}

	@Transactional(readOnly = true)
	public List<GatheringChannelVO> findAllEnabledGatheringChannel() {
		return GatheringChannelVO.convertFor(gatheringChannelRepo.findByEnabledAndDeletedFlagIsFalse(true));
	}

}
