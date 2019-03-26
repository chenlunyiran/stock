package com.twotiger.stock.core.enums;

import org.beetl.sql.core.annotatoin.EnumMapping;

/**
 * 资金流水枚举类
 */
@EnumMapping("value")
public enum AccountLogTypeEnum {

    EXCHANGE(1,"兑换", 1),
    FROZEN(2,"冻结", -1),
    UN_FROZEN(3,"解冻", 1),
    CONSUME(4,"消费", -1),
    RETURN_BACK(5,"退回", 1);

    private int value;
    private String remark;
    private int isIncome;

    AccountLogTypeEnum(int value, String remark, int isIncome) {
        this.value = value;
        this.remark = remark;
        this.isIncome = isIncome;
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

    public int isIncome() {
        return isIncome;
    }

    public void setIsIncome(int isIncome) {
        this.isIncome = isIncome;
    }

    public static AccountLogTypeEnum findByValue(int value) {
        AccountLogTypeEnum[] values = AccountLogTypeEnum.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].value == value) {
                return values[i];
            }
        }
        return null;
    }
}
