package io.cloudbridge.retry;

@FunctionalInterface
public interface ThrowingRunnable {

    void run() throws Exception;
}
