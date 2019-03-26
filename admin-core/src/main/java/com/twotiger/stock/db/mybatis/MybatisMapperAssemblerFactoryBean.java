package com.twotiger.stock.db.mybatis;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * 用于创建合成mapper的bean工厂
 * Created by liuqing-notebook on 2016/12/21.
 */
public class MybatisMapperAssemblerFactoryBean implements FactoryBean {
    private static final String GENERATED_METHOD_PREFIX = "g_";
    //mybatis 自定义实现接口的后缀
    private  String customSuffix;
    //mybatis 生成实现接口的后缀
    private  String generatedSuffix;
    private  Class  mapperInterface;
    private Class customMapperClass = null;
    private Class generatedMapperClass = null;
//    private final Set<Method> customMapperMethods = new HashSet<>();
    private final Set<Method> generatedMapperMethods = new HashSet<>();

    private Object customMapper;
    private Object generatedMapper;

    public void init(){
        String mapperInterfaceName = mapperInterface.getSimpleName();
        Class[] extentInterfaces = mapperInterface.getInterfaces();
        for(Class clazz :extentInterfaces){
            String sName = clazz.getSimpleName();
            if(sName.equals(mapperInterfaceName+customSuffix)){
                customMapperClass = clazz;
            }else if(sName.equals(mapperInterfaceName+generatedSuffix)){
                generatedMapperClass = clazz;
            }
        }
        if(customMapperClass==null||generatedMapperClass==null){
            throw new RuntimeException("no custom generated MapperClass");
        }

        for(Method m:generatedMapperClass.getDeclaredMethods()){//generatedMapperClass 无继承
            generatedMapperMethods.add(m);
        }

        for(Method m:customMapperClass.getMethods()){
            if(generatedMapperMethods.contains(m)){//TODO 排重有问题
                throw new RuntimeException("method 重复！"+m.toGenericString());
            }
        }
    }

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{mapperInterface},
                new SelectInvocationHandler(customMapper,generatedMapper));
    }

    @Override
    public Class<?> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private class SelectInvocationHandler implements InvocationHandler{

        public SelectInvocationHandler(Object customMapperTarget, Object generatedMapperTarget) {
            this.customMapperTarget = customMapperTarget;
            this.generatedMapperTarget = generatedMapperTarget;
        }

        private final Object customMapperTarget;

        private final Object generatedMapperTarget;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = null;
                //if(generatedMapperMethods.contains(method)){//generatedMapperClass 无继承
                if (method.getName().startsWith(GENERATED_METHOD_PREFIX)) {//generatedMapperClass 无继承
                    result = method.invoke(generatedMapperTarget, args);
                } else {
                    result = method.invoke(customMapperTarget, args);
                }
            return result;
        }
    }


    public String getCustomSuffix() {
        return customSuffix;
    }

    public void setCustomSuffix(String customSuffix) {
        this.customSuffix = customSuffix;
    }

    public String getGeneratedSuffix() {
        return generatedSuffix;
    }

    public void setGeneratedSuffix(String generatedSuffix) {
        this.generatedSuffix = generatedSuffix;
    }

    public Class getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperInterface(Class mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public void setCustomMapper(Object customMapper) {
        this.customMapper = customMapper;
    }

    public void setGeneratedMapper(Object generatedMapper) {
        this.generatedMapper = generatedMapper;
    }
}
