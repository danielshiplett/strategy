package org.brewman.strategydemo.workflow;

import com.google.common.base.Throwables;
import com.uber.m3.tally.Scope;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.history.v1.HistoryEvent;
import io.temporal.api.workflowservice.v1.WorkflowServiceGrpc;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowException;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.failure.ApplicationFailure;
import io.temporal.internal.common.WorkflowExecutionUtils;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Iterator;

import static org.awaitility.Awaitility.await;
import static org.brewman.strategydemo.workflow.TicketWorkflow.TASK;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
class TicketWorkflowTest {

    private TestWorkflowEnvironment workflowEnvironment;
    private Worker worker;
    private WorkflowClient workflowClient;
    private WorkflowOptions workflowOptions;

    @BeforeEach
    void beforeEachTicketEvaluationWorkflowTest() {
        workflowEnvironment = TestWorkflowEnvironment.newInstance();
        worker = workflowEnvironment.newWorker(TASK);
        worker.registerWorkflowImplementationTypes(TicketWorkflowImpl.class);

        workflowClient = workflowEnvironment.getWorkflowClient();

        workflowOptions = WorkflowOptions.newBuilder()
                .setTaskQueue(TASK)
                .build();
    }

    @AfterEach
    void afterEachTicketEvaluationWorkflowTest() {
        workflowEnvironment.close();
    }

    /**
     * Test that we can launch a workflow asynchronously and wait for it to hit a 'middle' state.
     */
    @Test
    void test_succeedAsync() {
        // Mock the activities to make the generateTicketNumber throw an
        // IllegalArgumentException.  This exception will be our one type
        // that does not trigger a retry.
        TicketActivities activities = new TicketActivitiesImpl();

        worker.registerActivitiesImplementations(activities);
        workflowEnvironment.start();

        final TicketWorkflow workflow = workflowClient.newWorkflowStub(TicketWorkflow.class, workflowOptions);

        try {
            // Start the workflow and wait for the ticket to move beyond the NOT_CREATED status.
            WorkflowExecution workflowExecution = WorkflowClient.start(workflow::createTicket, "this is a description");
            await()
                    .atMost(Duration.ofSeconds(10))
                    .untilAsserted(() -> assertNotEquals(TicketEntity.Status.NOT_CREATED, workflow.getTicketStatus()));

            // This is how you extract the full history of the workflow.  It is verbose.  Pretty print isn't so pretty.
            Scope scope = new com.uber.m3.tally.NoopScope();
            Iterator<HistoryEvent> history =
                    WorkflowExecutionUtils.getHistory(workflowEnvironment.getWorkflowService(),
                    workflowEnvironment.getNamespace(),
                    workflowExecution, scope);
            String result = WorkflowExecutionUtils.prettyPrintHistory(history, true);
            log.trace("history: {}", result);
        } catch (WorkflowException e) {
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
        TicketActivities activities = mock(TicketActivities.class);
        given(activities.generateTicketName())
                .willThrow(new IllegalArgumentException("Out of ticket numbers"));

        TicketWorkflow workflow = null;

        try {
            worker.registerActivitiesImplementations(activities);
            workflowEnvironment.start();

            workflow = workflowClient.newWorkflowStub(TicketWorkflow.class, workflowOptions);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fail(e.getMessage());
        }

        try {
            workflow.createTicket("this is a description");
            fail("unreachable");
        } catch (WorkflowException e) {
            Throwable rootCause = Throwables.getRootCause(e);
            assertThat(rootCause, instanceOf(ApplicationFailure.class));

            ApplicationFailure applicationFailure = (ApplicationFailure)rootCause;
            assertEquals("Out of ticket numbers", applicationFailure.getOriginalMessage());

            verify(activities, times(1)).validateTicketInput(eq("this is a description"));
        }
    }
}
