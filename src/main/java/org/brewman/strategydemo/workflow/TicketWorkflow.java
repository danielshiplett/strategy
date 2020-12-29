package org.brewman.strategydemo.workflow;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface TicketWorkflow {

    String TASK = "TICKETEVALUATIONTASK";

    @WorkflowMethod
    void createTicket(String description);

    @SignalMethod
    void updateTicketStatus(TicketEntity.Status status);

    @QueryMethod
    TicketEntity.Status getTicketStatus();
}
