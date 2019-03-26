package com.ibeetl.admin.core.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtil {
    public static void copy(InputStream input ,OutputStream os) {
        try {
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                os.write(buf, 0, bytesRead);
            }
           
        }catch(Exception ex) {
            throw new PlatformException("文件复制出错"+ex);
        }
        finally {
            try {
                input.close();
                os.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
    }


    /**
     * 对文件名MD5加密处理
     * @param fileName 文件名
     * @return 加密后的文件名
     */
    public static String MD5FileName(String fileName) {
        if (fileName != null) {
            int split = fileName.lastIndexOf(".");
            String pre = fileName.substring(0, split);
            String suf = fileName.substring(split);
            String newFileName = MD5Util.MD5(pre) + suf;
            return newFileName;
        } else {
            return null;
        }
    }
}
