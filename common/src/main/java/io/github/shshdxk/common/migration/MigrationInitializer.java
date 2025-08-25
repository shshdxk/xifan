package io.github.shshdxk.common.migration;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author shshdxk
 */
@Component
public class MigrationInitializer {

    /**
     * 迁移版本列表
     */
    private final List<Integer> versions;
    /**
     * MigratorService列表
     */
    private final Map<Integer, MigratorService> migratorVisionMap = new HashMap<>();

    /**
     * 构造函数
     * @param map 迁移服务
     */
    public MigrationInitializer(Map<String, MigratorService> map) {
        map.forEach((k, v) -> {
            MigrationVersion m;
            if (AopUtils.isAopProxy(v)) {
                try {
                    m = ((MigratorService) ((Advised) v).getTargetSource().getTarget())
                        .getClass().getDeclaredAnnotation(MigrationVersion.class);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                m = v.getClass().getDeclaredAnnotation(MigrationVersion.class);
            }
            if (m != null) {
                migratorVisionMap.put(m.version(), v);
            }
        });
        this.versions = migratorVisionMap.keySet().stream().sorted().collect(Collectors.toList());
    }

    /**
     * 迁移
     * @param oldVersion 旧版本
     * @param saveNewVersion 保存新版本
     */
    public void migrator(int oldVersion, Consumer<Integer> saveNewVersion) {
        int newVersion = -1;
        boolean hasNew = false;
        for (int v : versions) {
            if (v > oldVersion) {
                MigratorService service = migratorVisionMap.get(v);
                service.beforeEachMigrate();
                service.afterEachMigrate();
                newVersion = v;
                hasNew = true;
            }
        }
        if (hasNew) {
            saveNewVersion.accept(newVersion);
        }
    }
}
