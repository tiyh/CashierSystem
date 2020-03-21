package com.kupanet.cashiersystem.constant;

import java.util.HashMap;
import java.util.Map;

public class PayConstant {
    public enum AlipayReturnEnum{
        SUCCESS("success"),
        FAILED("fail");
        private String name;
        AlipayReturnEnum(String str){
            name=str;
        }
        public String getName(){
            return name;
        }
    }
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
}
