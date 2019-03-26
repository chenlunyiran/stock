package com.twotiger.stock.db.query;


import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 查询参数 设置接口
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/9
 * Time: 18:33
 * To change this template use File | Settings | File Templates.
 *
* <option value="eq">等于　　</option>
 *<option value="ne">不等　　</option>
 *<option value="lt">小于　　</option>
 *<option value="le">小于等于</option>
 *<option value="gt">大于　　</option>
 *<option value="ge">大于等于</option>
 *<option value="bw">开始于</option>
 *<option value="bn">不开始于</option>
 *<option value="in">属于　　</option>
 *<option value="ni">不属于</option>
 *<option value="ew">结束于</option>
 *<option value="en">不结束于</option>
 *<option value="cn">包含　　</option>
 *<option value="nc">不包含</option>
 */
public interface IQuery extends IQueryR{

    /**
     * 获取between查询条件
     * @return
     */
    Map<String, Duad<Comparable, Comparable>> getBetween();

    /**
     * 不开始于
     * @return
     */
    Map<String, String> getBn();

    /**
     * 开始于
     * @return
     */
    Map<String, String> getBw();

    /**
     * 包含
     * @return
     */
    Map<String, String> getCn();

    /**
     * 不结束于
     * @return
     */
    Map<String, String> getEn();

    /**
     * 等于
     * @return
     */
    Map<String, Object> getEq();

    /**
     * 结束于
     * @return
     */
    Map<String, String> getEw();

    /**
     * 大于等于
     * @return
     */
    Map<String, Comparable> getGe();

    /**
     * 大于
     * @return
     */
    Map<String, Comparable> getGt();

    /**
     * 属于
     * @return
     */
    Map<String, Collection<Object>> getIn();

    /**
     * 为空
     * @return
     */
    List<String> getIsEmpty();

    /**
     * 为null
     * @return
     */
    List<String> getIsNull();

    /**
     * 小于等于
     * @return
     */
    Map<String, Comparable> getLe();

    /**
     * like
     * @return
     */
    Map<String, String> getLike();

    /**
     * 小于
     * @return
     */
    Map<String, Comparable> getLt();

    /**
     * 不包含
     * @return
     */
    Map<String, String> getNc();

    /**
     * 不等于
     * @return
     */
    Map<String, Object> getNe();

    /**
     * 不为空
     * @return
     */
    List<String> getNotEmpty();

    /**
     * 不为null
     * @return
     */
    List<String> getNotNull();
}
