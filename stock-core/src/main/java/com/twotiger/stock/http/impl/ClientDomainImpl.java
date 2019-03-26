package com.twotiger.stock.http.impl;


import com.twotiger.stock.http.*;
import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.eclipse.jetty.util.HttpCookieStore;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import javax.net.ssl.SSLContext;
import java.net.HttpCookie;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

/**
 * Created by liuqing on 2014/12/24.
 */
@ThreadSafe
class ClientDomainImpl  implements ClientDomain,WebSocketConnect {
    private  static  final Logger LOGGER = LoggerFactory.getLogger(ClientDomainImpl.class);

    private final Collection<Header> headers;

    private volatile SSLContext sslContext;

    private final CookieStore cookieStore = new CookieStore(){
        private final List<Cookie> cookies = new CopyOnWriteArrayList();
        @Override
        public void addCookie(Cookie cookie) {
            LOGGER.debug("增加cookies : {}",cookie.toString());
            cookies.add(cookie);
        }

        @Override
        public List<Cookie> getCookies() {
            return Collections.unmodifiableList(cookies);
        }

        @Override
        public boolean clearExpired(Date date) {
            if(date == null) {
                return false;
            } else {
                boolean removed = false;
                Iterator it = this.cookies.iterator();

                while(it.hasNext()) {
                    if(((Cookie)it.next()).isExpired(date)) {
                        it.remove();
                        removed = true;
                    }
                }

                return removed;
            }
        }

        @Override
        public void clear() {
            cookies.clear();
        }
    };

    private final CloseableHttpClient httpclient;

    private final URI host_uri;

    ClientDomainImpl(URI uri,String agent,Map<String,String> headerMap) throws KeyManagementException, NoSuchAlgorithmException {
        this.host_uri =  uri;
        List<Header> headerList = new LinkedList<Header>();
          for(Map.Entry<String,String> entry:headerMap.entrySet()){
              headerList.add(new BasicHeader(entry.getKey(),entry.getValue()));
          }
        headerList.add(new BasicHeader("Host",uri.getHost()));
        this.headers = new CopyOnWriteArrayList(headerList);

        if(uri.getScheme().equals("https")) {
            this.sslContext = SSLContexts.custom().useTLS().build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(this.sslContext);
            Registry registry = RegistryBuilder.create().register("https", sslsf).build();
            HttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
            this.httpclient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .setDefaultHeaders(headers)
                    .setUserAgent(agent)
                    .setConnectionManager(cm)
                    .build();
        }else{
            this.httpclient = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .setDefaultHeaders(headers)
                    .setUserAgent(agent)
                    .build();
        }
    }

    void close(){
       HttpClientUtils.closeQuietly(httpclient);
    }



    public <I , O > O doAction(PostAction<I, O> action, I in){
        CloseableHttpResponse httpResponse = null;
        URIBuilder uriBuilder = new URIBuilder(host_uri);
        String path = action.getPath();
        if(path!=null) {
            uriBuilder.setPath(path);
        }
        try {
            URI uri = uriBuilder.build();
            LOGGER.debug("POST ACTION URI = {}",uri);
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(action.beforeRequest(new DomainContentImpl(httpPost),in));
            httpResponse = this.httpclient.execute(httpPost);
            return  action.afterResponse(httpResponse);
        }catch (Exception e){
            LOGGER.error("http post 请求失败!",e);
        }finally {
            HttpClientUtils.closeQuietly(httpResponse);
        }
        return null;
    }

    public <O> O doAction(GetAction<O> action){
        CloseableHttpResponse httpResponse = null;
        URIBuilder uriBuilder = new URIBuilder(host_uri);
        String path = action.getPath();
        String query = action.getQuery();
        if(path!=null) {
            uriBuilder.setPath(path);
        }
        if(query!=null) {
            uriBuilder.setCustomQuery(query);
        }
        try {
            URI uri = uriBuilder.build();
            LOGGER.debug("GET ACTION URI = {}",uri);
            HttpGet httpGet = new HttpGet(uri);
            action.beforeRequest(new DomainContentImpl(httpGet));
            httpResponse = this.httpclient.execute(httpGet);
            return action.afterResponse(httpResponse);
        }catch (Exception e){
            LOGGER.error("http get 请求失败!",e);
            throw new RuntimeException(e);
        }finally {
            HttpClientUtils.closeQuietly(httpResponse);
        }
    }

    @Override
    public void addCookie(String key, String value) {
        this.cookieStore.addCookie(new BasicClientCookie(key,value));
    }

    @Override
    public String getCookie(String key) {
        for(Cookie cookie:cookieStore.getCookies()){
            if(cookie.getName().equals(key)){
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public Future<Session> connect(Object socketHander, String path) {
        WebSocketClient client;
        URIBuilder uriBuilder = new URIBuilder(host_uri);
        uriBuilder.setPath(path);
        if(uriBuilder.getScheme().equalsIgnoreCase("https")) {
            uriBuilder.setScheme("wss");
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setSslContext(this.sslContext);
            client = new WebSocketClient(sslContextFactory);
        }else{
            uriBuilder.setScheme("ws");
            client = new WebSocketClient();
        }
        URI wsUri;
        try {
            wsUri = uriBuilder.build();
            LOGGER.debug("connect ws uri = {} ",wsUri);
            HttpCookieStore httpCookieStore = new HttpCookieStore();
            List<Cookie>  cookieList = cookieStore.getCookies();
            for(Cookie cookie:cookieList) {
                httpCookieStore.add(host_uri,new HttpCookie(cookie.getName(),cookie.getValue()));
            }
            client.setCookieStore(httpCookieStore);
            client.start();
            return  client.connect(socketHander,wsUri);
        } catch (Exception e) {
            LOGGER.error("WS连接错误！",e);
        }
        return null;
    }

    class DomainContentImpl implements DomainContent {
        private final HttpRequestBase httpRequest;
        public DomainContentImpl(HttpRequestBase httpRequest){
            this.httpRequest=httpRequest;
        }

        @Override
        public void addRequestHeader( String key, String value) {
            httpRequest.setHeader(key,value);
        }
    }
}
