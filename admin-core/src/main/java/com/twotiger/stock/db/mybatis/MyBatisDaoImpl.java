package com.twotiger.stock.db.mybatis;

import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import javax.annotation.Resource;

/**
 * Created by liuqing on 14-9-29.
 */
public class MyBatisDaoImpl extends SqlSessionDaoSupport implements MybatisDao {

    /**
     * 自动注入
     * @param sqlSessionTemplate
     */
    @Resource(name="sqlSession")
    public final void setTemplate(SqlSessionTemplate sqlSessionTemplate){
        setSqlSessionTemplate(sqlSessionTemplate);
    }
}
