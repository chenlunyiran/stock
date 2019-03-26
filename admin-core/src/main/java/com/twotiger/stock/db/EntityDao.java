package com.twotiger.stock.db;



import com.twotiger.stock.db.entity.EntityObject;

import java.io.Serializable;

/**
 * Created by liuqing on 14-10-6.
 */
public interface EntityDao <ID extends Serializable,E extends EntityObject<ID>> extends DbDao<ID,E> {

//    /**
//     * 取得实体名称
//     * @param clazz
//     * @param <E>
//     * @return
//     */
//  <E extends com.lou1052.data.po.PersisentObject>  String getEntityName(Class<E> clazz);
//
//    /**
//     * 取得实体属性名称
//     * @param clazz
//     * @param hasId 是否包含id
//     * @param <E>
//     * @return
//     */
//  <E extends com.lou1052.data.po.PersisentObject>  List<String> getEntityAttributes(Class<E> clazz,boolean hasId);
//
//
//    /**
//     * 取得实体主键名称
//     * @param clazz
//     * @param <E>
//     * @return
//     */
//  <E extends com.lou1052.data.po.PersisentObject>  String getEntityId(Class<E> clazz);
//
//
//    /**
//     * 取得实体对应表名称
//     * @param clazz
//     * @param <E>
//     * @return
//     */
//    <E extends com.lou1052.data.po.PersisentObject>  String getTableName(Class<E> clazz);
//
//    /**
//     * 取得实体属性对应列名称
//     * @param clazz
//     * @param hasPk 是否包含主键
//     * @param <E>
//     * @return
//     */
//    <E extends com.lou1052.data.po.PersisentObject>  List<String> getTableColumns(Class<E> clazz,boolean hasPk);
//
//
//    /**
//     * 取得实体对应表主键名称
//     * @param clazz
//     * @param <E>
//     * @return
//     */
//    <E extends com.lou1052.data.po.PersisentObject>  String getTablePk(Class<E> clazz);
//
//    /**
//     * 根据实体属性名取得表列名
//     * @param clazz
//     * @param AttributeName
//     * @param <E>
//     * @return
//     */
//    <E extends com.lou1052.data.po.PersisentObject>  String getColumnByAttribute(Class<E> clazz,String AttributeName);
//
//
//    /**
//     * 取得实体属性映射
//     * @param clazz
//     * @param <E>
//     * @return
//     */
//    <E extends com.lou1052.data.po.PersisentObject> Map<String,String> getAttributeMap(Class<E> clazz);






}
