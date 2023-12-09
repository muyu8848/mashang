package com.mashang.useraccount.param;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.useraccount.domain.Menu;

import lombok.Data;

@Data
public class MenuParam {
	
	private String id;

	private String name;

	private String url;

	private String type;
	
	private Double orderNo;
	
	private String parentId;
	
	public Menu convertToPo() {
		Menu po = new Menu();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		po.setDeletedFlag(false);
		return po;
	}

}
