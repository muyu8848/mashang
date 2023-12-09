package com.mashang.mastercontrol.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mashang.common.valid.ParamValid;
import com.mashang.mastercontrol.domain.CustomerServiceSetting;
import com.mashang.mastercontrol.domain.MerchantSettlementSetting;
import com.mashang.mastercontrol.domain.ReceiveOrderRiskSetting;
import com.mashang.mastercontrol.domain.ReceiveOrderSetting;
import com.mashang.mastercontrol.domain.RechargeSetting;
import com.mashang.mastercontrol.domain.RegisterSetting;
import com.mashang.mastercontrol.domain.SystemSetting;
import com.mashang.mastercontrol.domain.WithdrawSetting;
import com.mashang.mastercontrol.param.UpdateCustomerServiceSettingParam;
import com.mashang.mastercontrol.param.UpdateMerchantSettlementSettingParam;
import com.mashang.mastercontrol.param.UpdateReceiveOrderRiskSettingParam;
import com.mashang.mastercontrol.param.UpdateReceiveOrderSettingParam;
import com.mashang.mastercontrol.param.UpdateRechargeSettingParam;
import com.mashang.mastercontrol.param.UpdateRegisterSettingParam;
import com.mashang.mastercontrol.param.UpdateSystemSettingParam;
import com.mashang.mastercontrol.param.UpdateWithdrawSettingParam;
import com.mashang.mastercontrol.repo.CustomerServiceSettingRepo;
import com.mashang.mastercontrol.repo.MerchantSettlementSettingRepo;
import com.mashang.mastercontrol.repo.ReceiveOrderRiskSettingRepo;
import com.mashang.mastercontrol.repo.ReceiveOrderSettingRepo;
import com.mashang.mastercontrol.repo.RechargeSettingRepo;
import com.mashang.mastercontrol.repo.RegisterSettingRepo;
import com.mashang.mastercontrol.repo.SystemSettingRepo;
import com.mashang.mastercontrol.repo.WithdrawSettingRepo;
import com.mashang.mastercontrol.vo.CustomerServiceSettingVO;
import com.mashang.mastercontrol.vo.MerchantSettlementSettingVO;
import com.mashang.mastercontrol.vo.ReceiveOrderRiskSettingVO;
import com.mashang.mastercontrol.vo.ReceiveOrderSettingVO;
import com.mashang.mastercontrol.vo.RechargeSettingVO;
import com.mashang.mastercontrol.vo.RegisterSettingVO;
import com.mashang.mastercontrol.vo.SystemSettingVO;
import com.mashang.mastercontrol.vo.WithdrawSettingVO;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

