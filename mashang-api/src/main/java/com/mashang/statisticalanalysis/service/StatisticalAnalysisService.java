package com.mashang.statisticalanalysis.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.mashang.statisticalanalysis.domain.AdjustCashDepositSituation;
import com.mashang.statisticalanalysis.domain.CashDeposit;
import com.mashang.statisticalanalysis.domain.MemberEverydayIncome;
import com.mashang.statisticalanalysis.domain.RechargeSituation;
import com.mashang.statisticalanalysis.domain.TradeSituation;
import com.mashang.statisticalanalysis.domain.WithdrawSituation;
import com.mashang.statisticalanalysis.repo.AdjustCashDepositSituationRepo;
import com.mashang.statisticalanalysis.repo.CashDepositRepo;
import com.mashang.statisticalanalysis.repo.ChannelUseSituationRepo;
import com.mashang.statisticalanalysis.repo.CollectForAnotherIncomeSituationRepo;
import com.mashang.statisticalanalysis.repo.MemberEverydayIncomeRepo;
import com.mashang.statisticalanalysis.repo.MemberIncomeRepo;
import com.mashang.statisticalanalysis.repo.MerchantAmountRepo;
import com.mashang.statisticalanalysis.repo.PlatformIncomeRepo;
import com.mashang.statisticalanalysis.repo.RechargeSituationRepo;
import com.mashang.statisticalanalysis.repo.TodayAccountReceiveOrderSituationRepo;
import com.mashang.statisticalanalysis.repo.TradeSituationRepo;
import com.mashang.statisticalanalysis.repo.WithdrawSituationRepo;
import com.mashang.statisticalanalysis.vo.AccountReceiveOrderSituationVO;
import com.mashang.statisticalanalysis.vo.AdjustCashDepositSituationVO;
import com.mashang.statisticalanalysis.vo.CashDepositVO;
import com.mashang.statisticalanalysis.vo.ChannelUseSituationVO;
import com.mashang.statisticalanalysis.vo.IncomeVO;
import com.mashang.statisticalanalysis.vo.MemberEverydayIncomeVO;
import com.mashang.statisticalanalysis.vo.MemberIncomeVO;
import com.mashang.statisticalanalysis.vo.MerchantAmountVO;
import com.mashang.statisticalanalysis.vo.RechargeSituationVO;
import com.mashang.statisticalanalysis.vo.TradeSituationVO;
import com.mashang.statisticalanalysis.vo.WithdrawSituationVO;

import cn.hutool.core.date.DateUtil;

@Validated
@Service
public class StatisticalAnalysisService {

	@Autowired
	private TodayAccountReceiveOrderSituationRepo todayAccountReceiveOrderSituationRepo;

	@Autowired
	private TradeSituationRepo tradeSituationRepo;

	@Autowired
	private WithdrawSituationRepo withdrawSituationRepo;

	@Autowired
	private RechargeSituationRepo rechargeSituationRepo;

	@Autowired
	private AdjustCashDepositSituationRepo adjustCashDepositSituationRepo;

	@Autowired
	private ChannelUseSituationRepo channelUseSituationRepo;

	@Autowired
	private CollectForAnotherIncomeSituationRepo collectForAnotherIncomeSituationRepo;

	@Autowired
	private CashDepositRepo cashDepositRepo;

	@Autowired
	private PlatformIncomeRepo platformIncomeRepo;

	@Autowired
	private MerchantAmountRepo merchantAmountRepo;

	@Autowired
	private MemberIncomeRepo memberIncomeRepo;

	@Autowired
	private MemberEverydayIncomeRepo memberEverydayIncomeRepo;

	@Transactional(readOnly = true)
	public List<MemberEverydayIncomeVO> findIncomeDetailByUserAccountIdAndTheMonth(String userAccountId,
			String theMonthStr) {
		Date theMonth = DateUtil.parse(theMonthStr, "yyyy-MM");
		Specification<MemberEverydayIncome> spec = new Specification<MemberEverydayIncome>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Predicate toPredicate(Root<MemberEverydayIncome> root, CriteriaQuery<?> query,
					CriteriaBuilder builder) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(builder.equal(root.get("userAccountId"), userAccountId));
				predicates.add(builder.greaterThanOrEqualTo(root.get("everyday").as(Date.class),
						DateUtil.beginOfMonth(theMonth)));
				predicates.add(builder.lessThanOrEqualTo(root.get("everyday").as(Date.class),
						DateUtil.endOfMonth(theMonth)));
				return predicates.size() > 0 ? builder.and(predicates.toArray(new Predicate[predicates.size()])) : null;
			}
		};
		List<MemberEverydayIncome> pos = memberEverydayIncomeRepo.findAll(spec, Sort.by(Sort.Order.desc("everyday")));
		return MemberEverydayIncomeVO.convertFor(pos);
	}

	@Transactional(readOnly = true)
	public MemberIncomeVO findMemberIncome(String userAccountId) {
		return MemberIncomeVO.convertFor(memberIncomeRepo.findById(userAccountId).orElse(null));
	}

	@Transactional(readOnly = true)
	public MerchantAmountVO findMerchantAmount() {
		return MerchantAmountVO.convertFor(merchantAmountRepo.findTopBy());
	}

	@Transactional(readOnly = true)
	public IncomeVO findPlatformIncome() {
		return IncomeVO.convertForPlatform(platformIncomeRepo.findTopBy());
	}

	@Transactional(readOnly = true)
	public CashDepositVO findCashDeposit() {
		CashDeposit cashDeposit = cashDepositRepo.findTopBy();
		return CashDepositVO.convertFor(cashDeposit);
	}

	@Transactional(readOnly = true)
	public IncomeVO findCollectForAnotherIncomeSituation() {
		return IncomeVO.convertForCollectForAnother(collectForAnotherIncomeSituationRepo.findTopBy());
	}

	@Transactional(readOnly = true)
	public AccountReceiveOrderSituationVO findMyTodayReceiveOrderSituation(@NotBlank String userAccountId) {
		return AccountReceiveOrderSituationVO
				.convertForToday(todayAccountReceiveOrderSituationRepo.findByReceivedAccountId(userAccountId));
	}

	@Transactional(readOnly = true)
	public List<ChannelUseSituationVO> findChannelUseSituation() {
		return ChannelUseSituationVO.convertFor(channelUseSituationRepo.findAll());
	}

	@Transactional(readOnly = true)
	public AdjustCashDepositSituationVO findAdjustCashDepositSituation() {
		AdjustCashDepositSituation situation = adjustCashDepositSituationRepo.findTopBy();
		return AdjustCashDepositSituationVO.convertFor(situation);
	}

	@Transactional(readOnly = true)
	public WithdrawSituationVO findWithdrawSituation() {
		WithdrawSituation situation = withdrawSituationRepo.findTopBy();
		return WithdrawSituationVO.convertFor(situation);
	}

	@Transactional(readOnly = true)
	public RechargeSituationVO findRechargeSituation() {
		RechargeSituation situation = rechargeSituationRepo.findTopBy();
		return RechargeSituationVO.convertFor(situation);
	}

	@Transactional(readOnly = true)
	public TradeSituationVO findTradeSituation() {
		TradeSituation tradeSituation = tradeSituationRepo.findTopBy();
		return TradeSituationVO.convertFor(tradeSituation);
	}

}
