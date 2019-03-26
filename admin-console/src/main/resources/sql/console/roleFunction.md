deleteRoleFunction
===

* 删除所有的功能的角色配置

	delete from core_role_function where function_id in ( #join(ids)# )
	
getFunctionIdByRole
===

* 获得角色对应的功能id

    select  function_id from core_role_function where role_id=#roleId#
    
getFunctionIdByRoleNoParent
===

* 获得角色对应的不包含父节点的功能id

    select  crf.function_id from core_role_function crf LEFT JOIN core_function cf ON crf.function_id=cf.id where crf.role_id=#roleId# AND crf.function_id NOT IN (select DISTINCT(cf.parent_id) from core_role_function crf LEFT JOIN core_function cf ON crf.function_id=cf.id where crf.role_id=#roleId#) 
    

getQueryFunctionAndRoleData
===

* 获得所有查询功能，并查询角色对应的功能信息。

	select  f.*,r.data_access_type from core_role_function r left join core_function f on r.function_id=f.id where r.role_id=#roleId#  and f.type='FN1'