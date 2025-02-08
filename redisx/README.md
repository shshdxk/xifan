
``` java
@Bean
public DelayedMessageService delayedMessageService(RedissonClient redissonClient) {
    return new DelayedMessageService(redissonClient);
}
```
``` java
delayedMessageService.addHandler(a, String.class);

delayedMessageService.addMessage(a, "10000", 10);
delayedMessageService.addMessage(a, "Asdwdfaefe", 5);
```