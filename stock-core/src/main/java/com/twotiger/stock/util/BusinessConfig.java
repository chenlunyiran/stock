package com.twotiger.stock.util;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "upload.file")
@PropertySource("business.properties")
public class BusinessConfig {
    private String rootPath;
    private String goodsImgPath;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getGoodsImgPath() {
        return goodsImgPath;
    }

    public void setGoodsImgPath(String goodsImgPath) {
        this.goodsImgPath = goodsImgPath;
    }


    public static class Oss{
        private String privateUrl;

        public String getPrivateUrl() {
            return privateUrl;
        }

        public void setPrivateUrl(String privateUrl) {
            this.privateUrl = privateUrl;
        }
    }
}
