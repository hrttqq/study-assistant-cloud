package com.study.common.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * httpServletRequest操作工具类
 *
 * @author:houkai 2018/4/4 11:13
 */
@Slf4j
public class HttpHelpUtil {

    private HttpHelpUtil(){}

    private static final String UNKNOWN = "unknown";
    private static final String POINT = ".";


    /**
     * 取得请求头信息
     *
     * @param request 请求
     * @return map 请求头信息
     */
    public static Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>(32);
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 组装head
     * @param mediaType
     * @param map
     * @return
     */
    public  static HttpHeaders getHeadersAuth(MediaType mediaType, Map<String,Object> map) {
        HttpHeaders headers = new HttpHeaders();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            headers.add(entry.getKey(),entry.getValue().toString());
        }
        headers.setContentType(mediaType);
        return headers;
    }

    public  static HttpHeaders getHeaders(MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        return headers;
    }

    /**
     * 组装头部鉴权
     * @author gq
     * @date 2022/5/30 10:42
     * @Param: mediaType
     * @Param: userName
     * @Param: passWord
     * @return org.springframework.http.HttpHeaders
     * @throws
     */
    public  static HttpHeaders getHeadersAuth(MediaType mediaType,String userName, String passWord) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic "+ Base64Util.encryptBASE64(userName,passWord));
        headers.setContentType(mediaType);
        return headers;
    }

    /**
     * 获取报文体信息
     *
     * @param request 请求
     * @return string 报文体信息
     */
    public static String getBodyInfo(HttpServletRequest request){
        StringBuilder buffer = new StringBuilder();
        try(
                InputStream inputStream = request.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            char[] charBuffer = new char[256];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                buffer.append(charBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            setErrorMsg(e);
        }
        return buffer.toString();
    }


    private static void setErrorMsg(Exception e) {
        log.error("当前类:" + Thread.currentThread().getStackTrace()[1].getClassName()
                + "  方案名：" + Thread.currentThread().getStackTrace()[1].getMethodName()
                + "异常：" + e.getMessage(), e);
    }

    /**
     * 获取当前网络IP
     *
     * @param request 请求
     * @return ipAddress 当前网络IP
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    setErrorMsg(e);
                    return "";
                }
                if (inet == null) {
                    return "";
                }
                ipAddress = inet.getHostAddress();
                log.info("&&&&&&&&&&&获取到的本地ip为:{}", ipAddress);
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && !UNKNOWN.equalsIgnoreCase(ipAddress) && ipAddress.contains(POINT))  {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(','));
        }
        return ipAddress;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url    发送请求的 URL
     * @param params 请求的参数集合
     * @return 远程资源的响应结果
     */
    @SuppressWarnings("unused")
    private static String sendPost(String url, Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        try{
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // POST方法
            conn.setRequestMethod("POST");
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();
            // 获取URLConnection对象对应的输出流
            try(
                    OutputStream outputStream = conn.getOutputStream();
                    OutputStreamWriter out = new OutputStreamWriter(outputStream , StandardCharsets.UTF_8);
            ){
                // 发送请求参数
                if (params != null) {
                    StringBuilder param = new StringBuilder();
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        if (param.length() > 0) {
                            param.append("&");
                        }
                        param.append(entry.getKey());
                        param.append("=");
                        param.append(entry.getValue());
                    }
                    out.write(param.toString());
                }
                // flush输出流的缓冲
                out.flush();
            }
            try(
                    // 定义BufferedReader输入流来读取URL的响应
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            ){
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (IOException e) {
            setErrorMsg(e);
        }
        return result.toString();
    }

    //============  Json 帮助内容 此类只能使用阿里的fastjosn组件，其他不要再使用 start=================

    /**
     * 将json转化为实体POJO
     *
     * @param jsonStr json数据
     * @param <T>     t
     * @param obj     实体POJO
     * @return {@code <T>T} 实体POJO
     */
    public static <T> T jsonToObj(String jsonStr, Class<T> obj) {
        T t = null;
        try {
            t = JSONObject.toJavaObject(JSON.parseObject(jsonStr), obj);
        } catch (Exception e) {
            setErrorMsg(e);
        }
        return t;
    }

    /**
     * 将实体POJO转化为Map的数据JSON
     *
     * @param obj 实体POJO
     * @param <T> t
     * @return {@code <T>JSONObject} Map的数据JSON
     * @throws JSONException json异常
     */
    public static <T> JSONObject objectToJson(T obj) throws JSONException {
        return (JSONObject) JSONObject.toJSON(obj);
    }

    /**
     * 将json转化为Map
     *
     * @param jsonStr json
     * @return map
     */
    public static Map<String, String> json2Map(String jsonStr) {
        Map<String, String> map = new HashMap(16);
        // 将json字符串转换成jsonObject
        JSONArray jsonObject = JSONObject.parseArray(jsonStr);
        Iterator ite = jsonObject.iterator();
        // 遍历jsonObject数据,添加到Map对象
        int k = 0;
        while (ite.hasNext()) {
            String key = ite.next().toString();
            String value = jsonObject.get(k).toString();
            map.put(key, value);
            k += 1;
        }
        return map;
    }

    public static String object2String(Object obj) {
        return JSONObject.toJSONString(obj);
    }

    //============  Json 帮助内容 end=================



}