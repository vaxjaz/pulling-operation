package com.vaxjaz.polling.operation.ops.impl;

import com.vaxjaz.polling.operation.anno.PollingOpProperty;
import com.vaxjaz.polling.operation.dto.SomeThing;
import com.vaxjaz.polling.operation.enums.PollingStrategyEnum;
import com.vaxjaz.polling.operation.ops.AbstractOperation;
import com.vaxjaz.polling.operation.ops.provider.CustomerOpeProvider;
import com.vaxjaz.polling.operation.ops.provider.OperationProviders;
import com.vaxjaz.polling.operation.ops.provider.Redistemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Slf4j
@PollingOpProperty(pullDuration = 3000L, pullTask = 10, workerTask = 500, strategy = PollingStrategyEnum.FIXED_THEN_IMMEDIATELY)
public class MyOperation extends AbstractOperation<SomeThing, Redistemplate> {

    @Override
    public void doOnNext(SomeThing someThing) {

    }

    @Override
    public Function<OperationProviders<Redistemplate>, SomeThing> doOperation() {
        return pros -> {
            Redistemplate provider = pros.get();
            //
            return provider.doSomething();
        };
    }

    @Override
    public OperationProviders<Redistemplate> loadOperation() {
        return new CustomerOpeProvider(new Redistemplate());
    }

    @Override
    public Void onException(Throwable throwable, SomeThing someThing) {
        log.error("t", throwable);
        return null;
    }
}
