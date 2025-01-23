package io.github.shshdxk.redisx;

import org.redisson.api.RBlockingDeque;

public interface DelayedMessageHandler<T> {

    /**
     *
     * @param blockingDeque
     * @param message
     * @return true or error: remove message from blockingDeque, false: keep message in blockingDeque
     */
    boolean run(RBlockingDeque<T> blockingDeque, T message);

}
