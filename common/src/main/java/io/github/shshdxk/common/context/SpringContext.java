package io.github.shshdxk.common.context;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

/**
 * SpringContext
 */
@Component
public class SpringContext implements ApplicationContextAware {
    private static volatile ApplicationContext context;
    private static final Object $lock = new Object[0];

    /**
     * 设置ApplicationContext
     * @param applicationContext applicationContext
     * @throws BeansException BeansException
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (context == null) {
            synchronized($lock) {
                if (context == null) {
                    context = applicationContext;
                }
            }
        }

    }

    /**
     * 获取ApplicationContext
     * @return ApplicationContext
     */
    public static ApplicationContext getContext() {
        if (context == null) {
            throw new IllegalStateException("ApplicationContext not initialized");
        }
        return context;
    }

    /**
     * 获取bean
     * @param klass bean class
     * @return bean
     * @param <T> T
     */
    public static <T> T getInstance(Class<T> klass) {
        return context.getBean(klass);
    }

    /**
     * 获取bean
     * @param name bean name
     * @param klass bean class
     * @return bean
     * @param <T> T
     */
    public static <T> T getInstance(String name, Class<T> klass) {
        return context.getBean(name, klass);
    }

    /**
     * 是否是开发环境
     * @return 是否是开发环境
     */
    public static boolean isDev() {
        return getInstance(Environment.class).acceptsProfiles(Profiles.of("dev"));
    }
}
