package com.twotiger.stock.information.event;


/**
 * Created by liuqing-notebook on 2016/2/5.
 */
public abstract class AbsEvent<D> implements Event<D> {
    private final String id;

    private final D data;

    public AbsEvent(String id, D data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public D getData() {
        return data;
    }

    @Override
    public String getId() {
        return id;
    }
}
