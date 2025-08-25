package io.github.shshdxk.redisx;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RQueue;
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

    /**
     * Redisson客户端
     */
    private final RedissonClient redissonClient;

    /**
     * 阻塞队列缓存
     */
    private final Map<String, RBlockingDeque<?>> blockingDequeHashMap = new HashMap<>();
    /**
     * 延迟队列缓存
     */
    private final Map<String, RQueue<?>> delayedQueueMap = new HashMap<>();

    /**
     * 添加处理器,处理器不会自动被添加,项目初始化时请添加全部的处理器,不然会导致没添加处理器时消息无法被处理
     * @param handler 处理器
     * @param clazz 消息类型
     * @param <T> 消息类型
     */
    public <T> void addHandler(DelayedMessageHandler<T> handler, Class<T> clazz) {
        String key = handler.getClass() + "." + clazz.getName();
        RQueue<T> rQueue;
        if (!delayedQueueMap.containsKey(key)) {
            RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(key);
            rQueue = redissonClient.getQueue(key);
            blockingDequeHashMap.put(key, blockingDeque);
            delayedQueueMap.put(key, rQueue);
            Executors.newSingleThreadExecutor().submit(() -> this.processMessages(rQueue, blockingDeque, handler));
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
        RQueue<T> rQueue;
        if (delayedQueueMap.containsKey(key)) {
            try {
                rQueue = (RQueue<T>) delayedQueueMap.get(key);
            } catch (Exception e) {
                log.error("已存在的类型和加入消息的类型不匹配");
                throw new RuntimeException(e);
            }
        } else {
            RBlockingDeque<T> blockingDeque = redissonClient.getBlockingDeque(key);
            rQueue = redissonClient.getQueue(key);
            blockingDequeHashMap.put(key, blockingDeque);
            delayedQueueMap.put(key, rQueue);
            Executors.newSingleThreadExecutor().submit(() -> this.processMessages(rQueue, blockingDeque, handler));
        }
        RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(rQueue);
        delayedQueue.offer(message, delaySeconds, TimeUnit.SECONDS);
    }

    /**
     * 处理延迟消息
     * @param rQueue 延迟队列
     * @param blockingDeque 阻塞队列
     * @param handler 处理器
     * @param <T> 消息类型
     */
    public <T> void processMessages(RQueue<T> rQueue, RBlockingDeque<T> blockingDeque, DelayedMessageHandler<T> handler) {
        while (true) {
            try {
                T message = blockingDeque.take();
                if (!handler.run(blockingDeque, message)) {
                    RDelayedQueue<T> delayedQueue = redissonClient.getDelayedQueue(rQueue);
                    delayedQueue.offer(message, 1, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
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
