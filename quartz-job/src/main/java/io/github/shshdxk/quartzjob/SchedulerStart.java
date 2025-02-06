//package io.github.shshdxk.quartzjob;
//
//import com.sunhealth.ihhis.config.HisHospitalProperties;
//import com.sunhealth.ihhis.task.scheduler.vm.InvoiceSchedulerVM;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.quartz.Scheduler;
//import org.quartz.SchedulerException;
//import org.quartz.impl.StdSchedulerFactory;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Slf4j
//@Component
//@Order(9999)
//@AllArgsConstructor
//public class SchedulerStart {
//
//    private final HisHospitalProperties hisHospitalProperties;
//
//    @PostConstruct
//    public void init() {
//        log.info("SchedulerStart...");
//        try {
//            SchedulerPersistence.init(hisHospitalProperties);
//            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
//            scheduler.start();
//            ConcurrentHashMap<String, ConcurrentHashMap<String, InvoiceSchedulerVM>> data = SchedulerPersistence.load();
//            if (!data.isEmpty()) {
//                ConcurrentHashMap<String, InvoiceSchedulerVM> invoiceData = data.get(SchedulerPersistence.INVOICE_SCHEDULER_KEY);
//                if (invoiceData != null && !invoiceData.isEmpty()) {
//                    invoiceData.forEach((key, value) -> {
//                        try {
//                            InvoiceScheduler.delayTask(value.getParam(), value.getDoTime(), key);
//                        } catch (SchedulerException e) {
//                            log.error("自动创建未处理任务失败", e);
//                        }
//                    });
//                }
//            }
//            // 在退出前保存调度器状态
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                try {
//                    SchedulerPersistence.save();
//                    scheduler.shutdown();
//                } catch (SchedulerException e) {
//                    log.trace("quartz关闭失败", e);
//                }
//            }));
//        } catch (SchedulerException ex) {
//            throw new RuntimeException("quartz初始化失败", ex);
//        }
//    }
//}
