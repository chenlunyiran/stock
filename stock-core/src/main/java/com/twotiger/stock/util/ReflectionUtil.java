/*******************************************************************************
 * Copyright (c) 2005, 2014 springside.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *******************************************************************************/
package com.twotiger.stock.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;

/**
 * 反射工具类.
 * 
 * 所有反射均无视modifier的范围限制，同时将反射的Checked异常转为UnChecked异常。
 * 
 * 1. 对于构造函数，直接使用本类即可
 * 
 * 2. 对于方法调用，本类全部是一次性调用的简化方法，如果考虑性能，对反复调用的方法应使用 MethodInvoker 及 FastMethodInvoker.
 * 
 * 3. 对于直接属性访问，恰当使用本类中的一次性调用，和基于预先获取的Field对象反复调用两种做法.
 */
@SuppressWarnings("unchecked")
public class ReflectionUtil {


	private static final String CGLIB_CLASS_SEPARATOR = "$$";

	private static Logger logger = LoggerFactory.getLogger(ReflectionUtil.class);

	/////////// 属性相关函数 ///////////
	/**
	 * 调用Getter方法, 无视private/protected修饰符.
	 */
	public static <T> T invokeGetter(Object obj, String propertyName) {
		Method method = ClassUtil.getGetterMethod(obj.getClass(), propertyName);
		if (method == null) {
			throw new IllegalArgumentException(
					"Could not find getter method [" + propertyName + "] on target [" + obj + ']');
		}
		return (T) invokeMethod(obj, method);
	}

