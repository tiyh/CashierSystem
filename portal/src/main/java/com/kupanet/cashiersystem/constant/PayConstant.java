package com.kupanet.cashiersystem.constant;

import java.util.HashMap;
import java.util.Map;

public class PayConstant {
    public static final String RETURN_ALIPAY_VALUE_SUCCESS = "success";
    public static final String RETURN_ALIPAY_VALUE_FAIL = "fail";
    public enum PayStatus{
        FAILED(-1,"TRADE_CLOSED"),
        INIT(0,"WAIT_BUYER_PAY"),
        SUCCESS(1,"TRADE_SUCCESS"),
        FINISHED(2,"TRADE_FINISHED");
        private final int status_int;
        private final String status_string;
        private static Map<String,Integer> m =new HashMap<>();

        static {
            for(PayStatus payStatus:PayStatus.values()){
                m.put(payStatus.status_string,payStatus.status_int);
            }
        }

        PayStatus(int i,String str) {
            status_int=i;
            status_string=str;
        }
        public int getInt(){
            return status_int;
        }
        public String getString(){
            return status_string;
        }
        public static int getInt(String str){
            return m.get(str);
        }

    }
    /*public final static int PAY_STATUS_FAILED = -1; 	// 支付失败
    public final static int PAY_STATUS_INIT = 0; 		// 初始态
    public final static int PAY_STATUS_SUCCESS = 1; 	// 支付成功
    public final static int PAY_STATUS_COMPLETE = 2; 	// 业务完成

    public static class AlipayTradeStatus {
        public final static String WAIT = "WAIT_BUYER_PAY";		// 交易创建,等待买家付款
        public final static String CLOSED = "TRADE_CLOSED";		// 交易关闭
        public final static String SUCCESS = "TRADE_SUCCESS";		// 交易成功
        public final static String FINISHED = "TRADE_FINISHED";	// 交易成功且结束
    }*/
}
