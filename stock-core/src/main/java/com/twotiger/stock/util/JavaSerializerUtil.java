package com.twotiger.stock.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * jdk 默认序列化工具
 * Created with IntelliJ IDEA.
 * User: liuqing
 * Date: 2015/10/18
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
public final class JavaSerializerUtil {
    private JavaSerializerUtil(){}

    /**
     * 序列化对象
     * @param obj
     * @return
     */
    public static  byte[] serializer(Object obj) {
        try {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(obj);
                return byteArrayOutputStream.toByteArray();
            }
        }catch (Exception e){
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    /**
     * 反序列化对象
     * @param data
     * @param <T>
     * @return
     */
    public static <T> T deserializer(byte[] data){
        if(data==null){
            return null;
        }
        try {
            return (T)new ObjectInputStream(new ByteArrayInputStream(data)).readObject();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(),e);
        }
    }
}
