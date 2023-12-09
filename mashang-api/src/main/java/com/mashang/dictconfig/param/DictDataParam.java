package com.mashang.dictconfig.param;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.dictconfig.domain.DictItem;

import lombok.Data;

@Data
public class DictDataParam {

	/**
	 * 字典项code
	 */
	@NotBlank
	private String dictItemCode;

	/**
	 * 字典项名称
	 */
	@NotBlank
	private String dictItemName;

	public DictItem convertToPo() {
		DictItem po = new DictItem();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		return po;
	}

}
