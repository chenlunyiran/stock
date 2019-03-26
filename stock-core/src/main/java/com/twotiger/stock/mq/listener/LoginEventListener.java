package com.twotiger.stock.mq.listener;

import com.twotiger.stock.information.mqevent.AbsMqEventListener;
import com.twotiger.stock.mq.event.LoginEvent;

import org.springframework.stereotype.Component;


@Component
public class LoginEventListener extends AbsMqEventListener<String,LoginEvent> {

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public void onEvent(String id, String eventData) {
        System.out.println("onEvent----onEvent----"+eventData);
    }

}
