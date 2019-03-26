package com.twotiger.stock.customer.common;

import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;

/**
 * Created by alean on 2018/11/21.
 */
public class MyFilterType implements TypeFilter {
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {

        //获取当前正在扫描类信息
        ClassMetadata classMetadata = metadataReader.getClassMetadata();
        //获取当前类路径
        String className = classMetadata.getClassName();
        String str1 = "com.ibeetl.admin.console.web";
        String str2 = "com.ibeetl.admin.core.web";
        if(className.startsWith(str1) || className.startsWith(str2)){
            return true;
        }
        return false;

    }
}
