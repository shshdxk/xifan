package io.github.shshdxk.common.migration;

/**
 * @author MigratorService
 */
public interface MigratorService {

    /**
     * 迁移之前执行
     */
    void beforeEachMigrate();

    /**
     * 迁移之后执行
     */
    default void afterEachMigrate() {}

}
