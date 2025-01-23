package io.github.shshdxk.redisx;

import org.redisson.api.RBlockingDeque;

public interface DelayedMessageHandler<T> {

    void run(RBlockingDeque<T> blockingDeque);

}
