# Custom Polling Task Framework
This project is a custom framework designed to implement polling tasks and process those tasks efficiently. The framework offers flexible task processing strategies and interval configurations, allowing users to define and adjust the task polling behavior as per their requirements.

### Features
* Customizable Polling Strategy: You can load and define custom polling operations using the Operation interface, which supports loading (loadOperation()) and executing (doOperation()) user-defined polling strategies.
* Task Processing: After performing the doOperation(), the framework processes the returned tasks, handling each task accordingly. If any exception occurs during task processing, a callback mechanism is triggered.
* Flexible Polling Interval and Task Strategy: With the help of the PollingOpProperty attribute, you can define and customize the polling interval as well as the task handling strategy.
* Asynchronous Polling and Task Processing: Polling and task processing can be performed as separate asynchronous operations, providing simplified backpressure support. This allows the polling properties to adjust dynamically based on task processing efficiency.
### Core Components
1.Operation Interface:

* loadOperation(): Loads user-defined polling strategies.
* doOperation(): Executes the polling operation based on the loaded strategy and returns tasks for further processing.

2.PollingOpProperty:
* This property defines the key configurations for the polling mechanism, including the polling interval and task processing strategies.

3.Asynchronous Task Handling:
* The framework supports asynchronous execution of polling and task processing, with backpressure mechanisms to adjust PollingOpProperty based on task processing performance.

### Usage
Define your custom polling operation by extend AbstractOperation
```java
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
```
