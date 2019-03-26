package com.twotiger.stock.core.entity.dto;

import com.twotiger.stock.core.enums.InventoryChangeTypeEnum;

import java.io.Serializable;

/**
 * @description: 库存变更
 * @author: NYE
 * @create: 2018-11-30 14:16
 **/
public class InventoryChange implements Serializable {

    private static final long serialVersionUID = 8063620877425062044L;

    private InventoryChangeTypeEnum typeEnum;

    private long goodsId;

    private int count;

    private long bussId;

    private Integer totalQuantity;

    private Integer avaliableQuantity;

    private Integer frozenQuantity;

    public InventoryChangeTypeEnum getTypeEnum() {
        return typeEnum;
    }

    public void setTypeEnum(InventoryChangeTypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getBussId() {
        return bussId;
    }

    public void setBussId(long bussId) {
        this.bussId = bussId;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Integer getAvaliableQuantity() {
        return avaliableQuantity;
    }

    public void setAvaliableQuantity(Integer avaliableQuantity) {
        this.avaliableQuantity = avaliableQuantity;
    }

    public Integer getFrozenQuantity() {
        return frozenQuantity;
    }

    public void setFrozenQuantity(Integer frozenQuantity) {
        this.frozenQuantity = frozenQuantity;
    }
}
