package io.github.shshdxk.redisx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 延迟消息服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DelayedMessageService {

    private final RedissonClient redissonClient;

    private final Map<String, RBlockingDeque<?>> blockingDequeHashMap = new HashMap<>();
    private final Map<String, RDelayedQueue<?>> delayedQueueMap = new HashMap<>();

    /**
     * 添加处理器,处理器不会自动被添加,项目初始化时请添加全部的处理器,不然会导致没添加处理器时消息无法被处理
     * @param handler 处理器
     * @param clazz 消息类型
     * @param <T> 消息类型
     */
    public <T> void addHandler(DelayedMessageHandler<T> handler, Class<T> clazz) {
        String key = handler.getClass() + "." + clazz.getName();
        RDelayedQueue<T> delayedQueue;
        if (!delayedQueueMap.containsKey(key)) {
            RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(key);
            delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
            blockingDequeHashMap.put(key, blockingDeque);
            delayedQueueMap.put(key, delayedQueue);
            Executors.newSingleThreadExecutor().submit(() -> this.processMessages(delayedQueue, blockingDeque, handler));
        }
    }

    /**
     * 添加延迟消息
     * @param handler 处理器
     * @param message 消息内容
     * @param delaySeconds 单位:秒
     * @param <T> 消息类型
     */
    public <T> void addMessage(DelayedMessageHandler<T> handler, T message, long delaySeconds) {
        String key = handler.getClass() + "." + message.getClass();
        RDelayedQueue<T> delayedQueue;
        if (delayedQueueMap.containsKey(key)) {
            try {
                delayedQueue = (RDelayedQueue<T>) delayedQueueMap.get(key);
            } catch (Exception e) {
                log.error("已存在的类型和加入消息的类型不匹配");
                throw new RuntimeException(e);
            }
        } else {
            RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(key);
            delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
            blockingDequeHashMap.put(key, blockingDeque);
            delayedQueueMap.put(key, delayedQueue);
            Executors.newSingleThreadExecutor().submit(() -> this.processMessages(delayedQueue, blockingDeque, handler));
        }
        delayedQueue.offer(message, delaySeconds, TimeUnit.SECONDS);
    }

    /**
     * 处理延迟消息
     * @param delayedQueue 延迟队列
     * @param blockingDeque 阻塞队列
     * @param handler 处理器
     * @param <T> 消息类型
     */
    public <T> void processMessages(RDelayedQueue<T> delayedQueue, RBlockingDeque<T> blockingDeque, DelayedMessageHandler<T> handler) {
        while (true) {
            try {
                T message = blockingDeque.take();
                if (!handler.run(blockingDeque, message)) {
                    delayedQueue.offer(message, 1000, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
                log.warn(e.getMessage());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            } catch (Throwable e) {
                log.error("中断异常", e);
            }
        }
    }

}
