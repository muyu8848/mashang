package com.mashang.distributepayout.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mashang.distributepayout.domain.DistributePayoutOrder;

import cn.hutool.core.collection.CollectionUtil;
import lombok.Data;

@Data
public class WaitReceivingDistributePayoutOrderVO {

	private String id;

	private Double amount;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;
	
	public static List<WaitReceivingDistributePayoutOrderVO> convertFor(List<DistributePayoutOrder> pos) {
		if (CollectionUtil.isEmpty(pos)) {
			return new ArrayList<>();
		}
		List<WaitReceivingDistributePayoutOrderVO> vos = new ArrayList<>();
		for (DistributePayoutOrder po : pos) {
			vos.add(convertFor(po));
		}
		return vos;
	}

	public static WaitReceivingDistributePayoutOrderVO convertFor(DistributePayoutOrder po) {
		if (po == null) {
			return null;
		}
		WaitReceivingDistributePayoutOrderVO vo = new WaitReceivingDistributePayoutOrderVO();
		BeanUtils.copyProperties(po, vo);
		return vo;
	}
	
}
