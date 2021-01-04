package org.brewman.temporal.autoconfigure;

import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Worker.class)
@ConditionalOnMissingBean(WorkflowClient.class)
@EnableConfigurationProperties(TemporalProperties.class)
@Import({
        WorkflowClientConfiguration.class,
        WorkerBeanFactoryConfiguration.class
})
public class TemporalAutoConfiguration {
}
