package com.twotiger.stock.customer.util;

import com.twotiger.shop.core.entity.ShopCustomer;
import com.twotiger.stock.customer.constant.SystemContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 * 获取session中相关信息公共方法
 *
 *
 */
public class SessionUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionUtil.class);

	/**
	 * 获取session对象
	 * 
	 * @return
	 */
	public static HttpSession getSession() {
		HttpSession session = null;
		try {
			if(getRequest() != null)
				session = getRequest().getSession();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}
		return session;
	}

	public static HttpServletRequest getRequest() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		return attrs.getRequest();
	}

	public static HttpServletResponse getResponse() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes();
		return attrs.getResponse();
	}

	/**
	 * 获取当前登陆用户对象信息
	 *
	 * @return
	 */
	public static ShopCustomer getCurrentCustomer() {
		ShopCustomer shopCustomer = null;
		if(SessionUtil.getSession() != null){
			shopCustomer = (ShopCustomer) SessionUtil.getSession().getAttribute(SystemContents.SESSION_KEY);
		}
		return shopCustomer;
	}

	public static void setCurrentUser(ShopCustomer currentCustomer) {
		SessionUtil.getSession().setAttribute(SystemContents.SESSION_KEY,currentCustomer);
	}


    public static void invalidate() {
		HttpSession session = getSession();
		if(session != null){
			session.invalidate();
		}
	}
}
