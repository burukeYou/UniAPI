package com.burukeyou.demo.util;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
public class HttpResponseUtil {

    private HttpResponseUtil(){}

    public static void write(File file, HttpServletResponse httpServletResponse) {
        if (file == null){
            throw new RuntimeException("下载的文件不存在");
        }

        File absolutePath = file.getAbsoluteFile();
        if (!file.exists()){
            throw new RuntimeException("下载的文件"+absolutePath+"不存在");
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            //String fileName = URLEncoder.encode(file.getName(), "UTF-8");

            String fileName = new String(file.getName().getBytes(), StandardCharsets.UTF_8);

            inputStream = new BufferedInputStream(new FileInputStream(file));
            httpServletResponse.reset();

            httpServletResponse.setContentType("application/octet-stream");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
            httpServletResponse.addHeader("Content-Length", "" + file.length());
            outputStream = new BufferedOutputStream(httpServletResponse.getOutputStream());
            byte[] buffer = new byte[4*1024*1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (FileNotFoundException fileNotFoundException) {
            log.error("文件下载失败，文件不存在", fileNotFoundException);
            throw new RuntimeException("文件不存在:" + absolutePath);
        } catch (Exception exception) {
            log.error("文件下载失败", exception);
            throw new RuntimeException("文件下载失败:" + absolutePath);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ioException) {
                    log.error("", ioException);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ioException) {
                    log.error("", ioException);
                }
            }
        }
    }

    /**
     * 新文件名
     * @param file
     * @param newFileName
     * @param httpServletResponse
     */
    public static void write(File file, String newFileName, HttpServletResponse httpServletResponse) {
        File absolutePath = file.getAbsoluteFile();
        if (!file.exists()){
            throw new RuntimeException("下载的文件"+absolutePath+"不存在");
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            httpServletResponse.reset();

            httpServletResponse.setContentType("application/octet-stream");
            httpServletResponse.setContentType("application/octet-stream");
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(newFileName, "UTF-8") + "\"");
            httpServletResponse.addHeader("Content-Length", "" + file.length());
            outputStream = new BufferedOutputStream(httpServletResponse.getOutputStream());
            byte[] buffer = new byte[4*1024*1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        } catch (FileNotFoundException fileNotFoundException) {
            log.error("文件下载失败，文件不存在", fileNotFoundException);
            throw new RuntimeException("文件不存在:" + absolutePath);
        } catch (Exception exception) {
            log.error("文件下载失败", exception);
            throw new RuntimeException("文件下载失败:" + absolutePath);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ioException) {
                    log.error("", ioException);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ioException) {
                    log.error("", ioException);
                }
            }
        }

    }

}
