package com.mashang.constants;

import java.util.Arrays;
import java.util.List;

public class Constant {

	public static final String 快速充值派单ID = "FAST_RECHARGE_DISPATCH_ORDER_ID";

	public static final String 会员额度类型_余额 = "balance";

	public static final String 会员额度类型_代充余额 = "serviceProviderAmount";

	public static final String 下发代付订单状态_待支付 = "1";

	public static final String 下发代付订单状态_支付失败 = "2";

	public static final String 下发代付订单状态_支付成功 = "3";

	public static final String 下发代付订单状态_待确认 = "4";

	public static final String 下发代付订单状态_待接单 = "5";

	public static final String 收益记录ID = "ORDER_INCOME_ID";

	public static final String 收益类型_代收收益 = "3";

	public static final String 收益类型_代收团队收益 = "4";

	public static final String 收益类型_下发代付收益 = "5";

	public static final String 收益类型_代充收益 = "6";

	public static final String USDT类型_ERC20 = "ERC20";

	public static final String USDT类型_TRC20 = "TRC20";

	public static final String 申诉发起方_会员 = "member";

	public static final String 申诉发起方_商户 = "merchant";

	public static final String 申诉类型_未支付申请取消订单 = "1";

	public static final String 申诉类型_实际支付金额与订单金额不符 = "2";

	public static final String 申诉状态_待处理 = "1";

	public static final String 申诉状态_已完结 = "2";

	public static final String 申诉处理方式_会员撤销申诉 = "1";

	public static final String 申诉处理方式_改为实际支付金额 = "2";

	public static final String 申诉处理方式_取消订单 = "3";

	public static final String 申诉处理方式_不做处理 = "4";

	public static final String 申诉处理方式_商户撤销申诉 = "5";

	public static final String 申诉处理方式_确认已支付 = "6";

	public static final String 菜单类型_一级菜单 = "menu_1";

	public static final String 菜单类型_二级菜单 = "menu_2";

	public static final String 菜单类型_按钮 = "btn";

	public static final String 已删除账号ID = "DELETED_ACCOUNT_ID";

	public static final String 收款通道_微信 = "wechat";
	
	public static final String 收款通道_微信红包 = "wechatRed";

	public static final String 收款通道_闲鱼代付 = "xydf";

	public static final String 收款通道_支付宝 = "alipay";

	public static final String 收款通道_银行卡 = "bankCard";

	public static final String 收款通道_支付宝转卡 = "alipayBankCard";

	public static final String 收款通道_微信转卡 = "wechatBankCard";
	
	public static final String 收款通道_财付通转卡 = "tenpayBankCard";

	public static final String 收款通道_微信手机转账 = "wechatMobile";

	public static final String 收款通道_支付宝转账 = "alipayTransfer";

	public static final String 收款通道_支付宝id转账 = "alipayIdTransfer";

	public static final String 收款通道_小钱袋 = "xqd";

	public static final String 收款通道_TRC20 = "trc20";

	public static final String 系统_会员端 = "member";

	public static final String 系统_商户端 = "merchant";

	public static final String 系统_后台管理 = "admin";

	public static final String 登录提示_登录成功 = "登录成功";

	public static final String 登录提示_不是管理员无法登陆后台 = "该账号不是管理员,无法登陆到后台";

	public static final String 登录提示_用户名或密码不正确 = "账号或密码不正确";

	public static final String 登录提示_谷歌验证码不正确 = "谷歌验证码不正确";

	public static final String 登录提示_账号已被禁用 = "你的账号已被禁用";

	public static final String 登录提示_用户名不存在 = "用户名不存在";

	public static final String 登录提示_后台账号不能登录会员端 = "后台账号不能登录会员端";

	public static final String 登录状态_成功 = "1";

	public static final String 登录状态_失败 = "0";

	public static final String 接单状态_正在接单 = "1";

	public static final String 接单状态_停止接单 = "2";

	public static final String 接单状态_禁止接单 = "3";

	public static final String 商户订单状态_等待接单 = "1";

	public static final String 商户订单状态_已接单 = "2";

	public static final String 商户订单状态_已支付 = "4";

	public static final String 商户订单状态_超时取消 = "5";

	public static final String 商户订单状态_人工取消 = "6";

	public static final String 商户订单状态_未确认超时取消 = "7";

	public static final String 商户订单状态_补单 = "8";

	public static final String 商户订单状态_取消订单退款 = "9";

	public static final String 商户订单状态_申诉中 = "10";

	public static final List<String> 商户订单已支付状态集合 = Arrays.asList(商户订单状态_已支付, 商户订单状态_补单);

