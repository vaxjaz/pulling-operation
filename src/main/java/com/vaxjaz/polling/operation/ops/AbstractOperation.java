package com.vaxjaz.polling.operation.ops;

import com.vaxjaz.polling.operation.anno.PollingOpProperty;
import com.vaxjaz.polling.operation.enums.PollingStrategyEnum;
import com.vaxjaz.polling.operation.ops.provider.OperationProviders;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Slf4j
public abstract class AbstractOperation<T, R> implements Operation<T, R> {

    private OperationProviders<R> operationProvider;

    private long periodMillsSeconds = 2000L;

    private Executor worker;

    private ScheduledExecutorService pullTask;

    private int pullSize = 5;

    private int workSize = 100;

    private PollingStrategyEnum strategy = PollingStrategyEnum.FIXED;

    @Override
    public abstract void doOnNext(T t);

    @Override
    public Void onException(Throwable throwable, T t) {
        log.error("work exception ", throwable);
        return null;
    }

    @Override
    public abstract Function<OperationProviders<R>, T> doOperation();

    private void run() {
        if (Objects.isNull(operationProvider)) {
            throw new RuntimeException("operationProvider is null");
        }
        AtomicInteger taskSize = new AtomicInteger();
        AtomicInteger backPressure = new AtomicInteger();
        pullTask.scheduleAtFixedRate(() -> {
            doPull(taskSize, backPressure);
        }, 0, periodMillsSeconds, TimeUnit.MILLISECONDS);
    }

    private void doPull(AtomicInteger pullTaskSize, AtomicInteger backPressure) {
        if (pullTaskSize.incrementAndGet() > pullSize) {
            pullTaskSize.decrementAndGet();
            log.info("pull task size reach limited {}", pullTaskSize.get());
            return;
        }
        if (backPressure.incrementAndGet() > workSize) {
            backPressure.decrementAndGet();
            log.info("worker task size reach limited {}", backPressure.get());
            return;
        }
        try {
            T apply = doOperation().apply(operationProvider);
            doWork(backPressure, apply);
            switch (strategy) {
                case FIXED_THEN_IMMEDIATELY:
                    if (Objects.nonNull(apply)) {
                        // 如果有值，立马pull，否则走scheduleAtFixedRate
                        pullImmediately(pullTaskSize, backPressure);
                    }
                    break;
                case FIXED:
                    // do nothing
            }
        } catch (Exception e) {
            log.error("customer pull error e", e);
        } finally {
            pullTaskSize.decrementAndGet();
        }
    }

    private void pullImmediately(AtomicInteger pullTaskSize, AtomicInteger backPressure) {
        pullTask.schedule(() -> doPull(pullTaskSize, backPressure), 0, TimeUnit.MILLISECONDS);
    }

    private void doWork(AtomicInteger backPressure, T apply) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            if (Objects.nonNull(apply)) {
                doOnNext(apply);
            }
        }, Objects.nonNull(worker) ? worker : ForkJoinPool.commonPool());
        future.whenComplete((unused, throwable) -> {
                    backPressure.decrementAndGet();
                    if (Objects.nonNull(throwable)) {
                        log.error("worker task error ", throwable);
                    }
                })
                .exceptionally(throwable -> onException(throwable, apply))
        ;
    }


    public void init() {
        OperationProviders<R> providers = loadOperation();
        if (Objects.isNull(providers)) {
            throw new RuntimeException("operationProvider is null");
        }
        PollingOpProperty property = this.getClass().getAnnotation(PollingOpProperty.class);
        if (Objects.nonNull(property)) {
            this.periodMillsSeconds = property.pullDuration();
            this.pullSize = property.pullTask();
            this.workSize = property.workerTask();
            this.strategy = property.strategy();
        }
        this.operationProvider = providers;
        customerPull(defaultPullTask());
        customerWorker(ForkJoinPool.commonPool());
        run();
    }

    private ScheduledThreadPoolExecutor defaultPullTask() {
        return new ScheduledThreadPoolExecutor(
                Math.min(5, pullSize), // 核心线程数
                r -> {
                    AtomicInteger count = new AtomicInteger();
                    Thread thread = new Thread(r);
                    int add = count.incrementAndGet();
                    add = add == Integer.MAX_VALUE ? count.getAndSet(0) : add;
                    thread.setName("Customer-operation-task-Thread-" + add);
                    return thread;
                }, // 自定义线程工厂
                new ThreadPoolExecutor.DiscardPolicy() // 自定义拒绝策略
        );
    }

    @Override
    public abstract OperationProviders<R> loadOperation();

    protected void customerWorker(Executor worker) {
        this.worker = worker;
    }

    protected void customerPull(ScheduledThreadPoolExecutor executor) {
        this.pullTask = executor;
    }

}
