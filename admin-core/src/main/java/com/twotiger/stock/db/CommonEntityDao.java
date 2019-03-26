package com.twotiger.stock.db;

import com.twotiger.stock.db.entity.EntityObject;
import com.twotiger.stock.db.query.Duad;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 关于实体通用的Dao
 * 提供了针对实体的增删改查(CRUD)功能的DAO基础接口。
 * Created by liuqing on 14-10-5.
 */
public interface CommonEntityDao extends EntityDao {

    /**
     * 持久化一个实体。
     *
     * @param entity 处于临时状态的实体。
     * 
     */
    <E extends EntityObject> void insert(E entity) ;

    /**
     * 持久化多个实体。
     *
     * @param entities 处于临时状态的实体的集合。
     * 
     */
    <E extends EntityObject> void insert(Collection<E> entities) ;

    /**
     * 更新实体。
     *
     * @param entity 处于持久化状态的实体。
     * 
     */
    <E extends EntityObject> void update(E entity) ;

    /**
     * 更新多个实体。
     *
     * @param entities 处于持久化状态的实体的集合。
     * 
     */
    <E extends EntityObject> void update(Collection<E> entities) ;

    /**
     * 持久化或者更新实体。
     *
     * @param entity 处于临时或者持久化状态的实体。
     * 
     */
    <E extends EntityObject> void insertOrUpdate(E entity) ;

    /**
     * 持久化或者更新多个实体。
     *
     * @param entities 处于临时或者持久化状态的实体的集合。
     * 
     */
    <E extends EntityObject> void insertOrUpdate(Collection<E> entities) ;

    /**
     * 更新处于游离状态的实体，更新后实体对象仍然处于游离状态。
     *
     * @param entity 处于游离状态的实体。
     * 
     */
    <E extends EntityObject> void merge(E entity) ;

    /**
     * 保存处于游离状态的多个实体，更新后实体对象仍然处于游离状态。
     *
     * @param entities 处于游离状态的实体的集合。
     * 
     */
    <E extends EntityObject> void merge(Collection<E> entities) ;

    /**
     * 删除一个持久化的实体。
     *
     * @param entity 处于持久化状态的实体。
     * 
     */
    <E extends EntityObject> void delete(E entity) ;

    /**
     * 删除多个持久化的实体。
     *
     * @param entities 处于持久化状态的实体的集合。
     * 
     */
    <E extends EntityObject> void delete(Collection<E> entities) ;

    /**
     * 按照唯一的（Unique）属性名和属性值，查询得到一个实体对象。
     *
     * @param uniqueParamName 实体唯一属性名
     * @param value 属性值
     * @return 持久化实体
     * 
     */
    <E extends EntityObject> E selectByUniqueParam(String uniqueParamName, Object value, Class<E> clazz) ;

    /**
     * 按照指定的属性值查询多个实体对象。
     *
     * @param paramName 实体属性名。
     * @param value 对应实体属性名的属性值。
     * @return 持久化实体集合
     * 
     */
    <E extends EntityObject> List<E> selectByParam(String paramName, Object value, Class<E> clazz) ;

    /**
     * 按照指定属性值查找多个实例，并按照分页条件分页，返回指定页的实体集合。
     *
     * @param paramName 作为查询条件的属性名。
     * @param value 查询条件属性值。
     * @param pageSize 每页大小。
     * @param pageNumber 查询的页码，0表示第一页。
     * @return 持久化实体集合
     * 
     */
    <E extends EntityObject> List<E> selectByParamPagination(String paramName, Object value, int pageSize, int pageNumber, Class<E> clazz)
            ;

    /**
     * 按照指定属性值、排序条件和分页条件进行查找指定页的多个实例。
     *
     * @param paramName 作为查询条件的属性名。
     * @param value 查询条件属性值。
     * @param orderParam 排序的属性名，null为没有排序条件
     * @param isDescending 是否是降序排序，当orderParam可用是才有效。
     * @param pageSize 每页大小。
     * @param pageNumber 查询的页码，0表示第一页。
     * @return 指定页的持久化实体集合
     * 
     */
    <E extends EntityObject> List<E> selectByParamPagination(String paramName, Object value, String orderParam,
                                                             boolean isDescending, int pageSize, int pageNumber, Class<E> clazz) ;

