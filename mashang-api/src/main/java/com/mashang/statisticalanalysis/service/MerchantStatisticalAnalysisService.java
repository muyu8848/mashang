package com.mashang.statisticalanalysis.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.mashang.common.valid.ParamValid;
import com.mashang.constants.Constant;
import com.mashang.merchant.domain.MerchantOrder;
import com.mashang.merchant.repo.MerchantOrderRepo;
import com.mashang.statisticalanalysis.domain.merchant.MerchantChannelTradeSituation;
import com.mashang.statisticalanalysis.domain.merchant.MerchantEverydayStatistical;
import com.mashang.statisticalanalysis.domain.merchant.MerchantTradeSituation;
import com.mashang.statisticalanalysis.param.MerchantIndexQueryParam;
import com.mashang.statisticalanalysis.param.MerchantOrderAnalysisCondParam;
import com.mashang.statisticalanalysis.repo.merchant.MerchantChannelTradeSituationRepo;
import com.mashang.statisticalanalysis.repo.merchant.MerchantEverydayStatisticalRepo;
import com.mashang.statisticalanalysis.repo.merchant.MerchantTradeSituationRepo;
import com.mashang.statisticalanalysis.vo.IndexStatisticalVO;
import com.mashang.statisticalanalysis.vo.MerchantChannelTradeSituationVO;
import com.mashang.statisticalanalysis.vo.MerchantTradeSituationVO;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;

@Validated
@Service
public class MerchantStatisticalAnalysisService {

	@Autowired
	private MerchantTradeSituationRepo merchantTradeSituationRepo;

	@Autowired
	private MerchantEverydayStatisticalRepo everydayStatisticalRepo;

	@Autowired
	private MerchantChannelTradeSituationRepo merchantChannelTradeSituationRepo;

	@Autowired
	private MerchantOrderRepo merchantOrderRepo;

