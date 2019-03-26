package com.twotiger.stock.db.jpa;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.EntityManager;

/**
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/4
 * Time: 23:02
 * To change this template use File | Settings | File Templates.
 */
public class BaseRepositoryFactory extends JpaRepositoryFactory {
    /**
     * Creates a new {@link JpaRepositoryFactory}.
     *
     * @param entityManager must not be {@literal null}
     */
    public BaseRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
    }

    @Override
    protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager em) {
        Class clazz = information.getDomainType();
        JpaEntityInformation entityInformation =  getEntityInformation(clazz);
        return new BaseRepositoryExt(entityInformation, em);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.data.repository.support.RepositoryFactorySupport#
     * getRepositoryBaseClass()
     */
    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return BaseRepositoryExt.class;
    }
}
