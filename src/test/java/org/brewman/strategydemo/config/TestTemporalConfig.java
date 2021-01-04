package org.brewman.strategydemo.config;

import io.temporal.client.WorkflowClient;
import io.temporal.testing.TestWorkflowEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brewman.temporal.autoconfigure.TemporalProperties;
import org.brewman.temporal.autoconfigure.WorkerBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(TemporalProperties.class)
@RequiredArgsConstructor
@Slf4j
public class TestTemporalConfig  {

    @Primary
    @Bean
    public WorkflowClient workflowClient() {
        log.warn("Overriding WorkflowClient");
        TestWorkflowEnvironment workflowEnvironment = TestWorkflowEnvironment.newInstance();
        WorkflowClient workflowClient = workflowEnvironment.getWorkflowClient();
        workflowEnvironment.start();
        return workflowClient;
    }

    @Primary
    @Bean
    public WorkerBeanFactory workerBeanFactory(
            ApplicationContext applicationContext,
            WorkflowClient workflowClient,
            TemporalProperties temporalProperties) {
        log.info("WorkerBeanFactory Created");
        return new WorkerBeanFactory(applicationContext, workflowClient, temporalProperties);
    }
}
