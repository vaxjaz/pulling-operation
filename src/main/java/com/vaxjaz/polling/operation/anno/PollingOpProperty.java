package com.vaxjaz.polling.operation.anno;

import com.vaxjaz.polling.operation.enums.PollingStrategyEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PollingOpProperty {

    int pullTask() default 5;

    int workerTask() default 100;

    long pullDuration() default 2000L;

    PollingStrategyEnum strategy() default PollingStrategyEnum.FIXED;

}
