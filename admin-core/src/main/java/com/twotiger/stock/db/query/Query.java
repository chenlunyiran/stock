package com.twotiger.stock.db.query;

/**
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/9
 * Time: 18:34
 * To change this template use File | Settings | File Templates.
 */



import java.util.*;

/**
 * 查询类 Query代表 where 后面 的查询条件 (group order limt 在page 里面设置)
 *
 * @author liuqing
 */
public class Query implements IQuery {

    /*************************************************************
     * 1 and 和 or 未实现 目前的Query 都是 and 操作 2
     * 参考org.hibernate.criterion.Restrictions 其中还有一些方法未实现
     * jgird
     *<option value="eq">等于　　</option>
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
     *
     *like  用于from 里面的查询参数
     *其他的需要还在这里面加  和 jgird 区分开来
     ****************************************************************/
    protected Map<String, Object> eq = null; // eques 等于 =
    protected Map<String, Object> ne = null; // no equal 不等于 <>
    protected Map<String, String> like = null; // like 模糊查询 like "%??%"
    protected Map<String, Comparable> ge = null; // greater than or equal 大于等于 >=
    protected Map<String, Comparable> gt = null; // greater than 大于 >
    protected Map<String, Comparable> le = null; // less than or equal 小于等于 <=
    protected Map<String, Comparable> lt = null; // less than 小于 <
    protected Map<String, String> bw = null; // 开始于 like 'xx%'
    protected Map<String, String> bn = null; // 不开始于  not like 'xx%'
    protected Map<String, String> ew = null; // 结束于  like '%xx'
    protected Map<String, String> en = null; // 不结束于  not like '%xx'
    protected Map<String, String> cn = null; //  like '%xx%'
    protected Map<String, String> nc = null; //   not like '%xx%'


    protected Map<String, Duad<Comparable, Comparable>> between  = null; // 在.. 之间
    // between 1 and
    // 100
    protected Map<String, Collection<Object>> in  = null; // 前台用多选 传到后面就是集合了?
    protected List<String> isNull = null; // isNull 为空 null
    protected List<String> notNull = null; // 非空
    protected List<String> isEmpty = null; // 为''
    protected List<String> notEmpty = null;// 不为''

    /**
     * Getter for property 'between'.
     *
     * @return Value for property 'between'.
     */
    public Map<String, Duad<Comparable, Comparable>> getBetween() {
        if(between == null){
            between = new HashMap<>();
        }
        return between;
    }

    /**
     * Getter for property 'bn'.
     *
     * @return Value for property 'bn'.
     */
    public Map<String, String> getBn() {
        if(bn == null){
            bn = new HashMap<>();
        }
        return bn;
    }

    /**
     * Getter for property 'bw'.
     *
     * @return Value for property 'bw'.
     */
    public Map<String, String> getBw() {
        if(bw == null){
            bw = new HashMap<>();
        }
        return bw;
    }

    /**
     * Getter for property 'cn'.
     *
     * @return Value for property 'cn'.
     */
    public Map<String, String> getCn() {
        if(cn == null){
            cn = new HashMap<>();
        }
        return cn;
    }

    /**
     * Getter for property 'en'.
     *
     * @return Value for property 'en'.
     */
    public Map<String, String> getEn() {
        if(en == null){
            en = new HashMap<>();
        }
        return en;
    }

    /**
     * Getter for property 'eq'.
     *
     * @return Value for property 'eq'.
     */
    public Map<String, Object> getEq() {
        if(eq == null){
            eq = new HashMap<>();
        }
        return eq;
    }

    /**
     * Getter for property 'ew'.
     *
     * @return Value for property 'ew'.
     */
    public Map<String, String> getEw() {
        if(ew == null){
            ew = new HashMap<>();
        }
        return ew;
    }

    /**
     * Getter for property 'ge'.
     *
     * @return Value for property 'ge'.
     */
    public Map<String, Comparable> getGe() {
        if(ge == null){
            ge = new HashMap<>();
        }
        return ge;
    }

    /**
     * Getter for property 'gt'.
     *
     * @return Value for property 'gt'.
     */
    public Map<String, Comparable> getGt() {
        if(gt == null){
            gt = new HashMap<>();
        }
        return gt;
    }

    /**
     * Getter for property 'in'.
     *
     * @return Value for property 'in'.
     */
    public Map<String, Collection<Object>> getIn() {
        if(in == null){
            in = new HashMap<>();
        }
        return in;
    }

    /**
     * Getter for property 'isEmpty'.
     *
     * @return Value for property 'isEmpty'.
     */
    public List<String> getIsEmpty() {
        if(isEmpty == null){
            isEmpty = new ArrayList<>();
        }
        return isEmpty;
    }