	public Specification<MerchantOrder> buildMerchantOrderQueryCond(MerchantOrderAnalysisCondParam param) {
		Specification<MerchantOrder> spec = new Specification<MerchantOrder>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MerchantOrder> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (param.getStartTime() != null) {
					predicates.add(builder.greaterThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.beginOfDay(param.getStartTime())));
				}
				if (param.getEndTime() != null) {
					predicates.add(builder.lessThanOrEqualTo(root.get("submitTime").as(Date.class),
							DateUtil.endOfDay(param.getEndTime())));
				}
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		return spec;
	}

	@Transactional(readOnly = true)
	public List<MerchantChannelTradeSituationVO> findMerchantChannelTradeSituationByMerchantId(String merchantId) {
		List<MerchantChannelTradeSituation> tradeSituations = merchantChannelTradeSituationRepo
				.findByMerchantId(merchantId);
		return MerchantChannelTradeSituationVO.convertFor(tradeSituations);
	}

	@Transactional(readOnly = true)
	public MerchantTradeSituationVO findMerchantTradeSituationById(String merchantId) {
		return MerchantTradeSituationVO.convertFor(merchantTradeSituationRepo.findById(merchantId).orElse(null));
	}

	@Transactional(readOnly = true)
	public List<MerchantTradeSituationVO> findMerchantTradeSituation() {
		List<MerchantTradeSituation> merchantTradeSituations = merchantTradeSituationRepo.findAll();
		return MerchantTradeSituationVO.convertFor(merchantTradeSituations);
	}

	@Transactional(readOnly = true)
	public List<MerchantTradeSituationVO> findMerchantTradeSituation(MerchantOrderAnalysisCondParam param) {
		Map<String, MerchantTradeSituationVO> reportMap = new HashMap<>();
		Specification<MerchantOrder> spec = buildMerchantOrderQueryCond(param);
		List<MerchantOrder> merchantOrders = merchantOrderRepo.findAll(spec);
		for (MerchantOrder merchantOrder : merchantOrders) {
			String id = merchantOrder.getMerchantId();
			if (reportMap.get(id) == null) {
				MerchantTradeSituationVO vo = MerchantTradeSituationVO.build(id,
						merchantOrder.getMerchant().getUserName(), merchantOrder.getMerchant().getMerchantName());
				reportMap.put(id, vo);
			}
			MerchantTradeSituationVO vo = reportMap.get(id);
			vo.setOrderNum(vo.getOrderNum() + 1);
			if (Constant.商户订单状态_已支付.equals(merchantOrder.getOrderState())
					|| Constant.商户订单状态_补单.equals(merchantOrder.getOrderState())) {
				vo.setPaidOrderNum(vo.getPaidOrderNum() + 1);
				vo.setTradeAmount(vo.getTradeAmount() + merchantOrder.getGatheringAmount());
				vo.setPoundage(vo.getPoundage() + merchantOrder.getHandlingFee());
				vo.setMerchantAgentHandlingFee(
						vo.getMerchantAgentHandlingFee() + merchantOrder.getMerchantAgentHandlingFee());
			}
		}
		List<MerchantTradeSituationVO> vos = new ArrayList<>();
		Set<Entry<String, MerchantTradeSituationVO>> entrySet = reportMap.entrySet();
		for (Entry<String, MerchantTradeSituationVO> entry : entrySet) {
			MerchantTradeSituationVO vo = entry.getValue();
			vo.setTradeAmount(NumberUtil.round(vo.getTradeAmount(), 2).doubleValue());
			vo.setPoundage(NumberUtil.round(vo.getPoundage(), 2).doubleValue());
			vo.setActualIncome(NumberUtil.round(vo.getTradeAmount() - vo.getPoundage(), 2).doubleValue());
			vo.setSuccessRate(
					NumberUtil.round((double) vo.getPaidOrderNum() / vo.getOrderNum() * 100, 1).doubleValue());
			vos.add(vo);
		}
		return vos;
	}

	@Transactional(readOnly = true)
	public List<MerchantChannelTradeSituationVO> findChannelTradeSituation(MerchantOrderAnalysisCondParam param) {
		Map<String, MerchantChannelTradeSituationVO> reportMap = new HashMap<>();
		Specification<MerchantOrder> spec = buildMerchantOrderQueryCond(param);
		List<MerchantOrder> merchantOrders = merchantOrderRepo.findAll(spec);
		for (MerchantOrder merchantOrder : merchantOrders) {
			String id = merchantOrder.getGatheringChannelId();
			if (reportMap.get(id) == null) {
				MerchantChannelTradeSituationVO vo = MerchantChannelTradeSituationVO.build(id,
						merchantOrder.getGatheringChannel().getChannelName());
				reportMap.put(id, vo);
			}
			MerchantChannelTradeSituationVO vo = reportMap.get(id);
			vo.setOrderNum(vo.getOrderNum() + 1);
			if (Constant.商户订单状态_已支付.equals(merchantOrder.getOrderState())
					|| Constant.商户订单状态_补单.equals(merchantOrder.getOrderState())) {
				vo.setPaidOrderNum(vo.getPaidOrderNum() + 1);
				vo.setTradeAmount(vo.getTradeAmount() + merchantOrder.getGatheringAmount());
				vo.setPoundage(vo.getPoundage() + (merchantOrder.getGatheringAmount() * merchantOrder.getRate() / 100));
			}
		}
		List<MerchantChannelTradeSituationVO> vos = new ArrayList<>();
		Set<Entry<String, MerchantChannelTradeSituationVO>> entrySet = reportMap.entrySet();
		for (Entry<String, MerchantChannelTradeSituationVO> entry : entrySet) {
			MerchantChannelTradeSituationVO vo = entry.getValue();
			vo.setTradeAmount(NumberUtil.round(vo.getTradeAmount(), 4).doubleValue());
			vo.setPoundage(NumberUtil.round(vo.getPoundage(), 4).doubleValue());
			vo.setActualIncome(NumberUtil.round(vo.getTradeAmount() - vo.getPoundage(), 4).doubleValue());
			vo.setSuccessRate(
					NumberUtil.round((double) vo.getPaidOrderNum() / vo.getOrderNum() * 100, 1).doubleValue());
			vos.add(vo);
		}
		return vos;
	}

	@ParamValid
	@Transactional(readOnly = true)
	public List<IndexStatisticalVO> findEverydayStatistical(MerchantIndexQueryParam param) {
		List<MerchantEverydayStatistical> statisticals = everydayStatisticalRepo
				.findByMerchantIdAndEverydayGreaterThanEqualAndEverydayLessThanEqualOrderByEveryday(
						param.getMerchantId(), DateUtil.beginOfDay(param.getStartTime()),
						DateUtil.beginOfDay(param.getEndTime()));
		return IndexStatisticalVO.convertForEvery(statisticals);
	}

}
