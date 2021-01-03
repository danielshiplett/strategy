package org.brewman.strategydemo.config;

import io.grpc.ManagedChannelBuilder;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brewman.temporal.annotations.EnableTemporal;
import org.brewman.temporal.autoconfigure.TemporalOptionsConfiguration;
import org.brewman.temporal.autoconfigure.TemporalProperties;
import org.brewman.temporal.autoconfigure.WorkerBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TemporalProperties.class)
@EnableTemporal(
        workflowBasePackages = "org.brewman.strategydemo.temporal.workflows",
        activityBasePackages = "org.brewman.strategydemo.temporal.activities")
@RequiredArgsConstructor
@Slf4j
public class TemporalConfiguration  {

    private final ApplicationContext applicationContext;
    private final TemporalOptionsConfiguration temporalOptionsConfiguration;

    @Bean
    public WorkerBeanFactory workerBeanFactory(WorkflowClient workflowClient) {
        log.info("WorkerBeanFactory Created");
        return new WorkerBeanFactory(applicationContext, workflowClient);
    }

    @Bean
    @ConditionalOnProperty(name = "temporal.enabled", havingValue = "true")
    public WorkflowClient defaultClient(TemporalProperties temporalProperties) {
        log.info("Temporal Default Client Enabled");

        WorkflowServiceStubs service;
        WorkflowServiceStubsOptions.Builder builder = WorkflowServiceStubsOptions.newBuilder();

        // Get worker to poll the common task queue.
        // gRPC stubs wrapper that talks to the local docker instance of temporal service.
        if (temporalProperties.getHost() != null) {
            ManagedChannelBuilder<?> channel =
                    ManagedChannelBuilder.forAddress(
                            temporalProperties.getHost(), temporalProperties.getPort());
            if (temporalProperties.getUseSsl() == null || !temporalProperties.getUseSsl()) {
                channel.usePlaintext();
            }

            builder.setChannel(channel.build())
                    .setEnableHttps(temporalProperties.getUseSsl());
        }

        service = WorkflowServiceStubs.newInstance(builder.build());

        WorkflowClientOptions.Builder optionsBuilder =
                temporalOptionsConfiguration.modifyClientOptions(WorkflowClientOptions.newBuilder());

        return WorkflowClient.newInstance(service, optionsBuilder.build());
    }
}
