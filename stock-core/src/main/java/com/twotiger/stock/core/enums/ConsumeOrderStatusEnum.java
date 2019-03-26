package com.twotiger.stock.core.enums;

import org.beetl.sql.core.annotatoin.EnumMapping;

@EnumMapping("value")
public enum ConsumeOrderStatusEnum {

    PAY_WAIT(0, "待支付"),
    PAY_SUCCESS(1, "支付成功"),
    PAY_FAIL(2, "支付失败"),
    PAY_CANCEL(3, "已取消"),
    DELIVER_GOODS_WAIT(4, "待发货"),
    DELIVER_GOODS_YES(5, "已发货"),
    RETURN_GOODS_PROCESSING(6, "退货中"),
    RETURN_GOODS_YES(7, "已退货");

    private int value;
    private String remark;

    ConsumeOrderStatusEnum(int value, String remark) {
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

    public static ConsumeOrderStatusEnum findByValue(int value) {
        ConsumeOrderStatusEnum[] values = ConsumeOrderStatusEnum.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].value == value) {
                return values[i];
            }
        }
        return null;
    }
}
