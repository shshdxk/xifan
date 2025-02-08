
``` java
// 自动注册任务
@Component
public class XxlJobRegister implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    @Async
    public void onApplicationEvent(ApplicationReadyEvent event) {
        XxlJobRegisterConfig config = new XxlJobRegisterConfig();
        config.setUsername("admin");
        config.setPassword("123456");
        config.setAdminAddress("http://127.0.0.1:8080/xxl-job-admin");
        config.setAppname("auto-test");
        config.setTitle("自动注册测试");
        new XxlJobAutoRegister().Register(config);
    }
}
```

``` java
// 调用
@XxlJob(value = "test job")
@XxlRegister(cron = "0 0 1 1 * ?", author = "xuechun", jobDesc = "test job1")
public void run() {
    log.info("测试任务1 ...");
}

@XxlJob(value = "testJob2")
@XxlRegister(scheduleType = XxlRegisterEnum.ScheduleType.FIX_RATE, cron = "3600", author = "xuechun", jobDesc = "test job2")
public void run1() {
    log.info("测试任务2 ...");
}
```