    /**
     * 按照指定的属性值映射查找多个实体对象。
     *
     * @param paramMap 实体类属性名和属性值的映射。
     * @return 持久化实体集合
     * 
     */
    <E extends EntityObject> List<E> selectByParams(Map<String, Object> paramMap, Class<E> clazz) ;

    /**
     * 按照指定的条件表达式查找多个实体对象。
     * <pre>
     * Map params = new HashMap();
     * params.put("name", "Jack");
     * params.put("age", "16");
     * List result = selectByParams("userName = :name and userAge = :age", params);
     * </prc>
     * @param extraCondition 包含形如":param"的条件表达式
     * @param extraParams 条件表达式中的参数值
     * @return 持久化实体集合
     * 
     */
    <E extends EntityObject> List<E> selectByParams(String extraCondition, Map<String, Object> extraParams, Class<E> clazz) ;

    /**
     * 按照指定的属性值映射查找多个实体对象。
     * <pre>
     * Map params1 = new HashMap();
     * params1.put("name", "Jack");
     * Map params2 = new HashMap();
     * params.put("age1", "16");
     * params.put("age2", "18");
     * List result = selectByParams(params1, " and (userAge = :age1 or userAge = :age2)", params);
     * </prc>
     * @param paramMap 实体类属性名和属性值的映射。
     * @param extraCondition 额外的查询条件，跟在paramMap的后面，类似“ OR XX = :xx”的形式，没有则为null。
     * @param extraParams 配合extraCondition使用，用于保存extraCondition中的变量。
     * @return 持久化实体集合
     * 
     */
    <E extends EntityObject> List<E> selectByParams(Map<String, Object> paramMap, String extraCondition, Map<String, Object> extraParams, Class<E> clazz) ;

    /**
     * 按照指定属性值映射、分页条件查找多个实例。
     * @param paramMap 查询条件，
     * @param pageSize 每页大小。
     * @param pageNumber 查询的页码，0表示第一页。
     * @return 指定页的持久化实体集合
     * 
     */
    <E extends EntityObject>List< E> selectByParamsPagination(Map<String, Object> paramMap, int pageSize, int pageNumber, Class<E> clazz)
            ;

    /**
     * 按照指定属性值映射查找多个实例，并按照分页条件分页，返回指定页的实体列表。
     * @param condition 查询条件，类似“ WHERE AA = :aa OR XX = :xx”的形式，没有则为null。
     * @param params 配合condition使用，用于保存condition中的变量值。
     * @param pageSize 每页大小。
     * @param pageNumber 查询的页码，0表示第一页。
     * @return 指定页的持久化实体集合
     * 
     */
    <E extends EntityObject> List< E> selectByParamsPagination(String condition, Map<String, Object> params, int pageSize, int pageNumber, Class<E> clazz)
            ;

    /**
     * 按照指定属性值映射查找多个实例，并按照分页条件分页，返回指定页的实体列表。
     *
     * @param paramMap 查询条件，如果需要更灵活的条件，使用extraCondition。
     * @param extraCondition 额外的查询条件，跟在paramMap的后面，类似“ OR XX = :xx”的形式，没有则为null。
     * @param extraParams 配合extraCondition使用，用于保存extraCondition中的变量。
     * @param pageSize 每页大小。
     * @param pageNumber 查询的页码，0表示第一页。
     * @return 指定页的持久化实体集合
     * 
     */
    <E extends EntityObject> List< E> selectByParamsPagination(Map<String, Object> paramMap, String extraCondition,
                                                               Map<String, Object> extraParams, int pageSize, int pageNumber, Class<E> clazz) ;

    /**
     * 按照指定属性值映射、排序条件和分页条件进行查找指定页的多个实例。
     * @param paramMap 查询条件。
     * @param orderParam 排序的属性名，null为没有排序条件
     * @param isDescending 是否是降序排序，当orderParam可用是才有效。
     * @param pageSize 每页大小。
     * @param pageNumber 查询的页码，0表示第一页。
     * @return  指定页的持久化实体集合
     * 
     */
    <E extends EntityObject>List<E> selectByParamsPagination(Map<String, Object> paramMap, String orderParam, boolean isDescending,
                                                             int pageSize, int pageNumber, Class<E> clazz) ;

