package com.twotiger.stock.db.jpa;


import com.twotiger.stock.db.DbDao;
import com.twotiger.stock.db.entity.EntityObject;
import com.twotiger.stock.db.mybatis.page.SpringDataPageAdapter;
import com.twotiger.stock.db.query.Duad;
import com.twotiger.stock.db.query.IQueryR;
import com.twotiger.stock.db.query.page.IPageR;
import com.twotiger.stock.db.query.page.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 内部通用方法定义
 * 增加通用方法 在此接口增加 在BaseRepositoryExt中实现 使用时继承BaseRepositorySupport
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2016/1/2
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
@NoRepositoryBean
public interface JpaRepositoryExt<ID extends Serializable,E extends EntityObject<ID>> extends JpaRepositoryImplementation<E,ID>,JpaSpecificationExecutor<E>,JpaRepository<E,ID> ,DbDao<ID,E> {

    default PageResult<E> query(IQueryR query, IPageR pageQuery){
        Specification<E> specification = (root, criteriaQuery,criteriaBuilder) ->{
            List<Predicate> predicateList = new ArrayList<>();
            Map<String, Duad<Comparable, Comparable>> between = query.between();
            //between
            if(between!=null) {
                between.forEach((key, duadValue) -> {
                    Path<Comparable> keyPath=root.get(key);
                    predicateList.add(criteriaBuilder.between(keyPath, duadValue.getMin(), duadValue.getMax()));
                });
            }
            //不开始于  not like 'xx%'
            Map<String, String> bn = query.bn();
            if(bn!=null){
                bn.forEach((key,value)->{
                    Path<String> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.notLike(keyPath, value + "%"));
                });
            }

            //开始于
            Map<String, String> bw = query.bw();
            if(bw!=null){
                bw.forEach((key,value)->{
                    Path<String> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.like(keyPath, value + "%"));
                });
            }

            //包含 like '%xx%'
            Map<String, String> cn = query.cn();
            if(cn!=null){
                cn.forEach((key,value)->{
                    Path<String> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.like(keyPath, "%" + value + "%"));
                });
            }
            //不结束于  not like '%xx'
            Map<String, String> en = query.en();
            if(en!=null){
                en.forEach((key,value)->{
                    Path<String> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.notLike(keyPath, "%" + value));
                });
            }
            //等于
            Map<String, Object>  eq = query.eq();
            if(eq!=null){
                eq.forEach((key,value)->{
                    Path<String> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.equal(keyPath, value));
                });
            }
            //结束于  like '%xx'
            Map<String, String>  ew = query.ew();
            if(ew!=null){
                ew.forEach((key,value)->{
                    Path<String> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.like(keyPath, "%" + value));
                });
            }
            // greater than or equal 大于等于 >=
            Map<String,Comparable > ge = query.ge();
            if(ge!=null){
                ge.forEach((key,value)->{
                    Path<Comparable> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.greaterThanOrEqualTo(keyPath, value));
                });
            }
            // greater than 大于 >
            Map<String, Comparable> gt = query.gt();
            if(gt!=null){
                gt.forEach((key,value)->{
                    Path<Comparable> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.greaterThan(keyPath, value));
                });
            }

            //in
            Map<String, Collection<Object>> in = query.in();
            if(in!=null){
                in.forEach((key,value)->{
                    Path<Object> keyPath = root.get(key);
                    Expression<?> exp = keyPath.in(value);
                    predicateList.add(criteriaBuilder.in(exp));
                });
            }
            // 为''
            List<String> isEmpty = query.isEmpty();
            if(isEmpty!=null){
                isEmpty.forEach((key)->{
                    Path<String> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.equal(keyPath, ""));
                });
            }

            List<String> isNull  = query.isNull();
            if(isNull!=null){
                isNull.forEach((key)->{
                    Path<String> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.isNull(keyPath.isNull()));
                });
            }
            // less than or equal 小于等于 <=
            Map<String, Comparable> le = query.le();
            if(le!=null){
                le.forEach((key,value)->{
                    Path<Comparable> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.lessThanOrEqualTo(keyPath, value));
                });
            }

            Map<String, String>  like = query.like();
            if(like!=null){
                like.forEach((key,value)->{
                    Path<String> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.like(keyPath, "%" + value +"%"));
                });
            }
            // less than 小于 <
            Map<String, Comparable> lt = query.lt();
            if(lt!=null){
                lt.forEach((key,value)->{
                    Path<Comparable> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.lessThan(keyPath, value));
                });
            }

            //not like '%xx%'
            Map<String, String>  nc = query.nc();
            if(nc!=null){
                nc.forEach((key,value)->{
                    Path<String> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.notLike(keyPath, "%" + value + "%"));
                });
            }

            // no equal 不等于 <>
            Map<String, Object>  ne = query.ne();
            if(ne!=null){
                ne.forEach((key,value)->{
                    Path<Object> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.notEqual(keyPath, "%" + value + "%"));
                });
            }

            //不 为''
            List<String> notEmpty = query.notEmpty();
            if(notEmpty!=null){
                notEmpty.forEach((key)->{
                    Path<String> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.notEqual(keyPath, ""));
                });
            }

            List<String> notNull = query.notNull();
            if(notNull!=null){
                notNull.forEach((key)->{
                    Path<String> keyPath = root.get(key);
                    predicateList.add(criteriaBuilder.isNotNull(keyPath.isNotNull()));
                });
            }

            Predicate[] predicates = new Predicate[predicateList.size()];
            predicates = predicateList.toArray(predicates);
            return  criteriaQuery.where(predicates).getRestriction();
        };
        //设置排序
        Sort sort = null;
        List<Duad<String,String>> orderList = pageQuery.getOrders();
        List<Sort.Order> orders = new ArrayList<>();
        orderList.forEach((doubleString) -> {
            orders.add(new Sort.Order(Sort.Direction.fromOptionalString(doubleString.getValue()).get(),doubleString.getKey()));
        });
        if(!orders.isEmpty()) {
            sort = new Sort(orders);
        }
        Page<E> page = this.findAll(specification,new PageRequest(pageQuery.getPageNum()-1,pageQuery.getPageSize(),sort));
        return new SpringDataPageAdapter(page,pageQuery);
    }

}
