package com.vaxjaz.polling.operation.ops;

import com.vaxjaz.polling.operation.ops.provider.OperationProviders;

import java.util.function.Function;

public interface Operation<T, R> {
    OperationProviders<R> loadOperation();

    void doOnNext(T t);

    Void onException(Throwable throwable, T t);

    Function<OperationProviders<R>, T> doOperation();
}
