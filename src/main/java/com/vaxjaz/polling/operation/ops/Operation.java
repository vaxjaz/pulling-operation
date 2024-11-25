package com.vaxjaz.polling.operation.ops;

import java.util.function.Supplier;

public interface Operation<T> {

    void doOnNext(T t);

    Void onException(Throwable throwable, T t);

    void onSuccess(T t);

    void submit(T t);

    Supplier<T> doOperation();

}
