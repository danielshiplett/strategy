package org.brewman.temporal.autoconfigure;

import io.grpc.ManagedChannelBuilder;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(WorkflowClient.class)
@Slf4j
public class WorkflowClientConfiguration {

    @Bean
    public WorkflowClient defaultClient(TemporalProperties temporalProperties) {
        log.info("Temporal Default Client Enabled");

        WorkflowServiceStubs service;
        WorkflowServiceStubsOptions.Builder builder = WorkflowServiceStubsOptions.newBuilder();

        if (temporalProperties.getHost() != null) {
            ManagedChannelBuilder<?> channel =
                    ManagedChannelBuilder.forAddress(
                            temporalProperties.getHost(), temporalProperties.getPort());

            // For now, just support plaintext.
            channel.usePlaintext();
            builder.setChannel(channel.build());
        }

        service = WorkflowServiceStubs.newInstance(builder.build());
        return WorkflowClient.newInstance(service, WorkflowClientOptions.newBuilder().build());
    }
}
