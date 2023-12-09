package com.mashang.statisticalanalysis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "v_channel_use_situation")
@DynamicInsert(true)
@DynamicUpdate(true)
public class ChannelUseSituation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private String channelName;

	private Integer accountTotal;

	private Integer inOrderAccountTotal;

	private Integer disabledAccountTotal;

	private Integer codeTotal;

	private Integer inOrderCodeTotal;

	private Integer onlineCodeTotal;

	private Integer exceptionCodeTotal;

}
