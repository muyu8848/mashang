package com.mashang.merchant.vo;

import lombok.Data;

@Data
public class DispatchOrderTipVO {

	private String id;

	private String note;

	public static DispatchOrderTipVO build(String id, String note) {
		DispatchOrderTipVO vo = new DispatchOrderTipVO();
		vo.setId(id);
		vo.setNote(note);
		return vo;
	}

}
