package com.twotiger.stock.db.jpa;

import com.twotiger.stock.db.entity.EntityObject;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/4
 * Time: 23:23
 * To change this template use File | Settings | File Templates.
 */
public class BaseRepositoryFactoryBean<ID extends Serializable,E extends EntityObject<ID>,R extends BaseRepositorySupport<ID,E>> extends JpaRepositoryFactoryBean<R, E, ID> {

    public BaseRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
        return new BaseRepositoryFactory(em);
    }
}
