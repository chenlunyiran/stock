package com.twotiger.stock.customer.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wxming.
 * @date 2018/11/29.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public ModelAndView exceptionHandler(HttpServletRequest request,Exception ex){
        ModelAndView modelAndView = new ModelAndView();
        LOGGER.error("stock-customer统一拦截controller",ex);
        modelAndView.addObject("exception",ex);
        modelAndView.setViewName("/notice/errorPage");
        return modelAndView;
    }
}
