package com.spring.http.util;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class ConverterUtil {

    private ConverterUtil() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * 将对象属性值转成Map
     *
     * @param bean       对象实例
     * @param ignoreNull 是否忽略值为Null属性，true: 忽略  false: 保留
     * @return
     */
    public static Map<String, Object> beanToMap(Object bean, boolean ignoreNull)
            throws IllegalAccessException, NoSuchMethodException, InvocationTargetException{
        Map<String, Object> params = new HashMap<>();

        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(bean);
        for (int i = 0; i < descriptors.length; i++) {
            String name = descriptors[i].getName();
            if (!"class".equals(name)) {
                Object value = propertyUtilsBean.getNestedProperty(bean, name);
                if (ignoreNull) {
                    if (null != value) {
                        params.put(name, value);
                    }
                } else {
                    params.put(name, value);
                }
            }
        }

        return params;
    }

    /**
     * 将源对象列表转为目标类列表，相同属性值进行复制
     *
     * @param objectList  源对象列表
     * @param targetClass 目标类
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> List<T> beanToBean(List objectList, Class<T> targetClass) throws Exception {
        List targetList = new ArrayList<>();

        if (null != objectList && !objectList.isEmpty()) {
            for (Object sourceObject : objectList) {

                Object targetObject = targetClass.newInstance();

                BeanUtils.copyProperties(targetObject, sourceObject);

                targetList.add(targetObject);
            }
        }

        return targetList;

    }

}