    /**
     * 按照指定参数值映射和额外的查询条件、排序条件和分页条件查找多个实例。
     * @param paramMap 查询条件。
     * @param extraCondition 额外查询条件，跟在paramMap的后面，类似“ OR XX = :xx”的形式，没有则为null。
     * @param extraParams 配合extraCondition使用，用于保存extraCondition中的变量。
     * @param orderParam 排序的属性名，null为没有排序条件
     * @param isDescending 是否是降序排序，当orderParam可用是才有效。
     * @param pageSize 每页大小。
     * @param pageNumber 查询的页码，0表示第一页。
     * @return 指定页的持久化实体集合
     * 
     */
    <E extends EntityObject>List< E> selectByParamsPagination(Map<String, Object> paramMap, String extraCondition,
                                                              Map<String, Object> extraParams, String orderParam, boolean isDescending,
                                                              int pageSize, int pageNumber, Class<E> clazz) ;

    /**
     * 按照泛型的实体类型查询得到所有持久化实体。
     * 如果实体类被设置为缓存的，则该方法首先从缓存中获取。
     * @return 所有持久化实体的集合
     * 
     */
    //List< Entity> selectAll() ;

    /**
     * 按照指定实体类型查询得到所有持久化实体。
     * 如果实体类被设置为缓存的，则该方法首先从缓存中获取。
     *
     * @return 所有持久化实体的集合
     * 
     */
    <E extends EntityObject>List< E> selectAll(Class<E> clazz) ;


    /**
     * 在所有的持久化实体中查询指定页的实体集合。
     *
     * @param pageSize 每页大小
     * @param pageNumber 查询的页码，0表示第一页。
     * @return 指定页的持久化实体集合
     * 
     */
    <E extends EntityObject>List< E> selectAllByPagination(int pageSize, int pageNumber, Class<E> clazz) ;

    /**
     * 在所有的持久化实体中按照排序方式查询指定页的实体集合。
     *
     * @param orderParam 排序的属性名，null为没有排序条件
     * @param isDescending 是否是降序排序，当orderParam可用是才有效。
     * @param pageSize 每页大小。
     * @param pageNumber 查询的页码，0表示第一页。
     * @return 指定页的持久化实体集合
     * 
     */
    <E extends EntityObject>List< E> selectAllByPagination(String orderParam, boolean isDescending, int pageSize, int pageNumber, Class<E> clazz)
            ;

    /**
     * 统计所有持久化实体对象的数量。
     *
     * @return 所有持久化实体对象的数量
     * 
     */
    <E extends EntityObject> long countAll(Class<E> clazz) ;

    /**
     * 按条件统计持久化实体对象的数量。
     *
     * @param paramName
     * @param value
     * @return 持久化实体的数量
     * 
     */
    <E extends EntityObject>long countByParam(String paramName, Object value, Class<E> clazz) ;

    /**
     * 按给定的限制条件统计持久化实体对象的数量。
     *
     * @param paramMap 查询条件参数
     * @return 符合条件的持久化实体的数量
     * 
     */
    <E extends EntityObject>long countByParams(Map<String, Object> paramMap, Class<E> clazz) ;

    /**
     * 按给定的限制条件统计实体对象的数量。
     * @param paramMap 查询条件参数
     * @param extraCondition 额外查询条件，跟在paramMap的后面，类似“ OR XX = :xx”的形式，没有则为null。
     * @param extraParams 配合extraCondition使用，用于保存extraCondition中的变量。
     * @return 符合条件的持久化实体的数量
     * 
     */
    <E extends EntityObject>long countByParams(Map<String, Object> paramMap, String extraCondition, Map<String, Object> extraParams, Class<E> clazz) ;



    /**
     * 按照实体类型和实体唯一标识查询实体。
     *
     * @param key
     * @return
     * 
     */
    //@Transactional(readOnly = true)
    //Entity select(long id) ;

