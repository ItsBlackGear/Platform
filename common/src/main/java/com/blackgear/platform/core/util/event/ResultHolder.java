package com.blackgear.platform.core.util.event;

public class ResultHolder<T> {
    private final CancellableResult result;
    private final T value;

    public static <T> ResultHolder<T> pass() {
        return new ResultHolder<>(CancellableResult.PASS, null);
    }

    public static <T> ResultHolder<T> submit(T value) {
        return new ResultHolder<>(CancellableResult.CANCEL, value);
    }

    ResultHolder(CancellableResult result, T value) {
        this.result = result;
        this.value = value;
    }

    public boolean isCancelled() {
        return this.result.isCancelled();
    }

    public T getValue() {
        return this.value;
    }
}