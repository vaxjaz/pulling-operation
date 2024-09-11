package com.vaxjaz.polling.operation.ops.provider;

public abstract class OperationProviders<R> {

    final R r;

    protected OperationProviders(R r) {
        this.r = r;
    }

    public R get() {
        return r;
    }

}
