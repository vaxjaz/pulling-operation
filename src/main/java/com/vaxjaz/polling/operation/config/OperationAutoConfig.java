package com.vaxjaz.polling.operation.config;

import com.vaxjaz.polling.operation.ops.AbstractOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties
public class OperationAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({AbstractOperation.class})
    public NoneUsed redisCustomerConfig(List<AbstractOperation> operations) {
        operations.forEach(operation -> {
            operation.init();
            log.info("operation task run success {}", operation.getClass());
        });
        return new NoneUsed();
    }


    public static class NoneUsed {

    }


}
