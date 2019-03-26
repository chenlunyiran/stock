package com.twotiger.stock.db.jpa;

import com.twotiger.stock.db.entity.EntityObject;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * 内部通用方法实现
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/2
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
@NoRepositoryBean
public  class BaseRepositoryExt<ID extends Serializable,E extends EntityObject<ID>> extends SimpleJpaRepository<E, ID> implements BaseRepositorySupport<ID,E> {

    protected final EntityManager entityManager;

    protected final JpaEntityInformation<E, ID> entityInformation;

    public BaseRepositoryExt(JpaEntityInformation<E, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }
}
