package com.mashang.dataclean.param;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class DataCleanParam {

	private List<String> dataTypes;

	@NotNull
	private Date startTime;

	@NotNull
	private Date endTime;

}
