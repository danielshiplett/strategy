package org.brewman.temporal.autoconfigure;

import io.temporal.client.WorkflowClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(WorkerBeanFactory.class)
@RequiredArgsConstructor
@Slf4j
public class WorkerBeanFactoryConfiguration {

    private final ApplicationContext applicationContext;
    private final TemporalProperties temporalProperties;

    @Bean
    public WorkerBeanFactory workerBeanFactory(WorkflowClient workflowClient) {
        log.info("WorkerBeanFactory Created");
        return new WorkerBeanFactory(applicationContext, workflowClient, temporalProperties);
    }
}
