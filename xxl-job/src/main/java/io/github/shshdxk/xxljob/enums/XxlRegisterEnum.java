package io.github.shshdxk.xxljob.enums;

import lombok.Getter;

/**
 * 任务配置枚举
 */
public class XxlRegisterEnum {

    /**
     * 调度类型
     */
    @Getter
    public enum ScheduleType {
        /**
         * 无
         */
        NONE("NONE", "无"),
        /**
         * CRON
         */
        CRON("CRON", "CRON"),
        /**
         * 固定速度(秒)
         */
        FIX_RATE("FIX_RATE", "固定速度(秒)")
        ;

        private final String code;
        private final String name;

        ScheduleType(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    /**
     * 路由策略
     */
    @Getter
    public enum ExecutorRouteStrategy {
        /**
         * 第一个
         */
        FIRST("FIRST", "第一个"),
        /**
         * 最后一个
         */
        LAST("LAST", "最后一个"),
        /**
         * 轮询
         */
        ROUND("ROUND", "轮询"),
        /**
         * 随机
         */
        RANDOM("RANDOM", "随机"),
        /**
         * 一致性HASH
         */
        CONSISTENT_HASH("CONSISTENT_HASH", "一致性HASH"),
        /**
         * 最不经常使用
         */
        LEAST_FREQUENTLY_USED("LEAST_FREQUENTLY_USED", "最不经常使用"),
        /**
         * 最近最久未使用
         */
        LEAST_RECENTLY_USED("LEAST_RECENTLY_USED", "最近最久未使用"),
        /**
         * 故障转移
         */
        FAILOVER("FAILOVER", "故障转移"),
        /**
         * 忙碌转移
         */
        BUSYOVER("BUSYOVER", "忙碌转移"),
        /**
         * 分片广播
         */
        SHARDING_BROADCAST("SHARDING_BROADCAST", "分片广播"),
        ;

        private final String code;
        private final String name;

        ExecutorRouteStrategy(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    /**
     * 调度过期策略
     */
    @Getter
    public enum MisfireStrategy {
        /**
         * 忽略
         */
        DO_NOTHING("DO_NOTHING", "忽略"),
        /**
         * 立即执行一次
         */
        FIRE_ONCE_NOW("FIRE_ONCE_NOW", "立即执行一次"),
        ;

        private final String code;
        private final String name;

        MisfireStrategy(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    /**
     * 阻塞处理策略
     */
    @Getter
    public enum ExecutorBlockStrategy {
        /**
         * 单机串行
         */
        SERIAL_EXECUTION("SERIAL_EXECUTION", "单机串行"),
        /**
         * 丢弃后续调度
         */
        DISCARD_LATER("DISCARD_LATER", "丢弃后续调度"),
        /**
         * 覆盖之前调度
         */
        COVER_EARLY("COVER_EARLY", "覆盖之前调度"),
        ;

        private final String code;
        private final String name;

        ExecutorBlockStrategy(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }



}
