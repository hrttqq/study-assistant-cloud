package com.study.common.core.utils;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.*;


@Slf4j
public class MapUtils {

    public static TreeMap<String, Object> map2ObjectTreeMap(Map<String, String> map) {
        TreeMap<String, Object> treeMap = new TreeMap<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = entry.getValue();
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            treeMap.put(entry.getKey(), value);
        }
        return treeMap;
    }

    public static TreeMap<String, String> map2StringTreeMap(Map<String, Object> map) {
        TreeMap<String, String> treeMap = new TreeMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String value;
            Object obj = entry.getValue();
            if (null == obj) {
                continue;
            }
//            if (obj instanceof Date) {
//                value = String.valueOf(((Date)obj).getTime());
//            } else {
//                value = obj.toString();
//            }
            if (obj instanceof String) {
                value = (String) obj;
            } else if (obj instanceof Date) {
                value = String.valueOf(((Date) obj).getTime());
            } else if (obj instanceof List) {
                continue;
            } else {
                value = obj.toString();
            }
            if (StringUtils.isEmpty(value)) {
                continue;
            }
            treeMap.put(entry.getKey(), value);
        }
        return treeMap;
    }

    public static TreeMap<String, String> toStringTreeMap(Object obj) {
        Map<String, Object> newMap = toTreeMap(obj, true);
        return map2StringTreeMap(newMap);
    }

    public static TreeMap<String, Object> toTreeMap(Object obj, boolean isFilterNull) {
        return obj2Map(obj, isFilterNull, false);
    }

    public static TreeMap<String, Object> toJsonMap(Object obj) {
        return obj2Map(obj, true, true);
    }

    public static TreeMap<String, String> toStringJsonMap(Object obj) {
        Map<String, Object> map = obj2Map(obj, true, true);
        return map2StringTreeMap(map);
    }

    private static TreeMap<String, Object> obj2Map(Object obj, boolean isFilterNull, boolean isJson) {
        TreeMap<String, Object> map = new TreeMap<>();
        if (obj == null) {
            return map;
        }
        try {
            Class clazz = obj.getClass();
            BeanInfo e = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] properties = e.getPropertyDescriptors();
            for (int i = 0; i < properties.length; i++) {
                PropertyDescriptor property = properties[i];
                String key = property.getName();
                if (key.equals("class")) {
                    continue;
                }
                Method getter = property.getReadMethod();
                Object value = getter.invoke(obj, new Object[0]);
                if (isFilterNull && value == null) {
                    continue;
                }
                if (isJson) {
                    key = PublicUtil.getJsonColumnName(clazz, key);
                }
                map.put(key, value);
            }
        } catch (Exception e) {
            log.error("object to json map error", e);
        }
        return map;
    }


    /**
     * 将TreeMap中所有参数按升序转换成字符串，格式：key1=value1&key2=value2
     *
     * @param map
     * @return
     */
    public static String treeMap2ascString(Map<String, Object> map) {
        return treeMap2ascString(map, null);
    }

    /**
     * 将TreeMap中所有参数按升序转换成字符串，格式：key1=value1&key2=value2
     *
     * @param map
     * @param charset 编码，为空则不编码
     * @return
     */
    public static String treeMap2ascString(Map<String, Object> map, String charset) {
        return treeMap2ascString(map, charset, '&');
    }

    /**
     * 将TreeMap中所有参数按升序转换成字符串
     *
     * @param map
     * @param charset 编码，为空则不编码
     * @param link    连接符
     * @return
     */
    public static String treeMap2ascString(Map<String, Object> map, String charset, Character link) {
        StringBuilder sb = new StringBuilder();
        boolean isEncode = StringUtils.isNotBlank(charset);//是否url编码value
        boolean isLink = null != link;
        try {
            Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Object> entry = iter.next();
                String value;
                Object obj = entry.getValue();
                if (null == obj) {
                    continue;
                }
                if (obj instanceof Date) {
                    value = String.valueOf(((Date) obj).getTime());
                } else {
                    value = obj.toString();
                }
                if (StringUtils.isEmpty(value)) {
                    continue;
                }
                sb.append(entry.getKey()).append("=").append(isEncode ? URLEncoder.encode(value, charset) : value);
                if (isLink) {
                    sb.append(link);
                }
            }
            int n = sb.length() - 1;
            if (isLink && n >= 0 && sb.charAt(n) == link.charValue()) {
                sb.deleteCharAt(n);
            }
        } catch (Exception e) {
            log.error("错误", e);
        }
        return sb.toString();
    }

    public static ObjectMapper MAPPER = nonNullMapper();
    private static TypeFactory typeFactory = MAPPER.getTypeFactory();

    public static ObjectMapper getMapper(JsonInclude.Include include) {
        ObjectMapper mapper = new ObjectMapper();
        // 设置输出时包含属性的风格
        if (include != null) {
            mapper.setSerializationInclusion(include);
        }
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        //增加转义符支持
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        //日期类型转换为时间戳
        SimpleModule module = new SimpleModule("DatesModule", new Version(0, 1, 0, "alpha", "com.com.chuansheng.luckylive", "kds"));
        module.addSerializer(java.sql.Date.class, new DateTimeSerializer());
        mapper.registerModule(module);
        return mapper;
    }

    public static class DateTimeSerializer extends JsonSerializer<java.sql.Date> {
        @Override
        public void serialize(
                java.sql.Date value,
                JsonGenerator jgen,
                SerializerProvider provider) throws IOException {
            if (null == jgen) {
                return;
            }
            if (value == null) {
                jgen.writeNull();
            } else {
                jgen.writeNumber(value.getTime());
            }
        }
    }

    /**
     * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
     */
    public static ObjectMapper nonNullMapper() {
        return getMapper(JsonInclude.Include.NON_NULL);
    }

    public static String toJson(Object obj) {
        if (null == obj) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException jpe) {
            log.error("object to json error ", jpe);
        }
        return null;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (null == json) {
            return null;
        }
        try {
            return MAPPER.readValue(json, contructType(clazz));
        } catch (IOException ioe) {
            log.error("json to object ", ioe);
        }
        return null;
    }

    public static <T> T fromJson(String json, JavaType javaType) {
        if (null == json) {
            return null;
        }
        try {
            return MAPPER.readValue(json, javaType);
        } catch (IOException ioe) {
            log.error("json to object ", ioe);
        }
        return null;
    }

    public static JavaType contructType(Class clazz) {
        return typeFactory.constructType(clazz);
    }

    public static JavaType contructType(Class clazz, Class elementClass) {
        return typeFactory.constructParametrizedType(clazz, clazz, elementClass);
    }

    public static JavaType contructType(Class clazz, JavaType elementClass) {
        return typeFactory.constructParametrizedType(clazz, clazz, elementClass);
    }

    /**
     * 构造Collection类型.
     */
    public static JavaType contructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return typeFactory.constructCollectionType(collectionClass, elementClass);
    }

    /**
     * 构造Map类型.
     */
    public static JavaType contructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return typeFactory.constructMapType(mapClass, keyClass, valueClass);
    }

}
