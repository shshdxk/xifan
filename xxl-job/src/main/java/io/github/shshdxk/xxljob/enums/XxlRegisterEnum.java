package io.github.shshdxk.xxljob.enums;

import lombok.Getter;

public class XxlRegisterEnum {

    @Getter
    public enum ScheduleType {
        NONE("NONE", "无"),
        CRON("CRON", "CRON"),
        FIX_RATE("FIX_RATE", "固定速度(秒)")
        ;

        private final String code;
        private final String name;

        ScheduleType(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    @Getter
    public enum ExecutorRouteStrategy {
        FIRST("FIRST", "第一个"),
        LAST("LAST", "最后一个"),
        ROUND("ROUND", "轮询"),
        RANDOM("RANDOM", "随机"),
        CONSISTENT_HASH("CONSISTENT_HASH", "一致性HASH"),
        LEAST_FREQUENTLY_USED("LEAST_FREQUENTLY_USED", "最不经常使用"),
        LEAST_RECENTLY_USED("LEAST_RECENTLY_USED", "最近最久未使用"),
        FAILOVER("FAILOVER", "故障转移"),
        BUSYOVER("BUSYOVER", "故障转移"),
        SHARDING_BROADCAST("SHARDING_BROADCAST", "分片广播"),
        ;

        private final String code;
        private final String name;

        ExecutorRouteStrategy(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    @Getter
    public enum MisfireStrategy {
        DO_NOTHING("DO_NOTHING", "忽略"),
        FIRE_ONCE_NOW("FIRE_ONCE_NOW", "立即执行一次"),
        ;

        private final String code;
        private final String name;

        MisfireStrategy(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    @Getter
    public enum ExecutorBlockStrategy {
        SERIAL_EXECUTION("SERIAL_EXECUTION", "单机串行"),
        DISCARD_LATER("DISCARD_LATER", "丢弃后续调度"),
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
