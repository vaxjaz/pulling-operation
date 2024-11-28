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
    @ConditionalOnBean(AbstractOperation.class)
    public Void redisCustomerConfig(List<AbstractOperation> operations) {
        operations.parallelStream().forEach(operation -> {
            String name = operation.getClass().getName();
            try {
                operation.init();
                log.info("operation task run success {}", name);
            } catch (Throwable e) {
                log.error("operation task run error {}", name, e);
            }
        });
        return null;
    }

}
