package com.twotiger.stock.db;

import org.apache.ibatis.session.SqlSession;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import java.sql.Connection;

/**
 * Created by liuqing on 14-9-29.
 */
public interface Visit<R> {

    /**
     *
     * @param connection 使用数据库连接
     * @return
     */
    R visit(Connection connection);


    /**
     *
     * @param sqlSession  使用mybatis sqlSession
     * @return
     */
    R visit(SqlSession sqlSession);


    /**
     *
     * @param entityManager 使用Jpa EntityManager
     * @return
     */
    R visit(EntityManager entityManager);

    /**
     *
     * @param session 使用Hibernate Session
     * @return
     */
    R visit(Session session);

}
