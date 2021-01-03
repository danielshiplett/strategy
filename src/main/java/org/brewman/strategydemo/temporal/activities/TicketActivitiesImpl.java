package org.brewman.strategydemo.temporal.activities;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.activity.ActivityInfo;
import lombok.extern.slf4j.Slf4j;
import org.brewman.strategydemo.domain.TicketEntity;

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
    public TicketEntity storeTicket(TicketEntity ticketEntity) {
        log.info("storeTicket: {}", ticketEntity);

        // Just to see what is here.
        ActivityExecutionContext ctx = Activity.getExecutionContext();
        ActivityInfo info = ctx.getInfo();
        log.info("namespace=" +  info.getActivityNamespace());
        log.info("workflowId=" + info.getWorkflowId());
        log.info("runId=" + info.getRunId());
        log.info("activityId=" + info.getActivityId());

        // TODO: Store the ticket in the DB.
        this.ticketEntity = ticketEntity;

        return ticketEntity;
    }

    @Override
    public void sendNotifications(String event, TicketEntity ticketEntity) {
        log.info("sendNotifications: {}", event);

        // Intentionally doing nothing for now.
    }
}
