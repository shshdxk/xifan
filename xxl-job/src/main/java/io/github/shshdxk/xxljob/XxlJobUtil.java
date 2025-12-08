package io.github.shshdxk.xxljob;

import io.github.shshdxk.common.OkHttpUtils;
import io.github.shshdxk.common.UrlUtils;
import io.github.shshdxk.common.jackson.StandardObjectMapper;
import io.github.shshdxk.xxljob.config.XxlJobRegisterConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Response;
import tools.jackson.core.type.TypeReference;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * xxlJob接口类操作
 */
@Slf4j
public class XxlJobUtil {
    /**
     * xxlJob配置
     */
    public static XxlJobRegisterConfig xxlJobRegisterConfig = null;
    private final static Map<String,String> loginCookie = new HashMap<>();
    private static Headers header = Headers.of("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

    /**
     * 组装表单参数
     * @param params
     * @return String
     */
    private static String getFormParam(Map<String, Object> params) {
        return params.entrySet().stream().map(u -> {
                    if (u.getValue() == null) {
                        return u.getKey() + "=";
                    } else {
                        return u.getKey() + "=" + URLEncoder.encode(u.getValue() + "", StandardCharsets.UTF_8);
                    }
                }).collect(Collectors.joining("&"));
    }

    /**
     * 登录
     */
    public static void login() {
        String url= UrlUtils.concatSegments(xxlJobRegisterConfig.getAdminAddress(), "auth/doLogin");
        Map<String, Object> param = new HashMap<>();
        param.put("userName", xxlJobRegisterConfig.getUsername());
        param.put("password", xxlJobRegisterConfig.getPassword());
        try (Response response = OkHttpUtils.post(url, header, null, getFormParam(param))) {
            String cookies = response.headers("Set-Cookie").get(0);
            for (String s : cookies.split(";")) {
                String[] kv = s.split("=");
                if ("xxl_job_login_token".equalsIgnoreCase(kv[0])) {
                    loginCookie.put("xxl_job_login_token", kv[1].trim());
                    header = header.newBuilder()
                            .add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                            .add("Cookie", kv[0] + "=" + kv[1])
                            .build();
                    return;
                }
            }
        } catch (IOException e) {
            log.error("xxlJob自动登录失败");
        }
    }

    /**
     * 获取token
     * @return cookie
     */
    public static String getCookie() {
        for (int i = 0; i < 3; i++) {
            String cookieStr = loginCookie.get("xxl_job_login_token");
            if (cookieStr != null) {
                return "xxl_job_login_token=" + cookieStr;
            }
            login();
        }
        return null;
    }

    /**
     * 创建一个JobGroupService，根据appName和执行器名称title查询执行器列表：
     * @return groups
     */
    public static List<XxlJobGroup> getJobGroup() {
        getCookie();
        String url= UrlUtils.concatSegments(xxlJobRegisterConfig.getAdminAddress(), "jobgroup/pageList");
        Map<String, Object> param = new HashMap<>();
        param.put("appname", xxlJobRegisterConfig.getAppname());
        param.put("title", xxlJobRegisterConfig.getTitle());
        param.put("offset", 0);
        param.put("pagesize", 10);
        try {
            Response response = OkHttpUtils.post(url, header, null, getFormParam(param));
            XxlJobData<XxlJobPageData<List<XxlJobGroup>>> listData = StandardObjectMapper.readValue(OkHttpUtils.getResponseBody(response).get(), new TypeReference<>(){});
            return listData.data.data.stream()
                    .filter(xxlJobGroup -> xxlJobGroup.getAppname().equals(xxlJobRegisterConfig.getAppname()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("xxlJob自动登录失败");
        }
        return null;
    }

    /**
     * 我们在后面要根据配置文件中的appName和title判断当前执行器是否已经被注册到调度中心过，如果已经注册过那么则跳过，而/jobgroup/pageList接口是一个模糊查询接口，所以在查询列表的结果列表中，还需要再进行一次精确匹配。
     * @return 是否已注册过
     */
    public static boolean preciselyCheck() {
        List<XxlJobGroup> jobGroup = getJobGroup();
        XxlJobGroup has = null;
        if (jobGroup != null) {
            has = jobGroup.stream()
                    .filter(xxlJobGroup -> xxlJobGroup.getAppname().equals(xxlJobRegisterConfig.getAppname()))
                    .findAny().orElse(null);
        }
        return has != null;
    }

    /**
     * 注册新executor到调度中心
     * @return 是否注册成功
     */
    public static boolean autoRegisterGroup() {
        getCookie();
        String url= UrlUtils.concatSegments(xxlJobRegisterConfig.getAdminAddress(), "jobgroup/insert");
        Map<String, Object> param = new HashMap<>();
        param.put("appname", xxlJobRegisterConfig.getAppname());
        param.put("title", xxlJobRegisterConfig.getTitle());
        try {
            Response response = OkHttpUtils.post(url, header, null, getFormParam(param));
            XxlJobData<Void> listData = StandardObjectMapper.readValue(OkHttpUtils.getResponseBody(response).get(), new TypeReference<>(){});
            return listData.getCode() == 200;
        } catch (IOException e) {
            log.error("xxlJob自动登录失败");
        }
        return false;
    }

    /**
     * 查询JobInfoService，根据执行器id，jobHandler名称查询任务列表，和上面一样，也是模糊查询
     * @param jobGroupId jobGroupId
     * @param executorHandler executorHandler
     * @return jobs
     */
    public static List<XxlJobInfo> getJobInfo(Integer jobGroupId, String executorHandler) {
        getCookie();
        String url= UrlUtils.concatSegments(xxlJobRegisterConfig.getAdminAddress(), "jobinfo/pageList");
        Map<String, Object> param = new HashMap<>();
        param.put("jobGroup", jobGroupId);
        param.put("executorHandler", executorHandler);
        param.put("triggerStatus", -1);
        param.put("jobDesc", "");
        param.put("author", "");
        try {
            Response response = OkHttpUtils.post(url, header, null, getFormParam(param));
            XxlJobData<XxlJobPageData<List<XxlJobInfo>>> listData = StandardObjectMapper.readValue(OkHttpUtils.getResponseBody(response).get(), new TypeReference<>(){});
            return listData.data.data;
        } catch (IOException e) {
            log.error("xxlJob自动登录失败");
        }
        return null;
    }

    /**
     * 注册一个新任务，最终返回创建的新任务的id
     * @param xxlJobInfo xxlJobInfo
     * @return id
     */
    public static Integer addJobInfo(XxlJobInfo xxlJobInfo) {
        getCookie();
        String url= UrlUtils.concatSegments(xxlJobRegisterConfig.getAdminAddress(), "jobinfo/insert");
        Map<String, Object> param = StandardObjectMapper.getInstance().convertValue(xxlJobInfo, new TypeReference<>() {});
        try {
            Response response = OkHttpUtils.post(url, header, null, getFormParam(param));
            String r = OkHttpUtils.getResponseBody(response).get();
            XxlJobData<Void> data = StandardObjectMapper.readValue(r, new TypeReference<>(){});
            return Integer.parseInt(data.msg);
        } catch (IOException e) {
            log.error("xxlJob自动登录失败");
        }
        return -1;
    }

    /**
     * xxlJob返回数据
     * @param <T> 类型
     */
    @Getter
    @Setter
    public static class XxlJobData<T> {
        private T data;
        private int code;
        private String msg;
        private boolean success;
    }

    /**
     * xxlJob返回数据
     * @param <T> 类型
     */
    @Getter
    @Setter
    public static class XxlJobPageData<T> {
        private T data;
        private int pagesize;
        private int offset;
        private int total;
    }

    /**
     * 执行器组
     */
    @Getter
    @Setter
    public static class XxlJobGroup {
        private int id;
        private String appname;
    }

    /**
     * 任务
     */
    @Getter
    @Setter
    public static class XxlJobInfo {
        /**
         * 执行器
         */
        private Integer jobGroup;
        /**
         * 任务描述
         */
        private String jobDesc;
        /**
         * 负责人
         */
        private String author;
        /**
         * 报警邮件
         */
        private String alarmEmail;
        /**
         * 调度类型
         */
        private String scheduleType;
        /**
         * Cron
         */
        private String scheduleConf;
        private String cronGen_display;
        private String schedule_conf_CRON;
        private String schedule_conf_FIX_RATE;
        private String schedule_conf_FIX_DELAY;
        /**
         * 运行模式
         */
        private String glueType;
        /**
         * JobHandler
         */
        private String executorHandler;
        /**
         * 任务参数
         */
        private String executorParam;
        /**
         * 路由策略
         */
        private String executorRouteStrategy;
        /**
         * 子任务ID
         */
        private String childJobId;
        /**
         * 调度过期策略
         */
        private String misfireStrategy;
        /**
         * 阻塞处理策略
         */
        private String executorBlockStrategy;
        /**
         * 任务超时时间，单位秒，大于零时生效
         */
        private Integer executorTimeout;
        /**
         * 失败重试次数，大于零时生效
         */
        private Integer executorFailRetryCount;
        private String glueRemark;
        private String glueSource;
    }

}
