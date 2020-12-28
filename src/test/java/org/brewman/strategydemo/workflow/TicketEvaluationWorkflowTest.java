package org.brewman.strategydemo.workflow;

import com.google.common.base.Throwables;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowException;
import io.temporal.client.WorkflowOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.testing.TestWorkflowEnvironment;
import io.temporal.worker.Worker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.brewman.strategydemo.workflow.TicketEvaluationWorkflow.TASK;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

@Slf4j
class TicketEvaluationWorkflowTest {

    private TestWorkflowEnvironment workflowEnvironment;
    private Worker worker;
    private WorkflowClient workflowClient;
    private WorkflowOptions workflowOptions;

    @BeforeEach
    void beforeEachTicketEvaluationWorkflowTest() {
        workflowEnvironment = TestWorkflowEnvironment.newInstance();
        worker = workflowEnvironment.newWorker(TASK);
        worker.registerWorkflowImplementationTypes(TicketEvaluationWorkflowImpl.class);

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
     * Highly unlikely in reality.  This is just to understand how activity failures
     * work in Temporal.io.
     */
    @Test
    void test_failToGenerateTicketNumber() {
        // Mock the activities to make the generateTicketNumber throw an
        // IllegalArgumentException.  This exception will be our one type
        // that does not trigger a retry.
        TicketEvaluationActivities activities = mock(TicketEvaluationActivities.class);
        given(activities.generateTicketNumber())
                .willThrow(new IllegalArgumentException("Out of ticket numbers"));

        TicketEvaluationWorkflow workflow = null;

        try {
            worker.registerActivitiesImplementations(activities);
            workflowEnvironment.start();

            workflow = workflowClient.newWorkflowStub(TicketEvaluationWorkflow.class, workflowOptions);
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
        }
    }
}
