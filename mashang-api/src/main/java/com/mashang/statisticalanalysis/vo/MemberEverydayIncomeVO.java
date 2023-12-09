package com.mashang.statisticalanalysis.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.statisticalanalysis.domain.MemberEverydayIncome;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class MemberEverydayIncomeVO {

	private Double everydayIncome;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date everyday;

	private String userAccountId;

	public static List<MemberEverydayIncomeVO> convertFor(List<MemberEverydayIncome> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<MemberEverydayIncomeVO> vos = new ArrayList<>();
		for (MemberEverydayIncome po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static MemberEverydayIncomeVO convertFor(MemberEverydayIncome po) {
		if (po == null) {
			return null;
		}
		MemberEverydayIncomeVO vo = new MemberEverydayIncomeVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}

}
