package org.brewman.strategydemo.workflow;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.activity.ActivityInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TicketEvaluationActivitiesImpl implements TicketEvaluationActivities {

    @Override
    public void validateTicketInput(String description) {
        log.info("validateTicketInput: {}", description);

        ActivityExecutionContext ctx = Activity.getExecutionContext();
        ActivityInfo info = ctx.getInfo();
        log.info("namespace=" +  info.getActivityNamespace());
        log.info("workflowId=" + info.getWorkflowId());
        log.info("runId=" + info.getRunId());
        log.info("activityId=" + info.getActivityId());

        // Does nothing if the input is OK.
    }

    @Override
    public String generateTicketNumber() {
        log.info("generateTicketNumber:");
        return "TIK-01234";
    }
}
