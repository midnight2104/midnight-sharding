package com.midnight.sharding.engine;

public class ShardingContext {
    private static final ThreadLocal<ShardingResult> LOCAL = new ThreadLocal<>();

    public static ShardingResult get() {
        return LOCAL.get();
    }

    public static void set(ShardingResult result) {
        LOCAL.set(result);
    }
}
