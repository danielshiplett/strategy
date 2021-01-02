package org.brewman.strategydemo.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class TicketWorkflowImpl implements TicketWorkflow {

    private TicketEntity ticketEntity = null;

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

    private final TicketActivities activities =
            Workflow.newActivityStub(TicketActivities.class, options);

    @Override
    public void createTicket(String name, String description) {
        log.info("createTicket: {}", description);

        // Steps to create the ticket.  Original caller is waiting patiently.

        // All tickets are created in the NEW status.
        ticketEntity = new TicketEntity(name, TicketEntity.Status.NEW, description);
        ticketEntity = activities.storeTicket(ticketEntity);

        // Here is where I want the original caller to stop waiting and get a response.

        // Then send out notifications about the ticket creations.
        log.info("sending notifications");
        activities.sendNotifications("TICKET_CREATE", ticketEntity);
        log.info("sent notifications");

        // More workflow stuff that can definitely take time.
        log.info("gonna take a while...");
        Workflow.sleep(Duration.ofDays(1));
        log.info("...and we're back");

        // The workflow is finished when the ticket status is either REJECTED or COMPLETE.
        Workflow.await(() -> (ticketEntity.getStatus() == TicketEntity.Status.REJECTED) ||
                (ticketEntity.getStatus() == TicketEntity.Status.COMPLETE));
    }

    @Override
    public void updateTicketStatus(TicketEntity.Status status) {
        log.info("updateTicketStatus: {}", status);

        // Something to get the
    }

    @Override
    public TicketEntity.Status getTicketStatus() {
        log.info("getTicketStatus:");

        if(ticketEntity == null) {
            return TicketEntity.Status.NOT_CREATED;
        }

        return ticketEntity.getStatus();
    }

    @Override
    public TicketEntity getTicket() {
        return ticketEntity;
    }
}
