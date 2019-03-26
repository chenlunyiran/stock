package com.twotiger.stock.customer.intercept;

import com.twotiger.shop.core.entity.ShopCustomer;
import com.twotiger.stock.customer.util.SessionUtil;
import com.twotiger.shop.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by alean on 2018/10/22.
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    static Logger logger = LoggerFactory.getLogger(LoginInterceptor.class);


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//        ShopCustomer shopCustomer = new ShopCustomer();
//        shopCustomer.setName("借款徐");
//        shopCustomer.setIdCard("110101197506010134");
//        shopCustomer.setId(63L);
//        shopCustomer.setOriginalCustomerId(277021L);
//        shopCustomer.setRegistFrom("twotiger");
//        SessionUtil.setCurrentUser(shopCustomer);

        ShopCustomer currentCustomer = SessionUtil.getCurrentCustomer();
        if (StringUtil.isBlank(currentCustomer)) {
            response.sendRedirect("/notice/errorPage");
            return false;
        }
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}
