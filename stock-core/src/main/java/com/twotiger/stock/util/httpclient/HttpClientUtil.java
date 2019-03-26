package com.twotiger.stock.util.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {

//    private static final Logger LOG = Logger.getLogger(HttpClientUtil.class);
    public final static PoolingHttpClientConnectionManager cm;
    public final static HttpRequestRetryHandler retryHandler = new TTHttpRequestRetryHandler(3,
        true);
    static {
        cm = new PoolingHttpClientConnectionManager();
        // Increase max total connection to 200
        cm.setMaxTotal(500);
        // Increase default max connection per route to 20
        cm.setDefaultMaxPerRoute(50);
    }

    public static RequestConfig buildConfig(int connectTimeOut, int readTimeout) {
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(connectTimeOut);
        // 设置读取超时
        configBuilder.setSocketTimeout(readTimeout);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(500);
        // 在提交请求之前 测试连接是否可用
        configBuilder.setStaleConnectionCheckEnabled(true);
        return configBuilder.build();
    }

    /**
     * 获取一个httpclient
     * @return
     */
    public static CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * 获取一个httpclient
     * @return
     */
    public static CloseableHttpClient getHttpClient(HttpRequestRetryHandler retryHandler) {
        return HttpClients.custom().setRetryHandler(retryHandler).setConnectionManager(cm).build();
    }

    /**
     * 
     * MAP类型数组转换成NameValuePair类型
     * @param params
     * @return
     */
    public static List<NameValuePair> buildNameValuePair(Map<String, String> params) {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        return nvps;
    }

    public static String doPost(String body, String url) throws ClientProtocolException,
            IOException {
        return doPost(body, url, null);
    }

    public static String doPost(Map<String, String> params, String url)
                                                                       throws ClientProtocolException,
            IOException {
        return doPost(params, url, null);
    }

    public static String doPost(Map<String, String> params, String url, RequestConfig requestConfig)
                                                                                                    throws ClientProtocolException,
            IOException {
        return doPost(params, url, requestConfig, null);
    }

    /**
     * post 提交数据
     * @param params
     * @param url
     * @return String 
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String doPost(Map<String, String> params, String url,
                                RequestConfig requestConfig, HttpRequestRetryHandler retryHandler)
                                                                                                  throws ClientProtocolException,
            IOException {
        String result = null;
        List<NameValuePair> nvps = buildNameValuePair(params);
        //Pooling connection manager
        CloseableHttpClient httpClient = HttpClients.custom().setRetryHandler(retryHandler)
            .setConnectionManager(cm).build();
        HttpPost httpPost = new HttpPost(url);

        if (null != requestConfig) {
            httpPost.setConfig(requestConfig);
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getReasonPhrase().equals("OK")
                && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
            EntityUtils.consume(entity);
        } finally {
            if (response != null)
                response.close();
        }
        return result;
    }

    public static String doPost(String body, String url, RequestConfig requestConfig)
                                                                                     throws ClientProtocolException,
            IOException {
        return doPost(body, url, requestConfig, null);
    }

    /**
     * post 提交数据
     * @param url
     * @return String 
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String doPost(String body, String url, RequestConfig requestConfig,
                                HttpRequestRetryHandler retryHandler)
                                                                     throws ClientProtocolException,
            IOException {
        String result = null;
        //Pooling connection manager
        CloseableHttpClient httpClient = HttpClients.custom().setRetryHandler(retryHandler)
            .setConnectionManager(cm).build();
        HttpPost httpPost = new HttpPost(url);
        if (requestConfig != null) {
            httpPost.setConfig(requestConfig);
        }
        httpPost.setEntity(new StringEntity(body, "utf-8"));
        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getReasonPhrase().equals("OK")
                && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
            EntityUtils.consume(entity);
        } finally {
            if (response != null)
                response.close();
        }
        return result;
    }

    public static String doPostJson(String body, String url, RequestConfig requestConfig)
                                                                                         throws ClientProtocolException,
            IOException {
        return doPostJson(body, url, requestConfig, null);
    }



    /**
    * post 提交数据
    * @param url
    * @return String 
    * @throws ClientProtocolException
    * @throws IOException
    */
    public static String doPostJson(String body, String url, RequestConfig requestConfig,
                                    HttpRequestRetryHandler retryHandler)
                                                                         throws ClientProtocolException,
            IOException {
        String result = null;
        //Pooling connection manager
        CloseableHttpClient httpClient = HttpClients.custom().setRetryHandler(retryHandler)
            .setConnectionManager(cm).build();
        HttpPost httpPost = new HttpPost(url);
        if (requestConfig != null) {
            httpPost.setConfig(requestConfig);
        }

        StringEntity se = new StringEntity(body, "UTF-8");
        se.setContentType("application/json");
        se.setContentEncoding("UTF-8");

        httpPost.setEntity(se);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        try {
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getReasonPhrase().equals("OK")
                && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(entity, "UTF-8");
                if(result==null){
//                    LOG.error("doPostJson url=[" + url + "];body=["+body+"]");
                }
            }            EntityUtils.consume(entity);
        } finally {
            if (response != null)
                response.close();
        }
        return result;
    }
    public static String reTry(String url , String body, CloseableHttpClient httpClient , HttpPost httpPost, int count) throws IOException {
        String result = null;
        if(count>4){
            return result;
        }
        count++;
        CloseableHttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        if(response.getStatusLine().getStatusCode()==HttpStatus.SC_SERVICE_UNAVAILABLE) {
            result =  EntityUtils.toString(entity, "UTF-8");
//            LOG.info("doPostJson url=[" + url + "];body=["+body+"];res=["+result+"];response=[" + response.getStatusLine()+"];503错误,进行重试次数:"+count);
            EntityUtils.consume(entity);
            reTry(url,body,httpClient,httpPost,count);

        }else{
            result =  EntityUtils.toString(entity, "UTF-8");
            EntityUtils.consume(entity);
        }

        return result;
    }


    public static String doGet(String url) throws ClientProtocolException, IOException {
        return doGet(url, null);
    }

    public static String doGet(Map<String, String> params, String url)
                                                                      throws ClientProtocolException,
            IOException {
        return doGet(params, url, null);
    }

    /**
     * 
     * @param params
     * @param url
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String doGet(Map<String, String> params, String url, RequestConfig requestConfig)
                                                                                                   throws ClientProtocolException,
            IOException {
        List<NameValuePair> nvps = buildNameValuePair(params);
        String query = URLEncodedUtils.format(nvps, "UTF-8");
        if (url.indexOf("?") == -1) {
            url += "?" + query;
        } else {
            url += "&" + query;
        }
        return doGet(url, requestConfig);
    }

    public static String doGet(String url, RequestConfig requestConfig)
                                                                       throws ClientProtocolException,
            IOException {
        return doGet(url, requestConfig, null);
    }

    /**
     * get 提交数据
     * 
     * @param url
     * @return String
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String doGet(String url, RequestConfig requestConfig,
                               HttpRequestRetryHandler retryHandler)
                                                                    throws ClientProtocolException,
            IOException {
        String result = null;
        //Pooling connection manager
        CloseableHttpClient httpClient = HttpClients.custom().setRetryHandler(retryHandler)
            .setConnectionManager(cm).build();
        HttpGet httpGet = new HttpGet(url);
        if (requestConfig != null) {
            httpGet.setConfig(requestConfig);
        }
        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getReasonPhrase().equals("OK")
                && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(entity, "UTF-8");
                if(result==null){
//                    LOG.error("doGet url=" + url );
                }
            }else{
                try {
//                    LOG.error("doGet url=[" + url + "];response=[" + response.getStatusLine()+"]");
                }catch (Exception e){

                }
            }
            EntityUtils.consume(entity);
        } finally {
            if (response != null)
                response.close();
        }
        return result;
    }
    
    /**
     * get 提交数据(返回值特殊情况，响应头不全。零玖科技使用。）
     * 
     * @param url
     * @return String
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String doGetNoResult(String url, RequestConfig requestConfig,
                                       HttpRequestRetryHandler retryHandler)
                                                                    throws ClientProtocolException,
            IOException {
        String result = null;
        //Pooling connection manager
        CloseableHttpClient httpClient = HttpClients.custom().setRetryHandler(retryHandler)
            .setConnectionManager(cm).build();
        HttpGet httpGet = new HttpGet(url);
        if (requestConfig != null) {
            httpGet.setConfig(requestConfig);
        }
        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(entity, "UTF-8");
                if(result==null){
//                    LOG.error("doGet url=" + url );
                }
            }else{
                try {
//                	LOG.error("doGet url=[" + url + "];response=[" + response.getStatusLine()+"]");
                }catch (Exception e){

                }
            }
            EntityUtils.consume(entity);
        } finally {
            if (response != null)
                response.close();
        }
        return result;
    }

    /**
     * 检测连接在5s且请求在5s内的地址响应结果
     * 
     * @param url
     * @return
     */
    public static boolean checkUrl(String url) {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        RequestConfig rc = RequestConfig.custom().setConnectTimeout(10000)
            .setConnectionRequestTimeout(500).build();
        HttpGet hg = new HttpGet(url);
        hg.setConfig(rc);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(hg);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200)
                return true;
        } catch (IOException e) {
//            LOG.warn("Connect failed:" + url);
        } finally {
            try {
                if (response != null)
                    response.close();
            } catch (IOException e) {
//                LOG.error(e);
            }
        }
        return false;
    }

}
