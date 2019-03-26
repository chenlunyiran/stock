package com.twotiger.stock.mq.event;

import com.twotiger.stock.information.mqevent.AbsMqEvent;

public class LoginEvent extends AbsMqEvent<String> {


    private static final long serialVersionUID = 1232653355044245238L;

    public LoginEvent(String data) {
        super(data);
    }
}
