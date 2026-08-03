package com.study.common.core.utils;

import com.fasterxml.jackson.databind.JavaType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RestInterface {

    private static final MediaType MEDIA_TYPE_JSON = new MediaType("application", "json", Charset.forName("UTF-8"));



    @Autowired
    protected  RestTemplate restTemplate;

    public void delPort(){
        restTemplate =  getFactory(null,null);
    }

    @PostConstruct
    public void init() {
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter converter : messageConverters) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
                break;
            }
        }
    }


    /**
     * http请求工具代理模式
     * @author gq
     * @date 2022/6/8 15:30
     * @Param: agentUrl
    * @Param: agentPort
     */
    public void init2(String agentUrl,String agentPort ) {
        restTemplate =  getFactory(agentUrl,agentPort);
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        for (HttpMessageConverter converter : messageConverters) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
                break;
            }
        }
    }

    public static  RestTemplate  getFactory(String agentUrl, String agentPort) {
        if(StringUtils.isBlank(agentPort) || StringUtils.isBlank(agentPort)){
            return new RestTemplate();
        }
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //单位为ms
        factory.setReadTimeout(30000);
        //单位为ms
        factory.setConnectTimeout(30000);
        // 代理的url网址或ip, port端口
        InetSocketAddress address = new InetSocketAddress(agentUrl, Integer.valueOf(agentPort));
        Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
        factory.setProxy(proxy);
        RestTemplate  restTemplate =new RestTemplate();
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }



    /**
     * 重载方法
     * @author gq
     * @date 2022/7/1 15:28
    */
    public  <T> T  postForObject(String url , @Nullable Object request, Class<T> responseType,Object... uriVariables){
        return restTemplate.postForObject(url,request,responseType);
    }



    public <T> T postJson(String url, Object request, Class<T> responseType) throws RestClientException {
        if (responseType == String.class) {
            return (T) this.postJson(url, request);
        }
        return this.postJson(url, request, MapUtils.contructType(responseType));
    }

    public <T> T postJson(String url, Object request, JavaType responseType) throws RestClientException {
        String res = this.postJson(url, request);
        return MapUtils.fromJson(res, responseType);
    }

    public String postJson(String url, Object request) throws RestClientException {
        return this.exchange(url, HttpMethod.POST, MapUtils.toJson(request));
    }

    public String postJson(String url, String json) throws RestClientException {
        return this.exchange(url, HttpMethod.POST, json);
    }

    public <T> T postJsonForm(String url, Object request, Class<T> responseType) throws RestClientException {
        if (responseType == String.class) {
            return (T) this.postJsonForm(url, request);
        }
        return this.postJsonForm(url, request, MapUtils.contructType(responseType));
    }


    public <T> T postJsonForm(String url, Object request, JavaType responseType) throws RestClientException {
        String res = this.postJsonForm(url, request);
        return MapUtils.fromJson(res, responseType);
    }

    public String postJsonForm(String url, Object request) throws RestClientException {
        Map<String, Object> map = MapUtils.toJsonMap(request);
        return this.postForm(url, map);
    }

    public <T> T postForm(String url, Object request, Class<T> responseType) throws RestClientException {
        Map<String, Object> map = MapUtils.toTreeMap(request, true);
        return this.postForm(url, map, responseType);
    }

    public  String postForm(String url, Object request, HttpHeaders httpHeaders) throws RestClientException {
        Map<String, Object> map = MapUtils.toTreeMap(request, true);
        return this.postForm(url, map,httpHeaders);
    }


    /**
     * 发送短信,因短信那边限制需使用HashMap
     * @param url
     * @param request
     * @param httpHeaders
     * @return
     * @throws RestClientException
     */
    public  String postFormV2(String url, Object request,HttpHeaders httpHeaders) throws RestClientException {
        Map<String, Object> map = MapUtils.toTreeMap(request, true);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map, httpHeaders);
        return this.httpExchange(url, HttpMethod.POST, httpEntity);
    }

    public  String postFormV3(String url, Object request,HttpHeaders httpHeaders) throws RestClientException {
        Map<String, Object> map = (Map)request;
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map, httpHeaders);
        return this.httpExchange(url, HttpMethod.POST, httpEntity);
    }

    public  String postFormV3(String url, String body,HttpHeaders httpHeaders) throws RestClientException {
        HttpEntity httpEntity = new HttpEntity(body, httpHeaders);
        return this.httpExchange(url, HttpMethod.POST, httpEntity);
    }

    public <T> T postForm(String url, Map<String, ? extends Object> request, Class<T> responseType) throws RestClientException {
        String res = this.postForm(url, request);
        if (responseType == String.class) {
            return (T) res;
        }
        return MapUtils.fromJson(res, responseType);
    }

    public <T> T postForm(String url, Map<String, ? extends Object> request, JavaType responseType) throws RestClientException {
        String res = this.postForm(url, request);
        return MapUtils.fromJson(res, responseType);
    }


    public String postForm(String url, Map<String, ? extends Object> request) throws RestClientException {
        MultiValueMap<String, String> map = new LinkedMultiValueMap();
        for (Map.Entry<String, ?> entry : request.entrySet()) {
            Object value = entry.getValue();
            if (null == value) {
                continue;
            }
            String s;
            if (value instanceof String) {
                s = (String) value;
            } else {
                s = value.toString();
            }
            map.add(entry.getKey(), s);
        }
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, new HttpHeaders());

        return this.httpExchange(url, HttpMethod.POST, httpEntity);
    }

    public String postForm(String url, Map<String, ? extends Object> request, HttpHeaders httpHeaders) throws RestClientException {
        MultiValueMap<String, String> map = new LinkedMultiValueMap();
        for (Map.Entry<String, ?> entry : request.entrySet()) {
            Object value = entry.getValue();
            if (null == value) {
                continue;
            }
            String s;
            if (value instanceof String) {
                s = (String) value;
            } else {
                s = value.toString();
            }
            map.add(entry.getKey(), s);
        }
        httpHeaders = (null == httpHeaders) ? new HttpHeaders() : httpHeaders;
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, httpHeaders);

        return this.httpExchange(url, HttpMethod.POST, httpEntity);
    }


    public <T> T get(String url, JavaType responseType) throws RestClientException {
        String res = this.get(url);
        return MapUtils.fromJson(res, responseType);
    }

    public String get(String url) throws RestClientException {
        return this.exchange(url, HttpMethod.GET, null);
    }

    private String exchange(String url, HttpMethod method, String body) throws RestClientException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> httpEntity;
        if (HttpMethod.POST == method) {
            headers.setContentType(MEDIA_TYPE_JSON);
            httpEntity = new HttpEntity<>(body, headers);
        } else {
            httpEntity = new HttpEntity<>(headers);
        }

        return this.httpExchange(url, method, httpEntity);
    }





    private String httpExchange(String url, HttpMethod method, HttpEntity httpEntity) throws RestClientException {
        if (StringUtils.isBlank(url)) {
            throw new RestClientException("request url can not blank");
        }
        if (url.indexOf(' ') >= 0) {
            log.info("url中含有空格，现将空格转换为20%，以便能正常执行请求");
            url = url.replaceAll(" ", "20%");
        }
        try {
            log.info("request: " + url + " ,method: " + method.name() + " ,header: " + httpEntity.getHeaders() + " ,body: " + httpEntity.getBody());
            long start = System.currentTimeMillis();
            ResponseEntity<String> responseEntity = restTemplate.exchange(url, method, httpEntity, String.class);
            long spent = System.currentTimeMillis() - start;
            HttpStatus status = responseEntity.getStatusCode();
            String res = responseEntity.getBody();
            log.info("response status：{} ,spent [" + spent + "]ms ,body: {}", status, res);
            if (status == HttpStatus.OK) {
                return res;
            }
        } catch (RestClientException e) {
            log.error("request RestClientException：" , e);
            throw e;
        }
        return null;
    }
}
