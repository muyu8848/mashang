package com.mashang.mastercontrol.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.mashang.common.utils.IdUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "system_setting")
@DynamicInsert(true)
@DynamicUpdate(true)
public class SystemSetting implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", length = 32)
	private String id;

	private String websiteTitle;
	
	private String homePageUrl;
	
	private String appUrl;
	
	private String localStoragePath;

	private String currencyUnit;

	private Boolean merchantLoginGoogleAuth;
	
	private Boolean backgroundLoginGoogleAuth;
	
	private Boolean memberClientLoginGoogleAuth;

	private Date latelyUpdateTime;

	public static SystemSetting build() {
		SystemSetting setting = new SystemSetting();
		setting.setId(IdUtils.getId());
		return setting;
	}

}
