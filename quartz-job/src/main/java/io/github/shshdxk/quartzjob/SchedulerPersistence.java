//package io.github.shshdxk.quartzjob;
//
//import io.github.shshdxk.common.UrlUtils;
//import io.github.shshdxk.common.jackson.StandardObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Date;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Slf4j
//public class SchedulerPersistence {
//
//    private static String schedulerStateFile = "scheduler_state.dat";
//    private static ConcurrentHashMap<String, ConcurrentHashMap<String, InvoiceSchedulerVM>> schedulers = new ConcurrentHashMap<>();
//    private static ConcurrentHashMap<String, ConcurrentHashMap<String, RecipeStatusSchedulerVM>> recipeSchedulers =
//            new ConcurrentHashMap<>();
//    private static ConcurrentHashMap<String, ConcurrentHashMap<String, InpatientFlushGuanKongSchedulerVM>> inpatientFlushGuanKongSchedulers =
//            new ConcurrentHashMap<>();
//    private static ConcurrentHashMap<String, ConcurrentHashMap<String, SendPatientInfoSchedulerVM>> sendPatientInfoSchedulers =
//            new ConcurrentHashMap<>();
//    public static final String INVOICE_SCHEDULER_KEY =  "invoiceScheduler";
//    public static final String INPATIENT_FLUSH_GUANKONG_SCHEDULER_KEY =  "inpatientFlushGuanKongScheduler";
//    public static final String RECIPE_STATUS_SCHEDULER_KEY =  "recipeStatusScheduler";
//    public static final String SEND_PATIENT_INFO_SCHEDULER_KEY =  "sendPatientInfoScheduler";
//
//    public static void init(String quartzSchedulerFilePath) {
//        if (StringUtils.isNotBlank(quartzSchedulerFilePath)) {
//            schedulerStateFile = UrlUtils.concatSegments(quartzSchedulerFilePath, schedulerStateFile);
//        }
//    }
//
//    /**
//     * 加载任务数据
//     * @return
//     */
//    public static ConcurrentHashMap<String, ConcurrentHashMap<String, InvoiceSchedulerVM>> load() {
//        try {
//            File file = new File(SchedulerPersistence.schedulerStateFile);
//            if (!file.exists()) {
//                if (file.getParentFile() != null && !file.getParentFile().exists()) {
//                    file.getParentFile().mkdirs();
//                }
//                file.createNewFile();
//            } else {
//                schedulers = StandardObjectMapper.getInstance().readValue(file,
//                        new TypeReference<ConcurrentHashMap<String, ConcurrentHashMap<String, InvoiceSchedulerVM>>>() {});
//            }
//        } catch (IOException e) {
//            log.error("读取保存的任务失败", e);
//        }
//        return schedulers;
//    }
//
//    /**
//     * 添加发票任务,并保存未执行的任务到文件
//     * @param id
//     * @param doTime
//     * @param param
//     */
//    public static synchronized void addInvoiceScheduler(String id, Date doTime, PushInvoiceMessageParam param) {
//        Map<String, InvoiceSchedulerVM> invoiceSchedulerVMMap = schedulers.computeIfAbsent(INVOICE_SCHEDULER_KEY,
//                k -> new ConcurrentHashMap<>());
//        if (!invoiceSchedulerVMMap.containsKey(id)) {
//            invoiceSchedulerVMMap.put(id, new InvoiceSchedulerVM(id, doTime, param));
//            save();
//        }
//    }
//
//    /**
//     * 移除发票任务,并保存未执行的任务到文件
//     * @param id
//     */
//    public static synchronized void removeInvoiceScheduler(String id) {
//        Map<String, InvoiceSchedulerVM> invoiceSchedulerVMMap = schedulers.computeIfAbsent(INVOICE_SCHEDULER_KEY, k -> new ConcurrentHashMap<>());
//        if (invoiceSchedulerVMMap.containsKey(id)) {
//            invoiceSchedulerVMMap.remove(id);
//            save();
//        }
//    }
//
//    /**
//     * 保存未执行的任务到文件
//     */
//    public static synchronized void save() throws IOException {
//        StandardObjectMapper.getInstance().writeValue(new File(schedulerStateFile), schedulers);
//    }
//}
