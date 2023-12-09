package com.mashang.common.exception;

import lombok.Getter;

@Getter
public class ParamValidException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String code;

	private String msg;

	public ParamValidException(String code, String msg) {
		super(msg);
		this.code = code;
		this.msg = msg;
	}

}
