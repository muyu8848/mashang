package com.mashang.common.utils;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ThreadPoolUtils {

	private static ScheduledThreadPoolExecutor rechargeSettlementPool = new ScheduledThreadPoolExecutor(5);
	
	private static ScheduledThreadPoolExecutor usdtRechargePool = new ScheduledThreadPoolExecutor(8);

	private static ScheduledThreadPoolExecutor paidMerchantOrderPool = new ScheduledThreadPoolExecutor(10);

	private static ScheduledThreadPoolExecutor dispatchOrderPool = new ScheduledThreadPoolExecutor(8);
	
	private static ScheduledThreadPoolExecutor payForAnotherPool = new ScheduledThreadPoolExecutor(8);
	
	public static ScheduledThreadPoolExecutor getUsdtRechargePool() {
		return usdtRechargePool;
	}

	public static ScheduledThreadPoolExecutor getRechargeSettlementPool() {
		return rechargeSettlementPool;
	}

	public static ScheduledThreadPoolExecutor getPaidMerchantOrderPool() {
		return paidMerchantOrderPool;
	}

	public static ScheduledThreadPoolExecutor getDispatchOrderPool() {
		return dispatchOrderPool;
	}
	
	public static ScheduledThreadPoolExecutor getPayForAnotherPool() {
		return payForAnotherPool;
	}
}
