package org.brewman.strategydemo.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class TicketEvaluationWorkflowImpl implements TicketEvaluationWorkflow {

    // An activity or workflow may also throw an ApplicationFailure.newNonRetryable()
    // instead of us defining the types here.
    private final ActivityOptions options =
            ActivityOptions.newBuilder()
                    .setScheduleToCloseTimeout(Duration.ofHours(1))
                    .setRetryOptions(
                            RetryOptions.newBuilder()
                                    .setInitialInterval(Duration.ofSeconds(1))
                                    .setDoNotRetry(IllegalArgumentException.class.getName())
                                    .build()
                    )
                    .build();

    private final TicketEvaluationActivities activities =
            Workflow.newActivityStub(TicketEvaluationActivities.class, options);

    @Override
    public void createTicket(String description) {
        log.info("createTicket: {}", description);
        activities.validateTicketInput(description);
        String ticketNumber = activities.generateTicketNumber();
    }
}
