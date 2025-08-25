package io.github.shshdxk.common.migration;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shshdxk
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MigrationVersion {

    /**
     * 版本号
     *
     * @return 版本号
     */
    int version() default 0;

}