	/**
	 * 调用Setter方法, 无视private/protected修饰符, 按传入value的类型匹配函数.
	 */
	public static void invokeSetter(Object obj, String propertyName, Object value) {
		Method method = ClassUtil.getSetterMethod(obj.getClass(), propertyName, value.getClass());
		if (method == null) {
			throw new IllegalArgumentException(
					"Could not find getter method [" + propertyName + "] on target [" + obj + ']');
		}
		invokeMethod(obj, method, value);
	}

	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 */
	public static <T> T getFieldValue(final Object obj, final String fieldName) {
		Field field = ClassUtil.getAccessibleField(obj.getClass(), fieldName);
		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + ']');
		}
		return getFieldValue(obj, field);
	}

	/**
	 * 使用已获取的Field, 直接读取对象属性值, 不经过getter函数.
	 */
	public static <T> T getFieldValue(final Object obj, final Field field) {
		try {
			return (T) field.get(obj);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
	 */
	public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
		Field field = ClassUtil.getAccessibleField(obj.getClass(), fieldName);
		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + ']');
		}
		setField(obj, field, value);
	}

	/**
	 * 使用已获取的Field, 直接读取对象属性值, 不经过setter函数.
	 */
	public static void setField(final Object obj, Field field, final Object value) {
		try {
			field.set(obj, value);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 先尝试用Getter函数读取, 如果不存在则直接读取变量.
	 */
	public static <T> T getProperty(Object obj, String propertyName) {
		Method method = ClassUtil.getGetterMethod(obj.getClass(), propertyName);
		if (method != null) {
			try {
				return (T) method.invoke(obj, ArrayUtils.EMPTY_OBJECT_ARRAY);
			} catch (Exception e) {
				throw convertReflectionExceptionToUnchecked(e);
			}
		} else {
			return (T) getFieldValue(obj, propertyName);
		}
	}

	/**
	 * 先尝试用Setter函数写入, 如果不存在则直接写入变量, 按传入value的类型匹配函数.
	 */
	public static void setProperty(Object obj, String propertyName, final Object value) {
		Method method = ClassUtil.getSetterMethod(obj.getClass(), propertyName, value.getClass());
		if (method != null) {
			try {
				method.invoke(obj, value);
			} catch (Exception e) {
				throw convertReflectionExceptionToUnchecked(e);
			}
		} else {
			setFieldValue(obj, propertyName, value);
		}
	}

	/////////// 方法相关函数 ////////////
	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 * 
	 * 根据传入参数的实际类型进行匹配
	 */
	public static <T> T invokeMethod(Object obj, String methodName, Object... args) {
		Object[] theArgs = ArrayUtils.nullToEmpty(args);
		final Class<?>[] parameterTypes = ClassUtils.toClass(theArgs);
		return (T) invokeMethod(obj, methodName, theArgs, parameterTypes);
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 * 
	 * 根据定义的参数类型进行匹配
	 */
	public static <T> T invokeMethod(final Object obj, final String methodName, final Object[] args,
			final Class<?>[] parameterTypes) {
		Method method = ClassUtil.getAccessibleMethod(obj.getClass(), methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + ']');
		}
		return invokeMethod(obj, method, args);
	}


	/**
	 * 调用已准备好的Method
	 */
	public static <T> T invokeMethod(final Object obj, Method method, Object... args) {
		try {
			return (T) method.invoke(obj, args);
		} catch (Exception e) {
			throw ExceptionUtil.uncheckedAndWrap(e);
		}
	}

	////////// 构造函数 ////////
	/**
	 * 调用构造函数.
	 */
	public static <T> T invokeConstructor(final Class<T> cls, Object... args) {
		try {
			return ConstructorUtils.invokeConstructor(cls, args);
		} catch (Exception e) {
			throw ExceptionUtil.uncheckedAndWrap(e);
		}
	}

	/////// 辅助函数 ////////

	/**
	 * 将反射时的checked exception转换为unchecked exception.
	 */
	public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
		if ((e instanceof IllegalAccessException) || (e instanceof NoSuchMethodException)) {
			return new IllegalArgumentException(e);
		} else if (e instanceof InvocationTargetException) {
			return new RuntimeException(((InvocationTargetException) e).getTargetException());
		} else if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new RuntimeException(e);
	}


	//Reflect

	public static class Generic{
		/**
		 * 获取指定类的泛型参数类型
		 * @param clazz  指定的类
		 * @param index  第几个泛型参数  从0 开始
		 * @return
		 */
		public static Class getGenericSupertype(Class clazz, int index) {
			return getComponentType(clazz.getGenericSuperclass(), index);
		}
	}

	/**
	 *
	 * @param type
	 * @param index
	 * @return
	 */
	public static Class getComponentType(Type type, int index) {
		if (type instanceof Class) {
			Class clazz = (Class) type;
			if (clazz.isArray()) {
				return clazz.getComponentType();
			}
		} else if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			Type[] generics = pt.getActualTypeArguments();
			if (index < 0) {
				index = generics.length + index;
			}
			if (index < generics.length) {
				return toClass(generics[index]);
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType gat = (GenericArrayType) type;
			return toClass(gat.getGenericComponentType());
		}
		return null;
	}
	public static Class toClass(Type type) {
		if (type instanceof Class) {
			return (Class) type;
		} else if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			return toClass(pt.getRawType());
		} else if (type instanceof WildcardType) {
			WildcardType wt = (WildcardType) type;
			Type[] lower = wt.getLowerBounds();
			if (lower.length == 1) {
				return toClass(lower[0]);
			}
			Type[] upper = wt.getUpperBounds();
			if (upper.length == 1) {
				return toClass(upper[0]);
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType gat = (GenericArrayType) type;
			Class componentType = toClass(gat.getGenericComponentType());
			// this is sort of stupid but there seems no other way...
			return Array.newInstance(componentType, 0).getClass();
		} else if (type instanceof TypeVariable) {
			TypeVariable tv = (TypeVariable) type;
			Type[] bounds = tv.getBounds();
			if (bounds.length == 1) {
				return toClass(bounds[0]);
			}
		}
		return null;
	}

	/**
	 * 判断两个类或代理的类是否相同
	 * @param clazz1
	 * @param clazz2
	 * @return
	 */
	public static boolean isSameClass(Class clazz1,Class clazz2){
		if(clazz1.equals(clazz2)){
			return true;
		}
		if(Proxy.isProxyClass(clazz1)){
			Class inClazz = clazz1.getInterfaces()[0];
			if(inClazz.equals(clazz2)){
				return true;
			}
		}
		if(Proxy.isProxyClass(clazz2)){
			Class inClazz = clazz2.getInterfaces()[0];
			if(inClazz.equals(clazz1)){
				return true;
			}
		}
		if(Proxy.isProxyClass(clazz1)&&Proxy.isProxyClass(clazz2)){
			Class inClazz1 = clazz1.getInterfaces()[0];
			Class inClazz2 = clazz2.getInterfaces()[0];
			if(inClazz1.equals(inClazz2)){
				return true;
			}
		}
		return false;
	}

	/**
	 * 取得类或代理类的全名
	 * @param clazz
	 * @return
	 */
	public static String getFullClassName(Class clazz){
		if(Proxy.isProxyClass(clazz)){
			Class inClazz = clazz.getInterfaces()[0];
			return inClazz.getName();
		}
		return clazz.getName();
	}

	/**
	 * 取得类或代理类的全名
	 * @param clazz
	 * @return
	 */
	public static Class getRealClass(Class clazz){
		if(Proxy.isProxyClass(clazz)){
			return clazz.getInterfaces()[0];
		}
		return clazz;
	}





	/**
	 * 直接调用对象方法, 无视private/protected修饰符.
	 * 用于一次性调用的情况，否则应使用getAccessibleMethod()函数获得Method后反复调用.
	 * 同时匹配方法名+参数类型，
	 */
	public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes,
									  final Object[] args) {
		Method method = getAccessibleMethod(obj, methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
		}

		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 直接调用对象方法, 无视private/protected修饰符，
	 * 用于一次性调用的情况，否则应使用getAccessibleMethodByName()函数获得Method后反复调用.
	 * 只匹配函数名，如果有多个同名函数调用第一个。
	 */
	public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
		Method method = getAccessibleMethodByName(obj, methodName);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
		}

		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
	 *
	 * 如向上转型到Object仍无法找到, 返回null.
	 */
	public static Field getAccessibleField(final Object obj, final String fieldName) {
		Validate.notNull(obj, "object can't be null");
		Validate.notBlank(fieldName, "fieldName can't be blank");
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				Field field = superClass.getDeclaredField(fieldName);
				makeAccessible(field);
				return field;
			} catch (NoSuchFieldException e) {//NOSONAR
				// Field不在当前类定义,继续向上转型
				continue;// new add
			}
		}
		return null;
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
	 * 如向上转型到Object仍无法找到, 返回null.
	 * 匹配函数名+参数类型。
	 *
	 * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
	 */
	public static Method getAccessibleMethod(final Object obj, final String methodName,
											 final Class<?>... parameterTypes) {
		Validate.notNull(obj, "object can't be null");
		Validate.notBlank(methodName, "methodName can't be blank");

		for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
			try {
				Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
				makeAccessible(method);
				return method;
			} catch (NoSuchMethodException e) {
				// Method不在当前类定义,继续向上转型
				continue;// new add
			}
		}
		return null;
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
	 * 如向上转型到Object仍无法找到, 返回null.
	 * 只匹配函数名。
	 *
	 * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
	 */
	public static Method getAccessibleMethodByName(final Object obj, final String methodName) {
		Validate.notNull(obj, "object can't be null");
		Validate.notBlank(methodName, "methodName can't be blank");

		for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
			Method[] methods = searchType.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					makeAccessible(method);
					return method;
				}
			}
		}
		return null;
	}

	/**
	 * 改变private/protected的方法为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
	 */
	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}

	/**
	 * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
	 */
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier
				.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/**
	 * 通过反射, 获得Class定义中声明的泛型参数的类型, 注意泛型必须定义在父类处
	 * 如无法找到, 返回Object.class.
	 * eg.
	 * public UserDao extends HibernateDao<User>
	 *
	 * @param clazz The class to introspect
	 * @return the first generic declaration, or Object.class if cannot be determined
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClassGenricType(final Class clazz) {
		return getClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射, 获得Class定义中声明的父类的泛型参数的类型.
	 * 如无法找到, 返回Object.class.
	 *
	 * 如public UserDao extends HibernateDao<User,Long>
	 *
	 * @param clazz clazz The class to introspect
	 * @param index the Index of the generic ddeclaration,start from 0.
	 * @return the index generic declaration, or Object.class if cannot be determined
	 */
	public static Class getClassGenricType(final Class clazz, final int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
					+ params.length);
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
			return Object.class;
		}

		return (Class) params[index];
	}

	public static Class<?> getUserClass(Object instance) {
		Class clazz = instance.getClass();
		if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR)) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null && !Object.class.equals(superClass)) {
				return superClass;
			}
		}
		return clazz;

	}

}
