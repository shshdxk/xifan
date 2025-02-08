package io.github.shshdxk.xxljob;

import com.xxl.job.core.handler.annotation.XxlJob;
import io.github.shshdxk.xxljob.annotation.XxlRegister;
import io.github.shshdxk.xxljob.config.XxlJobRegisterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class XxlJobAutoRegister {
    private ApplicationContext applicationContext;

    public void Register(ApplicationContext applicationContext, XxlJobRegisterConfig xxlJobRegisterConfig) {
        try {
            this.applicationContext = applicationContext;
            XxlJobUtil.xxlJobRegisterConfig = xxlJobRegisterConfig;
            // 注册执行器
            addJobGroup();
            // 注册任务
            addJobInfo();
        } catch (Exception e) {
            // 这里不输出详细的日志，自动注册执行器失败的话就手动去加执行器，自动注册只是为了偷懒
            log.error("自动注册xxlJob执行器失败" + e.getMessage());
            log.error("自动注册xxlJob执行器失败", e);
        }
    }

    private void addJobGroup() {
        if (XxlJobUtil.preciselyCheck()) {
            return;
        }

        if (XxlJobUtil.autoRegisterGroup()) {
            log.info("auto register xxl-job group success!");
        }
    }

    private void addJobInfo() {
        List<XxlJobUtil.XxlJobGroup> jobGroups = XxlJobUtil.getJobGroup();
        XxlJobUtil.XxlJobGroup xxlJobGroup = jobGroups.get(0);

        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);

            Map<Method, XxlJob> annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(),
                    (MethodIntrospector.MetadataLookup<XxlJob>) method -> AnnotatedElementUtils.findMergedAnnotation(method, XxlJob.class));
            for (Map.Entry<Method, XxlJob> methodXxlJobEntry : annotatedMethods.entrySet()) {
                Method executeMethod = methodXxlJobEntry.getKey();
                XxlJob xxlJob = methodXxlJobEntry.getValue();

                //自动注册
                if (executeMethod.isAnnotationPresent(XxlRegister.class)) {
                    XxlRegister xxlRegister = executeMethod.getAnnotation(XxlRegister.class);
                    List<XxlJobUtil.XxlJobInfo> jobInfo = XxlJobUtil.getJobInfo(xxlJobGroup.getId(), xxlJob.value());
                    if (jobInfo != null && !jobInfo.isEmpty()) {
                        //因为是模糊查询，需要再判断一次
                        Optional<XxlJobUtil.XxlJobInfo> first = jobInfo.stream()
                                .filter(xxlJobInfo -> xxlJobInfo.getExecutorHandler().equals(xxlJob.value()))
                                .findFirst();
                        if (first.isPresent()) {
                            continue;
                        }
                    }
                    try {
                        XxlJobUtil.XxlJobInfo xxlJobInfo = createXxlJobInfo(xxlJobGroup, xxlJob, xxlRegister);
                        Integer jobInfoId = XxlJobUtil.addJobInfo(xxlJobInfo);
                        log.info("xxlJob执行器注册成功: " + xxlJob.value() + " - " + jobInfoId);
                    } catch (Exception e) {
                        log.info("xxlJob执行器注册失败: " + xxlJob.value());
                    }
                }
            }
        }
    }

    private XxlJobUtil.XxlJobInfo createXxlJobInfo(XxlJobUtil.XxlJobGroup xxlJobGroup, XxlJob xxlJob, XxlRegister xxlRegister) {
        XxlJobUtil.XxlJobInfo xxlJobInfo = new XxlJobUtil.XxlJobInfo();
        xxlJobInfo.setJobGroup(xxlJobGroup.getId());
        xxlJobInfo.setJobDesc(xxlRegister.jobDesc());
        xxlJobInfo.setAuthor(xxlRegister.author());
        xxlJobInfo.setAlarmEmail(xxlRegister.alarmEmail());
        xxlJobInfo.setScheduleType(xxlRegister.scheduleType().getCode());
        xxlJobInfo.setScheduleConf(xxlRegister.cron());
        xxlJobInfo.setGlueType("BEAN");
        xxlJobInfo.setExecutorHandler(xxlJob.value());
        xxlJobInfo.setExecutorParam(xxlRegister.executorParam());
        xxlJobInfo.setExecutorRouteStrategy(xxlRegister.executorRouteStrategy().getCode());
        xxlJobInfo.setMisfireStrategy(xxlRegister.misfireStrategy().getCode());
        xxlJobInfo.setExecutorBlockStrategy(xxlRegister.executorBlockStrategy().getCode());
        xxlJobInfo.setExecutorTimeout(xxlRegister.executorTimeout());
        xxlJobInfo.setExecutorFailRetryCount(xxlRegister.executorFailRetryCount());
        String remark;
        try {
            remark = "本任务由: " + System.getProperty("user.name") + " 自动注册";
        } catch (Exception e) {
            remark = "本任务由注册器自动注册";
        }
        xxlJobInfo.setGlueRemark(remark);
        return xxlJobInfo;
    }
}
