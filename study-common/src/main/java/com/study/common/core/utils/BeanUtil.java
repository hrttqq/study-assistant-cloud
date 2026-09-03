package com.study.common.core.utils;

import com.alibaba.fastjson2.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Bean拷贝工具类。
 *
 * <p>
 * 浅拷贝只复制对象第一层属性值。如果属性是引用类型，源对象和目标对象会共享同一个引用。
 * 深拷贝会通过序列化重新构建对象，适合需要复制嵌套对象、集合对象且不希望共享引用的场景。
 * </p>
 */
public class BeanUtil {

    private BeanUtil() {
    }

    /**
     * 浅拷贝，创建目标类型的新对象。
     *
     * @param source      源对象
     * @param targetClass 目标对象类型
     * @param <T>         目标对象泛型
     * @return 目标对象
     */
    public static <T> T shallowCopy(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        T target = newInstance(targetClass);
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 浅拷贝，复制到已有目标对象。
     *
     * @param source 源对象
     * @param target 目标对象
     * @param <T>    目标对象泛型
     * @return 目标对象
     */
    public static <T> T shallowCopy(Object source, T target) {
        if (source == null || target == null) {
            return target;
        }
        BeanUtils.copyProperties(source, target);
        return target;
    }

    /**
     * 浅拷贝，忽略源对象中的null属性。
     *
     * @param source 源对象
     * @param target 目标对象
     * @param <T>    目标对象泛型
     * @return 目标对象
     */
    public static <T> T shallowCopyIgnoreNull(Object source, T target) {
        if (source == null || target == null) {
            return target;
        }
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
        return target;
    }

    /**
     * 浅拷贝集合。
     *
     * @param sources     源对象集合
     * @param targetClass 目标对象类型
     * @param <S>         源对象泛型
     * @param <T>         目标对象泛型
     * @return 目标对象集合
     */
    public static <S, T> List<T> shallowCopyList(Collection<S> sources, Class<T> targetClass) {
        if (sources == null) {
            return null;
        }
        List<T> targets = new ArrayList<>(sources.size());
        for (S source : sources) {
            targets.add(shallowCopy(source, targetClass));
        }
        return targets;
    }

    /**
     * 深拷贝，创建目标类型的新对象。
     *
     * <p>
     * 要求源对象可以被JSON序列化，目标类型可以被JSON反序列化。
     * </p>
     *
     * @param source      源对象
     * @param targetClass 目标对象类型
     * @param <T>         目标对象泛型
     * @return 深拷贝后的目标对象
     */
    public static <T> T deepCopy(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(source), targetClass);
    }

    /**
     * 深拷贝集合。
     *
     * <p>
     * 要求集合元素可以被JSON序列化，目标元素类型可以被JSON反序列化。
     * </p>
     *
     * @param sources     源对象集合
     * @param targetClass 目标对象类型
     * @param <S>         源对象泛型
     * @param <T>         目标对象泛型
     * @return 深拷贝后的目标对象集合
     */
    public static <S, T> List<T> deepCopyList(Collection<S> sources, Class<T> targetClass) {
        if (sources == null) {
            return null;
        }
        List<T> targets = new ArrayList<>(sources.size());
        for (S source : sources) {
            targets.add(deepCopy(source, targetClass));
        }
        return targets;
    }

    private static String[] getNullPropertyNames(Object source) {
        BeanWrapper wrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] propertyDescriptors = wrapper.getPropertyDescriptors();
        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = wrapper.getPropertyValue(propertyName);
            if (propertyValue == null) {
                emptyNames.add(propertyName);
            }
        }
        return emptyNames.toArray(new String[0]);
    }

    private static <T> T newInstance(Class<T> targetClass) {
        try {
            return targetClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Create target bean instance failed: " + targetClass.getName(), e);
        }
    }
}
