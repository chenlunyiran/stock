package com.twotiger.stock.core.enums;

import org.beetl.sql.core.annotatoin.EnumMapping;

/**
 * 订单支付状态
 */
@EnumMapping("value")
public enum PayStatusEnum {

    PAY_WAIT(0, "待支付"),
    PAY_SUCCESS(1, "支付成功"),
    PAY_FAIL(2, "支付失败"),
    PAY_CANCEL(3, "已取消"),
    PAY_PART_SUCCESS(4, "部分支付");

    private int value;
    private String remark;

    PayStatusEnum(int value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public int value() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String remark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public static PayStatusEnum findByValue(int value) {
        PayStatusEnum[] values = PayStatusEnum.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].value == value) {
                return values[i];
            }
        }
        return null;
    }
}