@Validated
@Service
@Slf4j
public class MasterControlService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private ReceiveOrderSettingRepo receiveOrderSettingRepo;

	@Autowired
	private ReceiveOrderRiskSettingRepo receiveOrderRiskSettingRepo;

	@Autowired
	private RegisterSettingRepo registerSettingRepo;

	@Autowired
	private RechargeSettingRepo rechargeSettingRepo;

	@Autowired
	private WithdrawSettingRepo withdrawSettingRepo;

	@Autowired
	private CustomerServiceSettingRepo customerServiceSettingRepo;

	@Autowired
	private SystemSettingRepo systemSettingRepo;

	@Autowired
	private MerchantSettlementSettingRepo merchantSettlementSettingRepo;

	@Transactional
	public void autoUpdateUsdtCnyExchangeRate() {
		RechargeSetting rechargeSetting = rechargeSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (!rechargeSetting.getAutoUpdateUsdtCnyExchangeRate()) {
			return;
		}
		double usdtCnyExchangeRate = 0d;
		String result = HttpUtil.get("https://otc-api.huobi.co/v1/data/market/detail", 10000);
		JSONObject jsonObject = JSON.parseObject(result);
		if (!jsonObject.getBooleanValue("success")) {
			return;
		}
		JSONArray detail = jsonObject.getJSONObject("data").getJSONArray("detail");
		for (int i = 0; i < detail.size(); i++) {
			JSONObject json = detail.getJSONObject(i);
			if ("USDT".equals(json.getString("coinName"))) {
				usdtCnyExchangeRate = Double.parseDouble(json.getString("buy"));
				break;
			}
		}
		if (usdtCnyExchangeRate == 0) {
			return;
		}
		if (rechargeSetting.getUsdtCnyExchangeRate().compareTo(usdtCnyExchangeRate) == 0) {
			return;
		}
		rechargeSetting.setUsdtCnyExchangeRate(usdtCnyExchangeRate);
		rechargeSettingRepo.save(rechargeSetting);
	}

	@Transactional(readOnly = true)
	public MerchantSettlementSettingVO getMerchantSettlementSetting() {
		MerchantSettlementSetting setting = merchantSettlementSettingRepo.findTopByOrderByLatelyUpdateTime();
		return MerchantSettlementSettingVO.convertFor(setting);
	}

	@ParamValid
	@Transactional
	public void updateMerchantSettlementSetting(UpdateMerchantSettlementSettingParam param) {
		MerchantSettlementSetting setting = merchantSettlementSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = MerchantSettlementSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		merchantSettlementSettingRepo.save(setting);
	}

	@Transactional(readOnly = true)
	public SystemSettingVO getSystemSetting() {
		SystemSetting setting = systemSettingRepo.findTopByOrderByLatelyUpdateTime();
		return SystemSettingVO.convertFor(setting);
	}

	@ParamValid
	@Transactional
	public void updateSystemSetting(UpdateSystemSettingParam param) {
		SystemSetting setting = systemSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = SystemSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		systemSettingRepo.save(setting);
	}

	@Transactional(readOnly = true)
	public RegisterSettingVO getRegisterSetting() {
		RegisterSetting setting = registerSettingRepo.findTopByOrderByLatelyUpdateTime();
		return RegisterSettingVO.convertFor(setting);
	}

	@ParamValid
	@Transactional
	public void updateRegisterSetting(UpdateRegisterSettingParam param) {
		RegisterSetting setting = registerSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = RegisterSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		registerSettingRepo.save(setting);
	}

	@Transactional(readOnly = true)
	public ReceiveOrderSettingVO getReceiveOrderSetting() {
		ReceiveOrderSetting setting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		return ReceiveOrderSettingVO.convertFor(setting);
	}

	@ParamValid
	@Transactional
	public void updateReceiveOrderSetting(UpdateReceiveOrderSettingParam param) {
		ReceiveOrderSetting setting = receiveOrderSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = ReceiveOrderSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		receiveOrderSettingRepo.save(setting);
	}

	@Transactional(readOnly = true)
	public ReceiveOrderRiskSettingVO getReceiveOrderRiskSetting() {
		ReceiveOrderRiskSetting setting = receiveOrderRiskSettingRepo.findTopByOrderByLatelyUpdateTime();
		return ReceiveOrderRiskSettingVO.convertFor(setting);
	}

	@ParamValid
	@Transactional
	public void updateReceiveOrderRiskSetting(UpdateReceiveOrderRiskSettingParam param) {
		ReceiveOrderRiskSetting setting = receiveOrderRiskSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = ReceiveOrderRiskSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		receiveOrderRiskSettingRepo.save(setting);
	}

	@Transactional(readOnly = true)
	public RechargeSettingVO getRechargeSetting() {
		RechargeSetting setting = rechargeSettingRepo.findTopByOrderByLatelyUpdateTime();
		return RechargeSettingVO.convertFor(setting);
	}

	@ParamValid
	@Transactional
	public void updateRechargeSetting(UpdateRechargeSettingParam param) {
		RechargeSetting setting = rechargeSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = RechargeSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		rechargeSettingRepo.save(setting);
	}

	@Transactional(readOnly = true)
	public WithdrawSettingVO getWithdrawSetting() {
		WithdrawSetting setting = withdrawSettingRepo.findTopByOrderByLatelyUpdateTime();
		return WithdrawSettingVO.convertFor(setting);
	}

	@ParamValid
	@Transactional
	public void updateWithdrawSetting(UpdateWithdrawSettingParam param) {
		WithdrawSetting setting = withdrawSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = WithdrawSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		withdrawSettingRepo.save(setting);
	}

	@Transactional(readOnly = true)
	public CustomerServiceSettingVO getCustomerServiceSetting() {
		CustomerServiceSetting setting = customerServiceSettingRepo.findTopByOrderByLatelyUpdateTime();
		return CustomerServiceSettingVO.convertFor(setting);
	}

	@Transactional
	public void updateCustomerServiceSetting(UpdateCustomerServiceSettingParam param) {
		CustomerServiceSetting setting = customerServiceSettingRepo.findTopByOrderByLatelyUpdateTime();
		if (setting == null) {
			setting = CustomerServiceSetting.build();
		}
		BeanUtils.copyProperties(param, setting);
		setting.setLatelyUpdateTime(new Date());
		customerServiceSettingRepo.save(setting);
	}

	public void refreshCache(@NotEmpty List<String> cacheItems) {
		List<String> deleteSuccessKeys = new ArrayList<>();
		List<String> deleteFailKeys = new ArrayList<>();
		for (String cacheItem : cacheItems) {
			Set<String> keys = redisTemplate.keys(cacheItem);
			for (String key : keys) {
				Boolean flag = redisTemplate.delete(key);
				if (flag) {
					deleteSuccessKeys.add(key);
				} else {
					deleteFailKeys.add(key);
				}
			}
		}
		if (!deleteFailKeys.isEmpty()) {
			log.warn("以下的缓存删除失败:", deleteFailKeys);
		}
	}

}
