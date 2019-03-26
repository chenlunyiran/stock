package com.twotiger.stock.util;

import com.ibeetl.admin.core.util.DateUtil;

import java.util.UUID;

public class IdUtil {

    private static final int PAY_TYPE = 1;
    private static final int CONSUME_ORDER_TYPE = 2;

    public static String payOrderId() {
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {
            hashCodeV = -hashCodeV;
        }
        return (DateUtil.getSystemTimeForLong() / 1000000000 + "").substring(2)
                + String.format("%02d", PAY_TYPE)
                + String.format("%012d", hashCodeV);
    }

    public static String consumeOrderId() {
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {
            hashCodeV = -hashCodeV;
        }
        return (DateUtil.getSystemTimeForLong() / 1000000000 + "").substring(2)
                + String.format("%02d", CONSUME_ORDER_TYPE)
                + String.format("%012d", hashCodeV);
    }

}
