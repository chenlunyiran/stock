package com.twotiger.stock.http.impl;

import com.twotiger.stock.http.Browser;
import com.twotiger.stock.http.ClientDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Created by liuqing on 2014/12/24.
 */
public class FirefoxBrowser implements Browser {
    private  static  final Logger LOGGER = LoggerFactory.getLogger(FirefoxBrowser.class);

    private static Map<String,String> HEADER = new HashMap<String, String>();

    private final Set<ClientDomainImpl> clientDomainSet = Collections.synchronizedSet(new HashSet<ClientDomainImpl>());

    static{
        HEADER.put("Accept","*/*");
        HEADER.put("Accept-Encoding","gzip,deflate");
        HEADER.put("Accept-Language","zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        HEADER.put("Cache-Control","max-age=0");
        HEADER.put("Connection","keep-alive");
    }

    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0";

    @Override
    public ClientDomain open(URL url) {
        ClientDomainImpl cdm;
        try {
             cdm = new ClientDomainImpl(url.toURI(), USER_AGENT, HEADER);
             clientDomainSet.add(cdm);
             return cdm;
        }catch (Exception e){
            LOGGER.error("初始化错误!",e);
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        synchronized (clientDomainSet) {
            for (ClientDomainImpl cdm : clientDomainSet) {
                cdm.close();
            }
            clientDomainSet.clear();
        }
    }
}
