package com.mashang.rechargewithdraw.service;

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
import com.mashang.constants.Constant;
import com.mashang.rechargewithdraw.domain.RechargeChannel;
import com.mashang.rechargewithdraw.param.AddOrUpdateRechargeChannelParam;
import com.mashang.rechargewithdraw.repo.RechargeChannelRepo;
import com.mashang.rechargewithdraw.vo.RechargeChannelVO;

import cn.hutool.core.util.StrUtil;

@Validated
@Service
public class RechargeChannelService {

	@Autowired
	private RechargeChannelRepo channelRepo;

	@Transactional(readOnly = true)
	public List<RechargeChannelVO> findEnabledRechargeChannel() {
		List<RechargeChannel> channels = channelRepo.findByEnabledIsTrueAndDeletedFlagIsFalseOrderByOrderNoAsc();
		return RechargeChannelVO.convertFor(channels);
	}

	@Transactional(readOnly = true)
	public List<RechargeChannelVO> findAllRechargeChannel() {
		List<RechargeChannel> channels = channelRepo.findByDeletedFlagIsFalseOrderByOrderNoAsc();
		return RechargeChannelVO.convertFor(channels);
	}

	@ParamValid
	@Transactional
	public void addOrUpdateRechargeChannel(AddOrUpdateRechargeChannelParam param) {
		if (Constant.充值通道类型_银行卡.equals(param.getChannelType())) {
			if (StrUtil.isBlank(param.getOpenAccountBank())) {
				throw new BizException(BizError.参数异常.getCode(), "开户银行不能为空");
			}
			if (StrUtil.isBlank(param.getAccountHolder())) {
				throw new BizException(BizError.参数异常.getCode(), "收款人不能为空");
			}
			if (StrUtil.isBlank(param.getBankCardAccount())) {
				throw new BizException(BizError.参数异常.getCode(), "银行卡号不能为空");
			}
		}
		if (Constant.充值通道类型_USDT.equals(param.getChannelType())) {
			if (StrUtil.isBlank(param.getAddressType())) {
				throw new BizException(BizError.参数异常.getCode(), "钱包类型不能为空");
			}
			if (StrUtil.isBlank(param.getAddress())) {
				throw new BizException(BizError.参数异常.getCode(), "钱包地址不能为空");
			}
		}
		// 新增
		if (StrUtil.isBlank(param.getId())) {
			RechargeChannel channel = param.convertToPo();
			channelRepo.save(channel);
		}
		// 修改
		else {
			RechargeChannel channel = channelRepo.getOne(param.getId());
			BeanUtils.copyProperties(param, channel);
			channelRepo.save(channel);
		}
	}

	@Transactional(readOnly = true)
	public RechargeChannelVO findRechargeChannelById(@NotBlank String id) {
		return RechargeChannelVO.convertFor(channelRepo.getOne(id));
	}

	@Transactional
	public void delRechargeChannelById(@NotBlank String id) {
		RechargeChannel channel = channelRepo.getOne(id);
		channel.deleted();
		channelRepo.save(channel);
	}

}
