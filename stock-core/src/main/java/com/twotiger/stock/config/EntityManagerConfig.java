package com.twotiger.stock.config;

import com.twotiger.stock.db.jpa.BaseRepositoryFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by liuqing on 2017/6/18.
 */
@Configuration
@ImportResource(locations = "classpath*:config/entity-manage-config.xml")
@EnableJpaRepositories(
        basePackages = "com.twotiger.stock.core.dao",
        repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class,
        transactionManagerRef = "transactionManager",
        entityManagerFactoryRef = "entityManagerFactory",
        repositoryImplementationPostfix = "Impl"
)
public class EntityManagerConfig {

}
