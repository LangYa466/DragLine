package me.nelly.utils;

import nellyobfuscator.NellyClassObfuscator;

@NellyClassObfuscator
public class Timer {
    private long startTime;
    private long endTime;

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        endTime = System.currentTimeMillis();
    }

    public long getSeconds() {
        return (endTime - startTime) / 1000;
    }


}