    /**
     * 按照实体类型和实体唯一标识查询实体。
     * @param id
     * @return
     * @throws org.springframework.dao.DataAccessException
     */
    <I extends Serializable,  E extends EntityObject<I>> E select(I id, Class<E> clazz) ;

    /**
     * 通过给定复合主键的各个属性值来查找实体对象。
     *
     * @param keyNames
     * @param keyValues
     * @return
     * 
     */
    <E extends EntityObject>E select(String[] keyNames, Object[] keyValues, Class<E> clazz) ;

    /**
     * 按照实体类型和实体唯一标识查询实体，并锁定该实体对象，直到事务结束。
     *
     * @param id
     * @return
     * 
     */
    //@Transactional(readOnly = true)
    //<E extends Entity>E selectAndLock(long id) ;

    /**
     * 按照实体类型和实体唯一标识查询实体，并锁定该实体对象，直到事务结束。
     * @param id
     * @return
     */
    <I extends Serializable,  E extends EntityObject<I>> E selectAndLock(I id, Class<E> clazz) ;

    /**
     * 按照给定的实体类型和唯一标识查询实体。通用的查询方法，适用于所有的继承SingleKeyPojo的实体类。
     * @param clazz
     * @param id
     * @return
     * 
     */
    //@Transactional(readOnly = true)
    //<E extends Entity> E select(Class<E> clazz, long id) ;



    // /**
    // * Find entity from DB by long type id.
    // * @param key
    // * @return
    // */
    // E select(Class<E> type, long id);
    //
    // /**
    // * Find entity from DB by Long type id.
    // * @param type
    // * @param id
    // * @return
    // */
    // E select(Class<E> type, Long id);

    /**
     * 删除实体主键id标识的实体。
     *
     * @param id
     * 
     */
    //void delete(long id) ;

    /**
     * 通过复合主键类的实例来删除实体对象。
     *
     * @param id
     * 
     */
    <I extends Serializable,E extends EntityObject<I>>  void delete(I id, Class<E> clazz) ;

    /**
     * 通过给定复合主键的各个属性值来删除实体对象。
     *
     * @param keyNames 主键各个字段名
     * @param keyValues 主键各个字段值
     * 
     */
    <E extends EntityObject> void delete(String[] keyNames, Object[] keyValues, Class<E> clazz) ;







    /**
     *
     * 根据指定的实体对象插入记录
     *
     * @param entity	: 要插入的对象
     * @return	: 主键值
     *
     */
    <I extends Serializable, E extends EntityObject<I>> I save(E entity);

    /**
     *
     * 根据指定的实体对象列表插入记录
     *
     * @param entities	: 要插入的对象列表
     * @return		: 主键值列表
     *
     */
    <I extends Serializable, E extends EntityObject<I>> List<I> save(List<E> entities);

//    /**
//     *
//     * 根据指定的实体对象更新记录
//     *
//     * @param entity	: 要更新的对象
//     *
//     */
//     <E extends Entity> void update(E entity);

    /**
     *
     * 根据指定的实体对象列表更新记录
     *
     * @param entities	: 要更新的对象列表
     *
     */
    < E extends EntityObject> void update(List<E> entities);

    /**
     *
     * 根据指定的实体对象列表插入或更新记录
     *
     * @param entity	: 要插入或更新的对象
     *
     */
     < E extends EntityObject> void saveOrUpdate(E entity);

    /**
     *
     * 根据指定的实体对象列表插入或更新记录
     *
     * @param entities	: 要插入或更新的对象列表
     *
     */
    < E extends EntityObject> void saveOrUpdate(List<E> entities);



    /**
     *
     * 根据指定的实体对象列表删除记录
     *
     * @param entities	: 要删除的对象列表
     *
     */
    < E extends EntityObject> void delete(List<E> entities);



    /**
     *
     * 把指定的实体对象列表同步到数据库
     *
     * @param entities	: 要同步的对象列表
     * @return		: 同步后的对象列表
     *
     */
    < E extends EntityObject> List<E> merge(List<E> entities);

