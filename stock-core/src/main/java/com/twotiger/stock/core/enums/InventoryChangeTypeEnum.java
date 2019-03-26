package com.twotiger.stock.core.enums;

import org.beetl.sql.core.annotatoin.EnumMapping;

@EnumMapping("value")
public enum InventoryChangeTypeEnum {

    下单冻结(1, "下单冻结"),
    支付成功扣减(2, "支付成功扣减"),
    未支付订单取消(3, "未支付订单取消"),
    支付订单取消(4, "支付订单取消"),
    售罄(5, "售罄");

    private int value;
    private String remark;

    InventoryChangeTypeEnum(int value, String remark) {
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

    public static InventoryChangeTypeEnum findByValue(int value) {
        InventoryChangeTypeEnum[] values = InventoryChangeTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].value == value) {
                return values[i];
            }
        }
        return null;
    }
}
