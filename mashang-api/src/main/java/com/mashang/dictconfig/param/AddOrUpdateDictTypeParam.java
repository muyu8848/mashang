package com.mashang.dictconfig.param;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.BeanUtils;

import com.mashang.common.utils.IdUtils;
import com.mashang.dictconfig.domain.DictType;

import lombok.Data;

@Data
public class AddOrUpdateDictTypeParam {

	/**
	 * 主键id
	 */
	private String id;

	/**
	 * 字典类型code
	 */
	@NotBlank
	private String dictTypeCode;

	/**
	 * 字典类型名称
	 */
	@NotBlank
	private String dictTypeName;

	/**
	 * 备注
	 */
	private String note;
	
	public DictType convertToPo() {
		DictType po = new DictType();
		BeanUtils.copyProperties(this, po);
		po.setId(IdUtils.getId());
		return po;
	}

}
