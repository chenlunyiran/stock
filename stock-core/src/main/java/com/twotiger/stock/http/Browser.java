package com.twotiger.stock.http;

import java.io.Closeable;
import java.net.URL;

/**
 * Created by liuqing on 2014/12/24.
 */
public interface Browser extends Closeable {

    /**
     * 打开地址
     * @param url 网页地址
     * @return
     */
    ClientDomain open(URL url);


}
