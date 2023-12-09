package com.mashang.gatheringcode.param;

import com.mashang.common.param.PageParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class GatheringCodeQueryCondParam extends PageParam {
	
	private String state;
	
	private String gatheringChannelId;
	
	private String payee;
	
	private String userName;
	
	private String userAccountId;
	
	private String detailInfo;
	

}
