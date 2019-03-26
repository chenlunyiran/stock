package com.twotiger.stock.db.mybatis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import static org.springframework.util.Assert.notNull;

/**
 * mybatis mybatis 组合器
 * Created by liuqing on 2016/12/20.
 */
public class MybatisMapperAssemblerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean, BeanNameAware {
    private static  final String DEFAULT_CUSTOM_SUFFIX = "Custom";
    private static  final String DEFAULT_GENERATED_SUFFIX = "Generated";
    //static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";//包含子包
    private static final String DEFAULT_RESOURCE_PATTERN = "*.class";
    private static final String DEFAULT_MAPPER_SUFFIX = "Mapper";

    //mybatis 自定义实现接口的后缀
    private final String customSuffix;
    //mybatis 生成实现接口的后缀
    private final String generatedSuffix;

    private String assemblerName;

    private String basePackage;

    private BeanNameGenerator nameGenerator;

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

    private Environment environment = new StandardEnvironment();

    public MybatisMapperAssemblerConfigurer() {
        this(DEFAULT_GENERATED_SUFFIX,DEFAULT_CUSTOM_SUFFIX);
    }

    public MybatisMapperAssemblerConfigurer(String generatedSuffix, String customSuffix) {
        this.generatedSuffix = generatedSuffix;
        this.customSuffix = customSuffix;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        notNull(this.basePackage, "Property 'basePackage' is required");
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        scan(registry,StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    private void scan(BeanDefinitionRegistry registry, String... basePackages){
        try {
            for (String basePackage:basePackages) {
                String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                        resolveBasePackage(basePackage) + "/" + DEFAULT_RESOURCE_PATTERN;
                Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);
                for (Resource resource:resources) {
                    if (resource.isReadable()) {
                        MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                        String mapperClassName = metadataReader.getClassMetadata().getClassName();
                        if(mapperClassName.endsWith(DEFAULT_MAPPER_SUFFIX)){
                            Class mapperInterface = Class.forName(mapperClassName);
                            registerBeanDefinition(registry,mapperInterface);
                        }
                    }
                }
            }
        }catch (Exception e){
            throw new RuntimeException("scan mybatis interface error!",e);
        }
    }

    private void registerBeanDefinition(BeanDefinitionRegistry registry, Class mapperInterface){
        final String mapperBeanName = StringUtils.uncapitalize(mapperInterface.getSimpleName());
        final String userMapperCustomBeanName = mapperBeanName+this.customSuffix;
        final String userMapperGeneratedBeanName = mapperBeanName+this.generatedSuffix;
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MybatisMapperAssemblerFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("customSuffix",this.customSuffix);
        beanDefinitionBuilder.addPropertyValue("generatedSuffix",this.generatedSuffix);
        beanDefinitionBuilder.addPropertyValue("mapperInterface",mapperInterface);
        beanDefinitionBuilder.addPropertyReference("customMapper",userMapperCustomBeanName);
        beanDefinitionBuilder.addPropertyReference("generatedMapper",userMapperGeneratedBeanName);
        beanDefinitionBuilder.setInitMethodName("init");
        beanDefinitionBuilder.addDependsOn(userMapperCustomBeanName);
        beanDefinitionBuilder.addDependsOn(userMapperGeneratedBeanName);
        registry.registerBeanDefinition(mapperBeanName,beanDefinitionBuilder.getBeanDefinition());
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(this.environment.resolveRequiredPlaceholders(basePackage));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setBeanName(String name) {
        this.assemblerName = name;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public BeanNameGenerator getNameGenerator() {
        return nameGenerator;
    }

    public void setNameGenerator(BeanNameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }
}