    /**
     * Getter for property 'isNull'.
     *
     * @return Value for property 'isNull'.
     */
    public List<String> getIsNull() {
        if(isNull == null){
            isNull = new ArrayList<>();
        }
        return isNull;
    }

    /**
     * Getter for property 'le'.
     *
     * @return Value for property 'le'.
     */
    public Map<String, Comparable> getLe() {
        if(le == null){
            le = new HashMap<>();
        }
        return le;
    }

    /**
     * Getter for property 'like'.
     *
     * @return Value for property 'like'.
     */
    public Map<String, String> getLike() {
        if(like == null){
            like = new HashMap<>();
        }
        return like;
    }

    /**
     * Getter for property 'lt'.
     *
     * @return Value for property 'lt'.
     */
    public Map<String, Comparable> getLt() {
        if(lt == null){
            lt = new HashMap<>();
        }
        return lt;
    }

    /**
     * Getter for property 'nc'.
     *
     * @return Value for property 'nc'.
     */
    public Map<String, String> getNc() {
        if(nc == null){
            nc = new HashMap<>();
        }
        return nc;
    }

    /**
     * Getter for property 'ne'.
     *
     * @return Value for property 'ne'.
     */
    public Map<String, Object> getNe() {
        if(ne == null){
            ne = new HashMap<>();
        }
        return ne;
    }

    /**
     * Getter for property 'notEmpty'.
     *
     * @return Value for property 'notEmpty'.
     */
    public List<String> getNotEmpty() {
        if(notEmpty == null){
            notEmpty = new ArrayList<>();
        }
        return notEmpty;
    }

    /**
     * Getter for property 'notNull'.
     *
     * @return Value for property 'notNull'.
     */
    public List<String> getNotNull() {
        if(notNull == null){
            notNull = new ArrayList<>();
        }
        return notNull;
    }

    @Override
    public Map<String, Duad<Comparable, Comparable>> between() {
        return between;
    }

    @Override
    public Map<String, String> bn() {
        return bn;
    }

    @Override
    public Map<String, String> bw() {
        return bw;
    }

    @Override
    public Map<String, String> cn() {
        return cn;
    }

    @Override
    public Map<String, String> en() {
        return en;
    }

    @Override
    public Map<String, Object> eq() {
        return eq;
    }

    @Override
    public Map<String, String> ew() {
        return ew;
    }

    @Override
    public Map<String, Comparable> ge() {
        return ge;
    }

    @Override
    public Map<String, Comparable> gt() {
        return gt;
    }

    @Override
    public Map<String, Collection<Object>> in() {
        return in;
    }

    @Override
    public List<String> isEmpty() {
        return isEmpty;
    }

    @Override
    public List<String> isNull() {
        return isNull;
    }

    @Override
    public Map<String, Comparable> le() {
        return le;
    }

    @Override
    public Map<String, String> like() {
        return like;
    }

    @Override
    public Map<String, Comparable> lt() {
        return lt;
    }

    @Override
    public Map<String, String> nc() {
        return nc;
    }

    @Override
    public Map<String, Object> ne() {
        return ne;
    }

    @Override
    public List<String> notEmpty() {
        return notEmpty;
    }

    @Override
    public List<String> notNull() {
        return notNull;
    }

    public void setEq(Map<String, Object> eq) {
        this.eq = eq;
    }

    public void setNe(Map<String, Object> ne) {
        this.ne = ne;
    }

    public void setLike(Map<String, String> like) {
        this.like = like;
    }

    public void setGe(Map<String, Comparable> ge) {
        this.ge = ge;
    }

    public void setGt(Map<String, Comparable> gt) {
        this.gt = gt;
    }

    public void setLe(Map<String, Comparable> le) {
        this.le = le;
    }

    public void setLt(Map<String, Comparable> lt) {
        this.lt = lt;
    }

    public void setBw(Map<String, String> bw) {
        this.bw = bw;
    }

    public void setBn(Map<String, String> bn) {
        this.bn = bn;
    }

    public void setEw(Map<String, String> ew) {
        this.ew = ew;
    }

    public void setEn(Map<String, String> en) {
        this.en = en;
    }

    public void setCn(Map<String, String> cn) {
        this.cn = cn;
    }

    public void setNc(Map<String, String> nc) {
        this.nc = nc;
    }

    public void setBetween(Map<String, Duad<Comparable, Comparable>> between) {
        this.between = between;
    }

    public void setIn(Map<String, Collection<Object>> in) {
        this.in = in;
    }

    public void setIsNull(List<String> isNull) {
        this.isNull = isNull;
    }

    public void setNotNull(List<String> notNull) {
        this.notNull = notNull;
    }

    public void setIsEmpty(List<String> isEmpty) {
        this.isEmpty = isEmpty;
    }

