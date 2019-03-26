package com.twotiger.stock.core.enums;

import org.beetl.sql.core.annotatoin.EnumMapping;

@EnumMapping("value")
public enum LaunchStatusEnum {
    LAUNCH_NO(0, "未上架"),
    LAUNCH_YES(1, "已上架");

    private int value;
    private String remark;

    LaunchStatusEnum(int value, String remark) {
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

    public static LaunchStatusEnum findByValue(int value) {
        LaunchStatusEnum[] values = LaunchStatusEnum.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].value == value) {
                return values[i];
            }
        }
        return null;
    }
}
