package com.study.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * SpringContent工具类，用于普通类调用Srping Bean，不需要注解
 * 使用方法在main启动时进行
 * ConfigurableApplicationContext ctx = SpringApplication.run(ServiceApplication.class, args);
 * SpringContextUtil.setApplicationContext(ctx);
 *
 * @author 李建华
 */
@Service
@Slf4j
public class SpringContextUtil {

    private SpringContextUtil() {
    }

    /**
     * 缓存当前程序的IPV4地址 当程序main启动时进行获取一次初始化,后面就不需要再去读取直接使用
     */
    private static String localHostIp = "127.0.0.1";
    /**
     * 缓存当前程序绑定的端口 当程序main启动时进行获取一次初始化,后面就不需要再去读取直接使用
     */
    private static String localHostBindPort = "9004";

    /**
     * 缓存当前程序绑定的端口 当程序main启动时进行获取一次初始化,后面就不需要再去读取直接使用
     */
    private static String springbootApplicationName = "";

    /**
     * 缓存当前程序所使用的配置文件  当程序main启动时进行获取一次初始化,后面就不需要再去读取直接使用
     */
    private static String springbootProfilesActive = "";

    /**
     * 缓存当前程序的path目录路径  当程序main启动时进行获取一次初始化,后面就不需要再去读取直接使用
     */
    private static String springbootContextPath = "";

    /**
     * Kafka的topic前缀，在创建时分开启动环境创建topic来区分开发及本地，
     * 这里是直接拿SPRINGBOOT_PROFILES_ACTIVE 加上下划线 "_" 如: local_
     */
    private static String kafkaTopicPrefix = "";
    /**
     * Redis的Key前缀，在创建时分开启动环境创建key来区分开发及本地，
     * 这里是直接拿SPRINGBOOT_PROFILES_ACTIVE 加上下划线 "_" 如: local_
     */
    private static String redisPrefix = "";