    /**
     *
     * 根据主键获取实体对象
     *
     * @param objClass	: 要加载的对象类型
     * @param id		: 主键
     * @return			: 加载的对象，如果找不到则返回 NULL
     *
     */
    <I extends Serializable, E extends EntityObject<I>> E get(Class<E> objClass, I id);

    /**
     *
     * 根据主键列表获取实体对象列表
     *
     * @param objClass	: 要加载的对象类型
     * @param ids		: 主键列表
     * @return			: 加载的对象列表，如果找不到则对应的列表项为 NULL
     *
     */
    <I extends Serializable, E extends EntityObject<I>> List<E> get(Class<E> objClass, List<I> ids);

    /**
     *
     * 根据主键获取实体对象
     *
     * @param objClass	: 要加载的对象类型
     * @param id		: 主键
     * @return			: 加载的对象，如果找不到则抛出异常
     *
     */
    <I extends Serializable, E extends EntityObject<I>> E load(Class<E> objClass, I id);

    /**
     *
     * 根据主键列表获取实体对象列表
     *
     * @param objClass	: 要加载的对象类型
     * @param ids		: 主键列表
     * @return			: 加载的对象列表，如果其中一个找不到则抛出异常
     *
     */
    <I extends Serializable, E extends EntityObject<I>> List<E> load(Class<E> objClass, List<I> ids);


    /**
     *
     * 清除缓存中的实体对象
     *
     * @param entity	: 要清除的对象
     *
     */
     <E extends EntityObject> void evict(E entity);


    /**
     *
     * 清除缓存中的实体对象
     *
     * @param eneities	: 要清除的对象列表
     *
     */
    <E extends EntityObject> void evict(List<E> eneities);


    /** 清空缓存 */
     void clear();


    /** 刷新缓存 */
    void flush();



    /**
     *
     * 执行命名查询
     *
     * @param queryName		: 查询名称
     * @param params		: 查询参数
     * @return				: 查询结果
     *
     */
    <E extends EntityObject> List<E> namedQuery(String queryName, Object... params);
    /**
     *
     * 根据起始记录和最大记录数执行命名查询
     *
     * @param firstResult	: 起始记录索引
     * @param maxResults	: 最大记录数
     * @param queryName		: 查询名称
     * @param params		: 查询参数
     * @return				: 查询结果
     *
     */
    <E extends EntityObject> List<E> namedQuery(int firstResult, int maxResults, String queryName, Object... params);


    /**
     *
     * 根据命名参数执行命名查询
     *
     * @param queryName		: 查询名称
     * @param params		: 查询参数（命名参数：参数名称－参数值）
     * @return				: 查询结果
     *
     */
    <E extends EntityObject> List<E> namedQuery(String queryName, Duad<String, Object>... params);

    /**
     *
     * 根据起始记录最大记录数和命名参数执行命名查询
     *
     * @param firstResult	: 起始记录索引
     * @param maxResults	: 最大记录数
     * @param queryName		: 查询名称
     * @param params		: 查询参数（命名参数：参数名称－参数值）
     * @return				: 查询结果
     *
     */
    <E extends EntityObject> List<E> namedQuery(int firstResult, int maxResults, String queryName, Duad<String, Object>... params);


    /**
     *
     * 执行命名更新
     *
     * @param queryName		: 查询名称
     * @param params		: 查询参数
     * @return				: 更新结果（影响的行数）
     *
     */
     int namedUpdate(String queryName, Object... params);

    /**
     *
     * 根据命名参数执行命名更新
     *
     * @param queryName		: 查询名称
     * @param params		: 查询参数（命名参数：参数名称－参数值）
     * @return				: 更新结果（影响的行数）
     *
     */
     int namedUpdate(String queryName, Duad<String, Object>... params);



    /**
     *
     * 执行SQL查询
     *
     * @param sql		: SQL 查询语句
     * @param params	: 查询参数
     * @return			: 查询结果
     *
     */
    <E extends EntityObject> List<E> sqlQuery(String sql, Object... params);


    /**
     *
     * 执行SQL查询
     *
     * @param sql		: SQL 查询语句
     * @param clazz		: 要绑定的查询实体
     * @param params	: 查询参数
     * @return			: 查询结果
     *
     */
    <E extends EntityObject> List<E> sqlQuery(String sql, Class<?> clazz, Object... params);


