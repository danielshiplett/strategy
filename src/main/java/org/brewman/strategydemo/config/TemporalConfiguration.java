package org.brewman.strategydemo.config;

import io.grpc.ManagedChannelBuilder;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brewman.spring.temporal.config.TemporalOptionsConfiguration;
import org.brewman.spring.temporal.config.TemporalProperties;
import org.brewman.strategydemo.workflow.TicketWorkflowImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TemporalProperties.class)
//@EnableTemporalWorkflows("org.brewman.strategydemo.workflow")
@RequiredArgsConstructor
@Slf4j
public class TemporalConfiguration  {

    private final TemporalOptionsConfiguration temporalOptionsConfiguration;

    @Bean
    public Worker ticketWorker(WorkerFactory workerFactory) {
        log.info("Temporal Ticket Worker Created");
        Worker worker = workerFactory.newWorker(TicketWorkflowImpl.TASK);
        worker.registerWorkflowImplementationTypes(TicketWorkflowImpl.class);
        workerFactory.start();

        return  worker;
    }

    @Bean
    public WorkerFactory defaultWorkerFactory(WorkflowClient workflowClient) {
        log.info("Temporal Default Worker Factory Enabled");
        return WorkerFactory.newInstance(workflowClient);
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