    public void setNotEmpty(List<String> notEmpty) {
        this.notEmpty = notEmpty;
    }

    /**
     * Getter for property 'between'.  mybatis query
     *
     * @return Value for property 'between'.
     */
    public Map<String, Duad<Comparable, Comparable>> getBetweenParam() {
        return between;
    }

    /**
     * Getter for property 'bn'. mybatis query
     *
     * @return Value for property 'bn'.
     */
    public Map<String, String> getBnParam() {
        if(bn==null)return null;
        Map<String, String> bnParam = new HashMap<>();
        bn.forEach((key,value)->{bnParam.put(key,"%"+value+"%");});
        return bnParam;
    }

    /**
     * Getter for property 'bw'. mybatis query
     *
     * @return Value for property 'bw'.
     */
    public Map<String, String> getBwParam() {
        if(bw==null)return null;
        Map<String, String> bwParam = new HashMap<>();
        bw.forEach((key,value)->{bwParam.put(key,value+"%");});
        return bwParam;
    }

    /**
     * Getter for property 'cn'. mybatis query
     *
     * @return Value for property 'cn'.
     */
    public Map<String, String> getCnParam() {
        if(cn==null)return null;
        Map<String, String> cnParam = new HashMap<>();
        cn.forEach((key,value)->{cnParam.put(key,value+"%");});
        return cnParam;
    }

    /**
     * Getter for property 'en'.  mybatis query
     *
     * @return Value for property 'en'.
     */
    public Map<String, String> getEnParam() {
        if(en==null)return null;
        Map<String, String> enParam = new HashMap<>();
        en.forEach((key,value)->{enParam.put(key,"%"+value);});
        return enParam;
    }

    /**
     * Getter for property 'eq'.  mybatis query
     *
     * @return Value for property 'eq'.
     */
    public Map<String, Object> getEqParam() {
        return eq;
    }

    /**
     * Getter for property 'ew'.  mybatis query
     *
     * @return Value for property 'ew'.
     */
    public Map<String, String> getEwParam() {
        if(ew==null)return null;
        Map<String, String> ewParam = new HashMap<>();
        ew.forEach((key,value)->{ewParam.put(key,"%"+value);});
        return ewParam;
    }

    /**
     * Getter for property 'ge'.  mybatis query
     *
     * @return Value for property 'ge'.
     */
    public Map<String, Comparable> getGeParam() {
        return ge;
    }

    /**
     * Getter for property 'gt'.  mybatis query
     *
     * @return Value for property 'gt'.
     */
    public Map<String, Comparable> getGtParam() {
        return gt;
    }

    /**
     * Getter for property 'in'.  mybatis query
     *
     * @return Value for property 'in'.
     */
    public Map<String, Collection<Object>> getInParam() {
        return in;
    }

    /**
     * Getter for property 'isEmpty'.  mybatis query
     *
     * @return Value for property 'isEmpty'.
     */
    public List<String> getIsEmptyParam() {
        return isEmpty;
    }

    /**
     * Getter for property 'isNull'. mybatis query
     *
     * @return Value for property 'isNull'.
     */
    public List<String> getIsNullParam() {
        return isNull;
    }

    /**
     * Getter for property 'le'. mybatis query
     *
     * @return Value for property 'le'.
     */
    public Map<String, Comparable> getLeParam() {
        return le;
    }

    /**
     * Getter for property 'like'. mybatis query
     *
     * @return Value for property 'like'.
     */
    public Map<String, String> getLikeParam() {
        if(like==null)return null;
        Map<String, String> likeParam = new HashMap<>();
        like.forEach((key,value)->{likeParam.put(key,"%"+value+"%");});
        return likeParam;
    }

    /**
     * Getter for property 'lt'. mybatis query
     *
     * @return Value for property 'lt'.
     */
    public Map<String, Comparable> getLtParam() {
        return lt;
    }

    /**
     * Getter for property 'nc'. mybatis query
     *
     * @return Value for property 'nc'.
     */
    public Map<String, String> getNcParam() {
        if(nc==null)return null;
        Map<String, String> ncParam = new HashMap<>();
        nc.forEach((key,value)->{ncParam.put(key,"%"+value+"%");});
        return ncParam;
    }

    /**
     * Getter for property 'ne'. mybatis query
     *
     * @return Value for property 'ne'.
     */
    public Map<String, Object> getNeParam() {
        return ne;
    }

    /**
     * Getter for property 'notEmpty'. mybatis query
     *
     * @return Value for property 'notEmpty'.
     */
    public List<String> getNotEmptyParam() {
        return notEmpty;
    }

    /**
     * Getter for property 'notNull'. mybatis query
     *
     * @return Value for property 'notNull'.
     */
    public List<String> getNotNullParam() {
        return notNull;
    }
}
