package org.brewman.strategydemo.config;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import lombok.extern.slf4j.Slf4j;
import org.brewman.strategydemo.service.SomeOtherService;
import org.brewman.strategydemo.temporal.activities.TicketActivitiesImpl;
import org.brewman.strategydemo.temporal.workflows.TicketWorkflowImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.brewman.strategydemo.temporal.workflows.TicketWorkflow.TASK;

@TestConfiguration
@Slf4j
public class TestTemporalConfig {

    private final WorkflowClient workflowClient;

    public TestTemporalConfig(SomeOtherService someOtherService) {
        TestWorkflowEnvironment workflowEnvironment = TestWorkflowEnvironment.newInstance();
        Worker worker = workflowEnvironment.newWorker(TASK);
        worker.registerWorkflowImplementationTypes(TicketWorkflowImpl.class);

        workflowClient = workflowEnvironment.getWorkflowClient();

        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder()
                .setTaskQueue(TASK)
                .build();

        worker.registerActivitiesImplementations(new TicketActivitiesImpl(someOtherService));
        workflowEnvironment.start();
    }

    @Primary
    @Bean
    public WorkflowClient testClient() {
        log.warn("Overriding WorkflowClient");
        return workflowClient;
    }
}