    private static ApplicationContext applicationContext = null;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        log.info("------SpringContextUtil setApplicationContext-------");
        SpringContextUtil.applicationContext = applicationContext;
        Environment item = applicationContext.getEnvironment();
        SpringContextUtil.localHostBindPort = item.getProperty("server.port");
        SpringContextUtil.springbootContextPath = item.getProperty("server.servlet.context-path");
        SpringContextUtil.localHostIp = getLocalHostIpAddress();
        SpringContextUtil.springbootApplicationName = item.getProperty("spring.application.name");
        SpringContextUtil.springbootProfilesActive = item.getProperty("spring.profiles.active");
        SpringContextUtil.kafkaTopicPrefix = SpringContextUtil.springbootProfilesActive + "_";
        SpringContextUtil.redisPrefix = SpringContextUtil.springbootProfilesActive + "_";
    }

    /**
     * 获取当前运行程序服务器本地IPV4地址
     *
     * @return
     */
    public static String getLocalHostIpAddress() {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error("获取当前电脑IP4地址异常", e);
        }

        if (localHost != null) {
            //返回格式为：xxx.xxx.xxx
            return localHost.getHostAddress();
        }
        return "";
    }

    /**
     * 获取Context
     *
     * @return ApplicationContext 容器
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 注意 bean name默认 = 类名(首字母小写)
     * 例如: A8sClusterDao = getBean("a8sClusterDao")
     *
     * @param name 类名(首字母小写)
     * @return bean对象
     * @throws BeansException 异常
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    /**
     * 根据类名获取到bean
     *
     * @param <T>   t
     * @param clazz class
     * @return bean对象
     * @throws BeansException 异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBeanByClass(Class<T> clazz) throws BeansException {
        try {
            char[] cs = clazz.getSimpleName().toCharArray();
            cs[0] += 32;
            return applicationContext.getBean(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据类名获取到bean,内部根据名称转换首写字母转为小字书再根据名称获取
     *
     * @param <T>   t
     * @param clazz class
     * @return bean对象
     * @throws BeansException 异常
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBeanByName(Class<T> clazz) throws BeansException {
        try {
            char[] cs = clazz.getSimpleName().toCharArray();
            cs[0] += 32;
            return (T) applicationContext.getBean(String.valueOf(cs));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前程序所注解的Beans
     * 调用方式如获取所有Controllers：
     * controllers=getApplicationContext.getAllAnnotations(Controller.class);
     * if (controllers != null) {
     * for (String controllerBeanName : controllers) {
     * System.out.println(controllerBeanName);
     * }
     * }
     *
     * @param clazz
     * @return
     */
    public static String[] getAllAnnotations(Class<? extends Annotation> clazz) {
        return getApplicationContext().getBeanNamesForAnnotation(clazz);
    }

    /**
     * 判断是否存在Bean
     *
     * @param name 类名
     * @return boolean
     */
    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    /**
     * 判断是否为一个共享的单例bean
     *
     * @param name 类名
     * @return boolean
     * @throws NoSuchBeanDefinitionException 无法找到或检索bean时 抛出改异常
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(name);
    }


    /**
     * 获取当前程序所有的Controller绑定的地址;
     * 调用本方法要在Contrller进行公开一个方法进行调用，内部进行HttpServletRequest的上下文获取
     *
     * @return 这里只是借用此实体类
     */
    /**
     public static List<RequestConditionItem> getAllContextRequestMappingPath() {
     List<RequestConditionItem> pathList = new ArrayList<RequestConditionItem>();//存储所有url集合
     HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
     WebApplicationContext wac = (WebApplicationContext) request.getAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE);//获取上下文对象
     // 通过上下文对象获取RequestMappingHandlerMapping实例对象 有可能有异常 因为有swagger的mapping
     // RequestMappingHandlerMapping bean = wac.getBean(RequestMappingHandlerMapping.class);

     //获取所有的RequestMapping
     Map<String, HandlerMapping> allRequestMappings = BeanFactoryUtils.beansOfTypeIncludingAncestors(wac, HandlerMapping.class, true, false);
     for (HandlerMapping handlerMapping : allRequestMappings.values()) {
     //本项目只需要RequestMappingHandlerMapping中的URL映射 有可能有异常 因为有swagger的mapping
     if (handlerMapping instanceof RequestMappingHandlerMapping) {

     RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) handlerMapping;
     Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
     for (Map.Entry<RequestMappingInfo, HandlerMethod> requestMappingInfoHandlerMethodEntry : handlerMethods.entrySet()) {
     try {
     RequestMappingInfo requestMappingInfo = requestMappingInfoHandlerMethodEntry.getKey();
     HandlerMethod mappingInfoValue = requestMappingInfoHandlerMethodEntry.getValue();
     RequestMethodsRequestCondition methodCondition = requestMappingInfo.getMethodsCondition();
     // String requestType = SetUtils.first(methodCondition.getMethods()).name();

     String requestType = methodCondition.getMethods().toArray().length > 0 ? methodCondition.getMethods().toArray()[0].toString() : "";

     PatternsRequestCondition patternsCondition = requestMappingInfo.getPatternsCondition();
     //String requestUrl = SetUtils.first(patternsCondition.getPatterns());
     String requestUrl = patternsCondition.getPatterns().toArray().length > 0 ? patternsCondition.getPatterns().toArray()[0].toString() : "";

     String controllerName = mappingInfoValue.getBeanType().toString();
     String requestMethodName = mappingInfoValue.getMethod().getName();

     System.out.println("请求路径：" + requestUrl + "控制器：" + controllerName + " 类型:" + requestType + " 方法名称:" + requestMethodName);
     Class<?>[] methodParamTypes = mappingInfoValue.getMethod().getParameterTypes();
     RequestConditionItem modelitem = new RequestConditionItem(requestUrl, requestType, controllerName, requestMethodName, methodParamTypes);
     pathList.add(modelitem);
     } catch (Exception ex) {
     LogHelper.writeError("获取当前程序所有的Controller绑定的地址异常", ex);
     }

     }
     break;
     }
     }

     return pathList;
     }
     */

    /**
     * 获取当前程序配置绑定的端口
     *
     * @return
     */
    public static String getContextBindPort() {
        Environment item = applicationContext.getEnvironment();
        return item.getProperty("server.port");
    }

    public static String getLocalHostIp() {
        return localHostIp;
    }

    public static void setLocalHostIp(String localHostIp) {
        SpringContextUtil.localHostIp = localHostIp;
    }

    public static String getLocalHostBindPort() {
        return localHostBindPort;
    }

    public static void setLocalHostBindPort(String localHostBindPort) {
        SpringContextUtil.localHostBindPort = localHostBindPort;
    }

    public static String getSpringbootApplicationName() {
        return springbootApplicationName;
    }

    public static void setSpringbootApplicationName(String springbootApplicationName) {
        SpringContextUtil.springbootApplicationName = springbootApplicationName;
    }

    public static String getSpringbootProfilesActive() {
        return springbootProfilesActive;
    }

    public static void setSpringbootProfilesActive(String springbootProfilesActive) {
        SpringContextUtil.springbootProfilesActive = springbootProfilesActive;
    }

    public static String getSpringbootContextPath() {
        return springbootContextPath;
    }

    public static void setSpringbootContextPath(String springbootContextPath) {
        SpringContextUtil.springbootContextPath = springbootContextPath;
    }

    public static String getKafkaTopicPrefix() {
        return kafkaTopicPrefix;
    }

    public static void setKafkaTopicPrefix(String kafkaTopicPrefix) {
        SpringContextUtil.kafkaTopicPrefix = kafkaTopicPrefix;
    }

    public static String getRedisPrefix() {
        return redisPrefix;
    }

    public static void setRedisPrefix(String redisPrefix) {
        SpringContextUtil.redisPrefix = redisPrefix;
    }
}

