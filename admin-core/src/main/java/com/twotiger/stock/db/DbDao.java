package com.twotiger.stock.db;


import com.twotiger.stock.db.entity.EntityObject;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/5
 * Time: 22:24
 * To change this template use File | Settings | File Templates.
 */
public interface DbDao<ID extends Serializable,E extends EntityObject<ID>> {
}