    /**
     *
     * 根据起始记录和最大记录数执行SQL查询
     *
     * @param firstResult	: 起始记录索引
     * @param maxResults	: 最大记录数
     * @param sql			: SQL 查询语句
     * @param params		: 查询参数
     * @return				: 查询结果
     *
     */
    <E extends EntityObject> List<E> sqlQuery(int firstResult, int maxResults, String sql, Object... params);


    /**
     *
     * 根据起始记录和最大记录数执行SQL查询
     *
     * @param firstResult	: 起始记录索引
     * @param maxResults	: 最大记录数
     * @param sql			: SQL 查询语句
     * @param clazz			: 要绑定的查询实体
     * @param params		: 查询参数
     * @return				: 查询结果
     *
     */
    <E extends EntityObject> List<E> sqlQuery(int firstResult, int maxResults, String sql, Class<?> clazz, Object... params);


    /**
     *
     * 执行SQL查询，并以指定的Scalars格式返回查询结果
     *
     * @param sql		: SQL 查询语句
     * @param scalars	: 查询结果转换标量集
     * @param params	: 查询参数
     * @return			: 查询结果
     *
     */
    <E extends EntityObject> List<E> sqlQueryByScalars(String sql, Duad<String, Type>[] scalars, Object... params);


    /**
     *
     * 根据起始记录和最大记录数执行SQL查询，并以指定的Scalars格式返回查询结果
     *
     * @param firstResult	: 起始记录索引
     * @param maxResults	: 最大记录数
     * @param sql			: SQL 查询语句
     * @param scalars		: 查询结果转换标量集
     * @param params		: 查询参数
     * @return				: 查询结果
     *
     */
    <E extends EntityObject> List<E> sqlQueryByScalars(int firstResult, int maxResults, String sql, Duad<String, Type>[] scalars, Object... params);


    /**
     *
     * 执行SQL查询，并根据Entities返回查询结果
     *
     * @param entities	: 要绑定的查询实体集合
     * @param sql		: SQL 查询语句
     * @param params	: 查询参数
     * @return			: 查询结果
     *
     */
    <E extends EntityObject> List<E> sqlQuery(String sql, Duad<String, Object>[] entities, Object... params);

    /**
     *
     * 执行SQL查询，并根据Entities返回查询结果
     *
     * @param firstResult	: 起始记录索引
     * @param maxResults	: 最大记录数
     * @param entities		: 要绑定的查询实体集合
     * @param sql			: SQL 查询语句
     * @param params		: 查询参数
     * @return				: 查询结果
     *
     */
    <E extends EntityObject> List<E> sqlQuery(int firstResult, int maxResults, String sql, Duad<String, Object>[] entities, Object... params);


    /**
     *
     * 执行SQL查询，并根据Entities和Joins返回查询结果
     *
     * @param sql		: SQL 查询语句
     * @param entities	: 要绑定的查询实体集合
     * @param joins		: 要绑定的连接实体集合
     * @param params	: 查询参数
     * @return			: 查询结果
     *
     */
    <E extends EntityObject> List<E> sqlQuery(String sql, Duad<String, Object>[] entities, Duad<String, String>[] joins, Object... params);

    /**
     *
     * 根据起始记录和最大记录数执行SQL查询，并根据Entities和Joins返回查询结果
     *
     * @param firstResult	: 起始记录索引
     * @param maxResults	: 最大记录数
     * @param sql			: SQL 查询语句
     * @param entities		: 要绑定的查询实体集合
     * @param joins			: 要绑定的连接实体集合
     * @param params		: 查询参数
     * @return				: 查询结果
     *
     */
    <E extends EntityObject> List<E> sqlQuery(int firstResult, int maxResults, String sql, Duad<String, Object>[] entities, Duad<String, String>[] joins, Object... params);


    /**
     *
     * 执行SQL更新
     *
     * @param sql		: SQL 更新语句
     * @param params	: 查询参数
     * @return			: 更新结果
     *
     */
     int sqlUpdate(String sql, Object... params);



}
