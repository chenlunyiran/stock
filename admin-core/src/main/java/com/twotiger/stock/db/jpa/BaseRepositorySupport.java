package com.twotiger.stock.db.jpa;

import com.twotiger.stock.db.entity.EntityObject;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * 对外扩展接口
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/4
 * Time: 23:00
 * To change this template use File | Settings | File Templates.
 */
@NoRepositoryBean
public interface BaseRepositorySupport<ID extends Serializable,E extends EntityObject<ID>>  extends JpaRepositoryExt<ID,E>{

}
