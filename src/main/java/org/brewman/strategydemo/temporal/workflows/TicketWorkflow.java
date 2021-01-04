package org.brewman.strategydemo.temporal.workflows;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;
import org.brewman.strategydemo.domain.TicketEntity;

@WorkflowInterface
public interface TicketWorkflow {

    @WorkflowMethod
    void createTicket(String name, String description);

    @SignalMethod
    void updateTicketStatus(TicketEntity.Status status);

    @QueryMethod
    TicketEntity.Status getTicketStatus();

    @QueryMethod
    TicketEntity getTicket();
}
