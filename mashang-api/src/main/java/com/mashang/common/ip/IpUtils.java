package com.mashang.common.ip;

import com.alibaba.fastjson.JSONObject;

import cn.hutool.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IpUtils {

	public static final String 太平洋电脑IP查询地址 = "http://whois.pconline.com.cn/ipJson.jsp?json=true&ip=";

	public static final String 淘宝IP查询地址 = "http://ip.taobao.com/service/getIpInfo.php?ip=";

	public static IpInfoVO getIpInfo(String ip) {
		try {
			String respResult = HttpUtil.get(太平洋电脑IP查询地址 + ip, 2000);
			JSONObject jsonObject = JSONObject.parseObject(respResult);
			IpInfoVO ipInfo = new IpInfoVO();
			ipInfo.setIp(ip);
			ipInfo.setProvinceCode(jsonObject.getString("proCode"));
			ipInfo.setProvince(jsonObject.getString("pro"));
			ipInfo.setCityCode(jsonObject.getString("cityCode"));
			ipInfo.setCity(jsonObject.getString("city"));
			return ipInfo;
		} catch (Exception e) {
			log.error("太平洋电脑ip查询异常", e);
		}
		try {
			String respResult = HttpUtil.get(淘宝IP查询地址 + ip, 2000);
			JSONObject jsonObject = JSONObject.parseObject(respResult);
			jsonObject = jsonObject.getJSONObject("data");
			IpInfoVO ipInfo = new IpInfoVO();
			ipInfo.setIp(ip);
			ipInfo.setCountryCode(jsonObject.getString("country_id"));
			ipInfo.setCountry(jsonObject.getString("country"));
			ipInfo.setProvinceCode(jsonObject.getString("region_id"));
			ipInfo.setProvince(jsonObject.getString("region"));
			ipInfo.setCityCode(jsonObject.getString("city_id"));
			ipInfo.setCity(jsonObject.getString("city"));
			return ipInfo;
		} catch (Exception e) {
			log.error("淘宝ip查询异常", e);
		}
		return null;
	}

}
