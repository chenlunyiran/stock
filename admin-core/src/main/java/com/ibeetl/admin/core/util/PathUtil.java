package com.ibeetl.admin.core.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class PathUtil {
    /**
     * 格式化文件路径
     * 传入任意无规则路径可格式化为系统所需路径File.separator
     * ex：a/b\c\d format为 linux:a/b/c/d windows:a\b\c\d
     */
    public static String formatFilePath(String path) {
        if (StringUtils.isBlank(path))
            throw new NullPointerException(" path can't be null or '' ! ");
        if (!"/".equals(File.separator) && path.indexOf("/") != -1) {
            path = path.replaceAll("/", "\\\\");
        }
        if (!"\\".equals(File.separator) && path.indexOf("\\") != -1) {
            path = path.replaceAll("\\\\", "/");
        }
        return path;
    }

    /**
     * 格式化路径为url形式"/"
     * 
     * @param path
     * @return
     */
    public static String formatUrlPath(String path) {
        if (StringUtils.isBlank(path))
            throw new NullPointerException(" path can't be null or '' ! ");
        if (path.indexOf("\\") != -1) {
            path = path.replaceAll("\\\\", "/");
        }
        return path;
    }


    public static String generateRelativePath(String relativePath, String fileName) {
        String path = relativePath + DateUtil.now("/yyyy/MM/dd/")
                      + System.currentTimeMillis() + "_" + FileUtil.MD5FileName(fileName);
        return PathUtil.formatUrlPath(path);
    }

    public static String convertPath(String relativePath) {
        return PathUtil.formatUrlPath(relativePath + DateUtil.now("/yyyy/MM/dd/"));
    }


    public static String convertMd5Name(String fileName) {
        return System.currentTimeMillis() + "_" + FileUtil.MD5FileName(fileName);
    }

}
