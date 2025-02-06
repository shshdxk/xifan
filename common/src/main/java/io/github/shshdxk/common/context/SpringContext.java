package io.github.shshdxk.common.context;


import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {
    private static volatile ApplicationContext context;
    private static final Object $lock = new Object[0];

    public SpringContext() {
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (context == null) {
            synchronized($lock) {
                if (context == null) {
                    context = applicationContext;
                }
            }
        }

    }

    public static ApplicationContext getContext() {
        if (context == null) {
            throw new IllegalStateException("ApplicationContext not initialized");
        }
        return context;
    }

    public static <T> T getInstance(Class<T> klass) {
        return context.getBean(klass);
    }

    public static <T> T getInstance(String name, Class<T> klass) {
        return context.getBean(name, klass);
    }

    public static boolean isDev() {
        return getInstance(Environment.class).acceptsProfiles(Profiles.of("dev"));
    }
}
