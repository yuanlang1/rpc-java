package com.yl.trace;

import java.util.UUID;

/**
 * @author yl
 * @date 2025-05-08 22:24
 */
public class TraceIdGenerator {
    private static final SnowflakeIdGenerator SNOWFLAKE = new SnowflakeIdGenerator(0L);

    public static String generateTraceId() {
        return Long.toHexString(SNOWFLAKE.nextId());
    }


    public static String generateTraceIdUUID() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        return uuidString.replace("-", "");
    }

    public static String generateSpanId() {
        return String.valueOf(System.currentTimeMillis());
    }

    static class SnowflakeIdGenerator {
        // 机器ID 0~1023
        private final long workerId;
        // 基准时间
        private final long epoch = 1609459200000L;
        // 序列号 0~4095
        private long sequence = 0L;
        // 上一次生成ID的时间戳
        private long lastTimestamp = -1L;

        public SnowflakeIdGenerator(long workerId) {
            if (workerId < 0 || workerId > 1023) {
                throw new IllegalArgumentException("Worker Id 必须在0~1023之间");
            }
            this.workerId = workerId;
        }

        // 生成下一个ID
        public synchronized long nextId() {
            long timestamp = System.currentTimeMillis();

            // 当前时间小于上一次生成ID的时间 时钟回拨
            if (timestamp < lastTimestamp) {
                throw new RuntimeException("时钟回拨！");
            }
            // 当前时间等于上一次生成ID的时间 递增序列号
            if (timestamp == lastTimestamp) {
                sequence = (sequence + 1) & 0xFFF;  // 12为序列号 最大4095
                if (sequence == 0) {
                    // 序列号溢出，等待下一毫秒
                    timestamp = waitNextMills(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }
            // 更新上一次生成ID的时间
            lastTimestamp = timestamp;
            // 生成ID
            return ((timestamp - epoch) << 22) | (workerId << 12) | sequence;
        }

        private long waitNextMills(long lastTimestamp) {
            long timestamp = System.currentTimeMillis();
            while (timestamp <= lastTimestamp) {
                timestamp = System.currentTimeMillis();
            }
            return timestamp;
        }
    }

}
