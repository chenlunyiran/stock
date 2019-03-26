package com.ibeetl.admin.core.entity;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.beetl.sql.core.annotatoin.AutoID;
import org.beetl.sql.core.annotatoin.SeqID;

import com.ibeetl.admin.core.util.ValidateConfig;

/*
* 
* gen by beetlsql 2016-11-22
*/
public class CoreRoleMenu extends BaseEntity {

	private Long menuId ;
	private Long roleId ;
	private Date createTime ;
	
	public CoreRoleMenu() {
	}
	
	public Long getMenuId(){
		return  menuId;
	}
	public void setMenuId(Long menuId ){
		this.menuId = menuId;
	}
	
	public Long getRoleId(){
		return  roleId;
	}
	public void setRoleId(Long roleId ){
		this.roleId = roleId;
	}
	
	public Date getCreateTime(){
		return  createTime;
	}
	public void setCreateTime(Date createTime ){
		this.createTime = createTime;
	}
	

}