	public static final List<String> 商户订单未支付状态集合 = Arrays.asList(商户订单状态_等待接单, 商户订单状态_已接单, 商户订单状态_超时取消, 商户订单状态_人工取消,
			商户订单状态_未确认超时取消, 商户订单状态_取消订单退款, 商户订单状态_申诉中);

	public static final String 账号类型_管理员 = "admin";

	public static final String 账号类型_会员 = "member";

	public static final String 商户账号类型_普通商户 = "merchant";

	public static final String 商户账号类型_商户代理 = "merchantAgent";

	public static final String 账号状态_启用 = "1";

	public static final String 账号状态_禁用 = "0";

	public static final Integer 充值订单默认有效时长 = 10;

	public static final String 下发代付订单_已支付订单单号 = "DISTRIBUTE_PAYOUT_ORDER_PAID_ORDER_NO";

	public static final String 充值订单_已支付订单单号 = "RECHARGE_ORDER_PAID_ORDER_NO";

	public static final String 充值方式_普通充值 = "platform";

	public static final String 充值方式_快速充值 = "serviceProvider";

	public static final String 充值订单状态_审核中 = "1";

	public static final String 充值订单状态_已支付 = "2";

	public static final String 充值订单状态_超时取消 = "4";

	public static final String 充值订单状态_支付失败 = "5";

	public static final String 充值订单状态_申诉中 = "6";

	public static final String 充值通道类型_银行卡 = "bankCard";

	public static final String 充值通道类型_USDT = "USDT";

	public static final String 账变日志类型_账号充值 = "1";

	public static final String 账变日志类型_接单扣款 = "2";

	public static final String 账变日志类型_代收收益 = "3";

	public static final String 账变日志类型_账号提现 = "4";

	public static final String 账变日志类型_提现不符退款 = "5";

	public static final String 账变日志类型_取消订单退款 = "6";

	public static final String 账变日志类型_代收团队收益 = "7";

	public static final String 账变日志类型_退还冻结资金 = "8";

	public static final String 账变日志类型_后台调整余额 = "9";

	public static final String 账变日志类型__改单为实际支付金额 = "10";

	public static final String 账变日志类型__下发代付收益 = "11";

	public static final String 账变日志类型_下发代付 = "12";

	public static final String 账变日志类型_转出到代充余额 = "13";

	public static final String 账变日志类型_代充余额转入 = "14";

	public static final String 账变日志类型_代充收益 = "15";

	public static final String 商户账变日志类型_已支付订单实收金额 = "1";

	public static final String 商户账变日志类型_增加余额 = "2";

	public static final String 商户账变日志类型_减少余额 = "3";

	public static final String 商户账变日志类型_提现结算 = "4";

	public static final String 商户账变日志类型_提现不符退款 = "5";

	public static final String 商户账变日志类型_发起代付 = "6";

	public static final String 商户账变日志类型_代付取消订单 = "7";

	public static final String 商户账变日志类型_代付异常退回 = "8";

	public static final String 商户账变日志类型_冻结资金 = "10";

	public static final String 商户账变日志类型_解冻资金 = "11";

	public static final String 商户账变日志类型_提现异常退回 = "12";

	public static final String 提现记录状态_审核中 = "1";

	public static final String 提现记录状态_审核通过 = "2";

	public static final String 提现记录状态_审核不通过 = "3";

	public static final String 提现记录状态_已到账 = "4";

	public static final String 提现方式_银行卡 = "bankCard";

	public static final String 商户订单ID = "MERCHANT_ORDER_ID";

	public static final String 派单订单ID = "DISPATCH_ORDER_ID";

	public static final String 异步通知订单ID = "ASYN_NOTICE_ORDER_ID";

	public static final String 实收金额记录ID = "ACTUAL_INCOME_RECORD_ID";

	public static final String 通知状态_未通知 = "1";

	public static final String 通知状态_通知成功 = "2";

	public static final String 通知状态_通知失败 = "3";

	public static final String 支付成功 = "1";

	public static final String 支付失败 = "2";

	public static final String 通知成功返回值 = "success";

	public static final String 商户结算状态_审核中 = "1";

	public static final String 商户结算状态_审核通过 = "2";

	public static final String 商户结算状态_审核不通过 = "3";

	public static final String 商户结算状态_已到账 = "4";

	public static final String 商户结算状态_下发待处理 = "5";

	public static final String 商户结算状态_下发处理中 = "6";

	public static final String 商户结算状态_异常退回 = "7";

	public static final String 收款码状态_正常 = "1";

	public static final String 收款码状态_待审核 = "2";

	public static final String 收款码状态_收款异常 = "3";

	public static final String 收款码审核类型_新增 = "1";

	public static final String 收款码审核类型_删除 = "2";

	public static final String 冻结记录ID = "FREEZE_RECORD_ID";

}
