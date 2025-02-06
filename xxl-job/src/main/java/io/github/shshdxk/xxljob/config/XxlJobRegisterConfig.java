package io.github.shshdxk.xxljob.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XxlJobRegisterConfig {

    private String username;
    private String password;
    /**
     * 使用XxlJobSpringExecutor.admin.addresses
     */
    private String adminAddress;
    private String appname;
    private String title;
}
