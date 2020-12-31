package org.brewman.strategydemo.workflow;

import ai.applica.spring.boot.starter.temporal.WorkflowFactory;
import ai.applica.spring.boot.starter.temporal.annotations.TemporalTest;
import com.google.common.base.Throwables;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowException;
import io.temporal.client.WorkflowOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@SpringBootTest
@TemporalTest
class TicketWorkflowTest {

    private TestWorkflowEnvironment testWorkflowEnvironment;
    private Worker worker;

    @Autowired
    WorkflowFactory workflowFactory;

    @Autowired
    TicketActivities ticketActivities;

    @BeforeEach
    void beforeEachTicketEvaluationWorkflowTest() {
        log.info("beforeEachTicketEvaluationWorkflowTest");
        testWorkflowEnvironment = TestWorkflowEnvironment.newInstance();
        worker = workflowFactory.makeWorker(testWorkflowEnvironment, TicketWorkflowImpl.class);
    }

    @AfterEach
    void afterEachTicketEvaluationWorkflowTest() {
        testWorkflowEnvironment.close();
    }

    /**
     * Test that we can launch a workflow asynchronously and wait for it to hit a 'middle' state.
     */
    @Test
    void test_succeedAsync() {
        log.info("test_succeedAsync");

        worker.registerActivitiesImplementations(ticketActivities);
        testWorkflowEnvironment.start();

        // Customize the options with the ticket name as the workflow ID.
        WorkflowOptions.Builder builder = workflowFactory
                .defaultOptionsBuilder(TicketWorkflowImpl.class)
                .setWorkflowId("TIKNAME");

        // Create the stub.
        final TicketWorkflow workflow = workflowFactory.makeStub(TicketWorkflow.class, builder, testWorkflowEnvironment.getWorkflowClient());
        log.info("makeStub complete");

        try {
            // Start the workflow and wait for the ticket to move beyond the NOT_CREATED status.
            WorkflowClient.start(workflow::createTicket, "TIKNAME", "this is a description");
            log.info("workflow started");

            await()
                    .atMost(Duration.ofSeconds(10))
                    .untilAsserted(() -> assertNotEquals(TicketEntity.Status.NOT_CREATED, workflow.getTicketStatus()));

            // Log the history of the workflow.
            log.trace("diagnostics: {}", testWorkflowEnvironment.getDiagnostics());
        } catch (WorkflowException e) {
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }
    }

    /**
     * Highly unlikely in reality.  This is just to understand how activity failures
     * work in Temporal.io.
     */
    @Test
    void test_failToGenerateTicketNumber() {
        // Mock the activities to make the generateTicketNumber throw an
        // IllegalArgumentException.  This exception will be our one type
        // that does not trigger a retry.
        TicketActivities mockTicketActivities = mock(TicketActivities.class);

        willThrow(new IllegalArgumentException("cannot send notifications"))
                .given(mockTicketActivities)
                .sendStartTicketNotifications(any(TicketEntity.class));

        worker.registerActivitiesImplementations(mockTicketActivities);
        testWorkflowEnvironment.start();

        // Customize the options with the ticket name as the workflow ID.
        WorkflowOptions.Builder builder = workflowFactory
                .defaultOptionsBuilder(TicketWorkflowImpl.class)
                .setWorkflowId("TIKNAME")
                .setRetryOptions(RetryOptions.newBuilder()
                        .setInitialInterval(Duration.ofSeconds(1))
                        .setDoNotRetry(IllegalArgumentException.class.getName())
                        .build());

        // Create the stub.
        final TicketWorkflow workflow = workflowFactory.makeStub(TicketWorkflow.class, builder, testWorkflowEnvironment.getWorkflowClient());

        try {
            // Perform the synchronous workflof method.  We don't want it to return.
            workflow.createTicket("TIKNAME", "this is a description");
            fail("unreachable");
        } catch (WorkflowException e) {
            log.error(e.getMessage(), e);
            verify(mockTicketActivities, times(1)).sendStartTicketNotifications(any());
            Throwable rootCause = Throwables.getRootCause(e);
            assertThat(rootCause, instanceOf(ApplicationFailure.class));

            ApplicationFailure applicationFailure = (ApplicationFailure)rootCause;
            assertEquals("cannot send notifications", applicationFailure.getOriginalMessage());

            //verify(activities, times(1)).validateTicketInput(eq("this is a description"));
        }
    }
}
