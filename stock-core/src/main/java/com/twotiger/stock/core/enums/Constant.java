package com.twotiger.stock.core.enums;

import org.beetl.sql.core.annotatoin.EnumMapping;

@EnumMapping("value")
public enum Constant {

    支付限时("pay_limit_time")
    ;

    private String nid;

    private Constant(String nid){
        this.nid = nid;
    }

    public String getNid() {
        return nid;
    }

   /* public final  static Map<String,String> consMap = new ConcurrentHashMap<String, String>();

    public static String conValue(String nid){
        return consMap.get(nid);
    }
    public static void update(String nid,String value){
        consMap.put(nid,value);
    }*/

}
