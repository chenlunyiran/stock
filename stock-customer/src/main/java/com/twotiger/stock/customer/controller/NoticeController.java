package com.twotiger.stock.customer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by alean on 2018/11/22.
 */
@Controller
@RequestMapping("/notice")
public class NoticeController {

    @GetMapping("chargeResult")
    public String notice(HttpServletRequest request){
        String result = request.getParameter("result");
        request.setAttribute("result",result);
        return "/notice/charge.html";
    }

    @GetMapping("errorPage")
    public String errorPage(){
        return "/notice/404.html";
    }

}
