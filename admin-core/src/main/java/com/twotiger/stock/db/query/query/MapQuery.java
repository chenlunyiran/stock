package com.twotiger.stock.db.query.query;




import com.twotiger.stock.db.query.Duad;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/10
 * Time: 10:59
 * To change this template use File | Settings | File Templates.
 */
public class MapQuery {

    //属性  ：{{查询类型：查询条件}，{查询类型：查询条件}...}
    private Map<String,List<Duad<Operation,Object>>> queryParam;
}
