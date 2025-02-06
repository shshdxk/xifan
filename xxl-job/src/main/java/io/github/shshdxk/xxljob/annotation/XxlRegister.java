package io.github.shshdxk.xxljob.annotation;

import io.github.shshdxk.xxljob.enums.XxlRegisterEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XxlRegister {

    /**
     * 任务描述*
     */
    String jobDesc();

    /**
     * 负责人*
     */
    String author();

    /**
     * 报警邮件
     */
    String alarmEmail() default "";

    /**
     * 调度类型*
     */
    XxlRegisterEnum.ScheduleType scheduleType() default XxlRegisterEnum.ScheduleType.CRON;

    /**
     * Cron*
     * 如果是固定速度，单位是秒
     */
    String cron();

    /**
     * 任务参数
     */
    String executorParam() default "";

    /**
     * 路由策略
     */
    XxlRegisterEnum.ExecutorRouteStrategy executorRouteStrategy() default XxlRegisterEnum.ExecutorRouteStrategy.FIRST ;

    /**
     * 调度过期策略
     */
    XxlRegisterEnum.MisfireStrategy misfireStrategy() default XxlRegisterEnum.MisfireStrategy.DO_NOTHING;

    /**
     * 阻塞处理策略
     */
    XxlRegisterEnum.ExecutorBlockStrategy executorBlockStrategy() default XxlRegisterEnum.ExecutorBlockStrategy.DISCARD_LATER;

    /**
     * 任务超时时间，单位秒，大于零时生效
     */
    int executorTimeout() default 0;

    /**
     * 失败重试次数，大于零时生效
     */
    int executorFailRetryCount() default 0;
}
