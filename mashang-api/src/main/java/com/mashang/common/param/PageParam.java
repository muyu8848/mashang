package com.mashang.common.param;

import lombok.Data;

@Data
public class PageParam {

	/**
	 * 页码
	 */
	private Integer pageNum;

	/**
	 * 每页大小
	 */
	private Integer pageSize;

	/**
	 * 排序字段
	 */
	private String propertie;

	/**
	 * 排序方式
	 */
	private String direction;

}
