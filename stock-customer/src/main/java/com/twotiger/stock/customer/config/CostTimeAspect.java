package com.twotiger.stock.customer.config;



import com.twotiger.shop.logger.monitor.CostTimeAspectUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


/**
 * 
 * <br>类 名: CostTimeAspect
 * <br>描 述: 调用计时日志切面：记录调用的顺序
 * <br>作 者: liuqing
 * <br>创 建： 2015年6月29日
 * <br>版 本：1.5.2
 *
 * <br>历 史: (版本) 作者 时间 注释
 */
@Aspect
@Component
public class CostTimeAspect {

	/**
	 *
	 * <br>描 述：计时日志切面 服务层
	 * <br>作 者：liuqing
	 * <br>历 史: (版本) 作者 时间 注释
	 * @return
	 */
	@Around(value="execution(* com.twotiger.shop.core.service.impl..*.*(..))||execution(* com.twotiger.shop.core.service..*.*(..))||execution(* com.twotiger.shop.core.mapper..*.*(..))||execution(* com.twotiger.shop.core.dao..*.*(..))||execution(* com.twotiger.shop.customer.controller..*.*(..))")
	public Object aroundCall(ProceedingJoinPoint pjp)  throws Throwable {
		return CostTimeAspectUtil.commonCall(pjp);
	}
}
