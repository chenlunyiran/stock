package com.twotiger.stock.util;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;

/**
 * Created by yangzhenwei on 2017/6/22.
 * <p>
 * 从阿里云下载文件
 */
@Component
public class OSSUtil  {
    private static String ENDPOINT = "";
    private static String ACCESS_KEY_ID = "";
    private static String ACCESS_KEY_SECRET = "";
    private static String BUCKET_NAME = "";
    public static Logger logger = LoggerFactory.getLogger(OSSUtil.class);

    @Autowired
    private Environment environment;

    @PostConstruct
    public void init(){
        ENDPOINT = environment.getProperty("oss.endpoint","null");
        ACCESS_KEY_ID = environment.getProperty("oss.access_key_id","null");
        ACCESS_KEY_SECRET = environment.getProperty("oss.access_key_secret","null");
        BUCKET_NAME = environment.getProperty("oss.bucket_name","null");
    }


    /**
     * 下载图片
     *
     * @param imageIndex
     * @return
     */
    public static OSSObject downLoad(String imageIndex) {
        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET_NAME, imageIndex);
        OSSObject ossObject = ossClient.getObject(getObjectRequest);
        return ossObject;
    }


    /**
     * 向response 响应流
     * @param imageIndex
     * @param response
     */
    public static void  responseWrite(String imageIndex,HttpServletResponse response) throws IOException {

        OutputStream out = response.getOutputStream();
        InputStream in = null;

        try {
            OSSObject ossObject  = downLoad(imageIndex);
            in = ossObject.getObjectContent();
            int data;
            while ((data = in.read()) != -1) {
                out.write(data);
            }
            out.flush();
        }catch (Exception e){
            logger.error("oss服务：响应文件失败。文件索引：" + imageIndex);
        }finally {
            if(in!=null){
                in.close();
            }
            if(out!=null){
                out.close();
            }

        }
    }

    /**
     * 上传文件
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static PutObjectResult uploadFile(String filePath,String index) throws IOException {
        OSSClient ossClient = null;
        try {
            File file = new File(filePath);
            ossClient = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            PutObjectResult result = ossClient.putObject(BUCKET_NAME, index, file);
            return result;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 上传文件
     *
     * @param data 圖片字節流
     * @return
     * @throws IOException
     */
    public static PutObjectResult uploadFile(byte[] data,String index) throws IOException {
        OSSClient ossClient = null;
        try {
            InputStream inputStream = new ByteArrayInputStream(data);
            ossClient = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            PutObjectResult result = ossClient.putObject(BUCKET_NAME, index, inputStream);
            return result;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }


    /**
     * 上传文件
     *
     * @param  圖片字節流
     * @return
     * @throws IOException
     */
    public static PutObjectResult uploadFileInputStream( InputStream inputStream,String index) throws Exception {
        OSSClient ossClient = null;
        try {
            ossClient = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
            PutObjectResult result = ossClient.putObject(BUCKET_NAME, index, inputStream);
            return result;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }



//    public String getUrl(String path, String sessionName) {
//        List<Rule> policys = new ArrayList<Rule>();
//        Rule policy = new Rule();
//        List<String> names = new ArrayList<String>();
//        names.add("/data/contract/*");
//        policy.setName(names);
//        policy.setIsAllow(true);
//        List<RuleAction> action = new ArrayList<RuleAction>();
//        action.add(RuleAction.读文件);
//        policy.setAction(action);
//        policys.add(policy);
//        return OSSTemplate.getInstance().getUrl(path, sessionName, policys);
//    }

    /**
     */
    public static String getImage(String path) {
        // 过期时间10分钟
        Date expiration = new Date(new Date().getTime() + 1000 * 60 * 10);
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(BUCKET_NAME, path,
                HttpMethod.GET);
        req.setExpiration(expiration);
        OSSClient client = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        URL signedUrl = client.generatePresignedUrl(req);
        logger.info("oss客户端" + JSON.toJSONString(client));
        return signedUrl.toString();
    }

}
