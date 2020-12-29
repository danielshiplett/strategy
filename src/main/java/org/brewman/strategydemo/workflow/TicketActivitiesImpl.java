package org.brewman.strategydemo.workflow;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.activity.ActivityInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TicketActivitiesImpl implements TicketActivities {

    // TODO: This is a violation of Temporal guidance.  This should be replaced with a DB.  For now though, just
    // store the TicketEntity here.
    private TicketEntity ticketEntity = null;

    @Override
    public TicketEntity getStoredTicket(String name) {
        return ticketEntity;
    }

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
    public String generateTicketName() {
        log.info("generateTicketName:");
        return "TIK-01234";
    }

    @Override
    public TicketEntity storeTicket(TicketEntity ticketEntity) {
        log.info("storeTicket: {}", ticketEntity);

        // TODO: Store the ticket in the DB.
        this.ticketEntity = ticketEntity;

        return ticketEntity;
    }
}
