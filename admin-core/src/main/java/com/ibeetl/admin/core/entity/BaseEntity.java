package com.ibeetl.admin.core.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.ibeetl.admin.core.util.ValidateConfig;
import com.twotiger.stock.db.entity.EntityObject;
import org.beetl.sql.core.TailBean;
import org.beetl.sql.core.annotatoin.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;

/**
 * 描述:用于辅助序列化beetlsql 的TailBean
 * @author : xiandafu
 */
@MappedSuperclass
public class BaseEntity extends TailBean implements EntityObject<Long> {

	protected final static String ORACLE_CORE_SEQ_NAME="core_seq";
	protected final static String ORACLE_AUDIT_SEQ_NAME="audit_seq";
	protected final static String ORACLE_FILE_SEQ_NAME="core_seq";
	@JsonAnyGetter
	@javax.persistence.Transient
    public Map<String, Object> getTails(){
    	return super.getTails();
    }
    
	@NotNull(message = "ID不能为空", groups =ValidateConfig.UPDATE.class)
	@AutoID
	@Id
	@javax.persistence.Id
	@GenericGenerator(name = "idGenerator", strategy = "identity")//jpa
	@GeneratedValue(strategy = GenerationType.IDENTITY,generator = "idGenerator")//mybatis
	protected Long id;// 主键ID.
	@InsertIgnore
	@UpdateIgnore
	@Column(name = "create_time",insertable = false,updatable = false)
	private Date createTime;// 创建时间
	@InsertIgnore
	@UpdateIgnore
	@Column(name = "update_time",insertable = false,updatable = false)
	private Date updateTime;// 修改时间
	@Column(name = "remark")
	private String remark;// 备注

	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	@ColumnIgnore(insert=true,update=true)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@ColumnIgnore(insert=true,update=true)
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
