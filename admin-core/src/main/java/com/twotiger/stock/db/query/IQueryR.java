package com.twotiger.stock.db.query;




import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 查询参数 读取接口 （dao层使用）
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
public interface IQueryR {

    /**
     * 获取between查询条件
     * @return
     */
    Map<String, Duad<Comparable, Comparable>> between();

    /**
     * 不开始于
     * @return
     */
    Map<String, String> bn();

    /**
     * 开始于
     * @return
     */
    Map<String, String> bw();

    /**
     * 包含
     * @return
     */
    Map<String,String> cn();

    /**
     * 不结束于
     * @return
     */
    Map<String, String> en();

    /**
     * 等于
     * @return
     */
    Map<String, Object> eq();

    /**
     * 结束于
     * @return
     */
    Map<String, String> ew();

    /**
     * 大于等于
     * @return
     */
    Map<String, Comparable> ge();

    /**
     * 大于
     * @return
     */
    Map<String, Comparable> gt();

    /**
     * 属于
     * @return
     */
    Map<String, Collection<Object>> in();

    /**
     * 为空
     * @return
     */
    List<String> isEmpty();

    /**
     * 为null
     * @return
     */
    List<String> isNull();

    /**
     * 小于等于
     * @return
     */
    Map<String, Comparable> le();

    /**
     * like
     * @return
     */
    Map<String, String> like();

    /**
     * 小于
     * @return
     */
    Map<String, Comparable> lt();

    /**
     * 不包含
     * @return
     */
    Map<String, String> nc();

    /**
     * 不等于
     * @return
     */
    Map<String, Object> ne();

    /**
     * 不为空
     * @return
     */
    List<String> notEmpty();

    /**
     * 不为null
     * @return
     */
    List<String> notNull();
}
