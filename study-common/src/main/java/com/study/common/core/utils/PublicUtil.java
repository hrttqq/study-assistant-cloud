package com.study.common.core.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * @Description: 通用工具类
 * 1.对象的非空判断
 * 2.序列化和反序列化
 * @Author: Fangyx
 * @CreateDate: 2020.06.04
 * @Version: 1.0
 **/
@Slf4j
public class PublicUtil {

    private PublicUtil() {
    }


    public static final String JAVA_LANG_PACKAGE = "java.lang";

    private static final Logger logger = LoggerFactory.getLogger(PublicUtil.class);

    /**
     * @description: 判断对象为空
     * @params: obj
     * @return: 是否为空
     **/
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof List) {
            return ((List) obj).isEmpty();
        }
        if (obj instanceof String) {
            return ((String) obj).trim().equals("");
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        return false;
    }

    /**
     * @description: 判断对象不为空
     * @params: obj
     * @return:是否不为空
     **/
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * @description: 判断对象是否为空，且对象的所有属性都为空(当你的对象 实现了序列化接口或者是含有boolean类型的属性时 ，你要处理好是否要忽略这个两种类型的值。)
     * @params: obj
     * @return: 是否为空
     **/
    public static boolean objCheckIsNull(Object obj) {
        //得到类对象
        Class clazz = obj.getClass();
        // 得到所有属性
        Field[] fields = clazz.getDeclaredFields();
        // 默认返回tr
        // ue
        boolean flag = true;
        for (Field field : fields) {
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                // 得到属性值
                fieldValue = field.get(obj);
                // 得到属性类型
                Type fieldType = field.getGenericType();
                // 得到属性名
                String fieldName = field.getName();
                logger.info("属性类型：{}，属性名:{},属性值:{}", fieldType, fieldName, fieldValue);
            } catch (IllegalArgumentException ex) {
                log.error("判断对象不为空异常--IllegalArgumentException " + ex.getMessage(), ex);
            } catch (IllegalAccessException ex) {
                log.error("判断对象不为空异常--IllegalAccessException " + ex.getMessage(), ex);
            }
            //只要有一个属性值不为null 就返回false 表示对象不为null
            if (fieldValue != null) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * 根据下表获取数组中的字符串
     *
     * @param dataSource 字符串数据源
     * @param index      下标
     * @param defValue   默认值
     * @return 获取到的值
     */
    public static String getValue(String[] dataSource, int index, String defValue) {
        try {
            return dataSource[index];
        } catch (Exception e) {
            return defValue;
        }
    }


    /**
     * @param bean  Bean对象
     * @param name  属性名称
     * @param value 值
     */
    public static Object copyProperty(Object bean, String name, Object value) {
        try {
            BeanUtils.copyProperty(bean, name, value);
        } catch (IllegalAccessException e) {
            log.error("Bean对象反射时出现异常", e);
        } catch (InvocationTargetException e) {
            log.error("Bean对象中没有找到属性名为:" + name, e);
        }
        return bean;
    }

    /**
     * 复制资源Bean中的属性值到目标Bean中
     *
     * @param targetObj 目标Bean
     * @param sourceObj 资源Bean
     */
    public static Object copyProperties(Object targetObj, Object sourceObj) {
        if (null == targetObj || null == sourceObj) {
            return targetObj;
        }
        try {
            BeanUtils.copyProperties(targetObj, sourceObj);
        } catch (Exception e) {
            log.error("Bean CopyProperties Exception", e);
        }
        return targetObj;
    }


    /**
     * usage:
     * Person po = new Person();
     * PersonDTO dto = convert(po, PersonDTO::new);
     *
     * @param d
     * @param supplier
     * @param <D>
     * @param <T>
     * @return
     */
    public static <D, T> T convert(D d, Supplier<T> supplier) {
        if (d == null) {
            return null;
        }
        T t = supplier.get();
        org.springframework.beans.BeanUtils.copyProperties(d, t);
        return t;
    }

    /**
     * 复制list对象
     * @author gq
     * @date 2022/5/30 10:45
     * @Param: dl
    * @Param: supplier
     * @return java.util.List<T>
     * @throws
     */
    public static <D, T> List<T> convert(List<D> dl, Supplier<T> supplier) {
        if (dl == null) {
            return null;
        }
        List<T> tl = new ArrayList<>(dl.size());
        dl.forEach(d -> tl.add(convert(d, supplier)));
        return tl;
    }



    /**
     * 获取class本身及所有父类定义的所有属性(不重复)
     *
     * @param clazz
     * @return
     */
    public static Collection<Field> getAllDeclaredFields(Class clazz) {
        if (String.class == clazz) {
            return Collections.EMPTY_LIST;
        }
        Class cls = clazz;
        HashMap<String, Field> fieldMap = new HashMap<>();
        String key;
        while (!cls.getName().startsWith(JAVA_LANG_PACKAGE)) {
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                key = field.getName();
                if (fieldMap.containsKey(key)) {
                    continue;
                }
                fieldMap.put(key, field);
            }
            cls = cls.getSuperclass();
        }
        return fieldMap.values();
    }

    /**
     * Bean转换为Map格式
     */
    @SuppressWarnings("all")
    public static Map<String, Object> toMap(Object bean) {
        if (bean == null) {
            return null;
        }
        if (Map.class.isAssignableFrom(bean.getClass())) {
            return (Map<String, Object>) bean;
        }
        Collection<Field> fields = getAllDeclaredFields(bean.getClass());
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        try {
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(bean));
                }
            }
        } catch (Exception e) {
            log.error("exception:", e);
        }
        return map;
    }

    /**
     * Bean转换为Map格式 忽略NULL
     */
    @SuppressWarnings("all")
    public static Map<String, Object> toMapIgnoreNULL(Object bean) {
        if (bean == null) {
            return null;
        }
        if (Map.class.isAssignableFrom(bean.getClass())) {
            return (Map<String, Object>) bean;
        }
        Collection<Field> fields = getAllDeclaredFields(bean.getClass());
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (!Modifier.isStatic(field.getModifiers()) && field.get(bean) != null) {
                    map.put(field.getName(), field.get(bean));
                }
            }
        } catch (Exception e) {
            log.error("exception:", e);
        }
        return map;
    }


    /**
     * 根据名称递归获取class中定义的字段
     *
     * @param clazz
     * @param name
     * @return
     */
    public static Field getFieldRecursion(Class clazz, String name) {
        if (clazz.getName().startsWith(JAVA_LANG_PACKAGE) || StringUtils.isBlank(name)) {
            return null;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (name.equals(field.getName())) {
                return field;
            }
        }
        return PublicUtil.getFieldRecursion(clazz.getSuperclass(), name);
    }

    /**
     * 根据class中的属性名获取对应的json字段名，没有JsonProperty时，直接返回name
     *
     * @param clazz
     * @param name
     * @return
     */
    public static String getJsonColumnName(Class clazz, String name) {
        Field field = PublicUtil.getFieldRecursion(clazz, name);
        if (null == field) {
            log.info("尝试获取json字段名出错,class={},feildName=" + clazz, name);
            return name;
        }
        return PublicUtil.getJsonColumnName(field, name);
    }

    /**
     * 根据属性Field获取对应的json字段名，没有JsonProperty时，直接返回fieldName
     *
     * @param field
     * @param name
     * @return
     */
    public static String getJsonColumnName(Field field, String name) {
        if (null == field) {
            return name;
        }
        JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
        if (null == jsonProperty) {
            return name;
        }
        String jsonName = jsonProperty.value();
        if (StringUtils.isBlank(jsonName)) {
            return name;
        }
        return jsonName;
    }

    /**
     * 获取对象中指定属性的值，屏蔽异常
     *
     * @param obj
     * @param field
     * @return Object
     */
    public static Object getFieldValue(Object obj, Field field) {
        if (null == field) {
            return null;
        }
        Object value = null;
        try {
            field.setAccessible(true);
            value = field.get(obj);
        } catch (Exception e) {
            log.info("获取{}.{}的值出错,{}", obj.getClass(), field.getName(), e.getLocalizedMessage());
        }
        return value;
    }

    /**
     * 获取对象中指定属性的值，屏蔽异常
     *
     * @param obj
     * @param fieldName
     * @return Object
     */
    public static Object getFieldValueByName(Object obj, String fieldName) {
        Field field = getFieldRecursion(obj.getClass(), fieldName);

        return getFieldValue(obj, field);
    }


    /**
     * list字符转换
     * @author gq
     * @date 2022/8/17 15:37
     * @Param: list
     * @return java.util.List<java.lang.String>
     */
    public static List<String> checkListSplit(List<String> list) {
        List<String> stringList = new ArrayList<>();
        if (PublicUtil.isNotEmpty(list)) {
            list.forEach(x -> stringList.addAll(Arrays.asList(x.split(","))));
        }
        return stringList;
    }
}
