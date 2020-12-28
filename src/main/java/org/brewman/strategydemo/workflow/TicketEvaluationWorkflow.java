package org.brewman.strategydemo.workflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface TicketEvaluationWorkflow {

    String TASK = "TICKETEVALUATIONTASK";

    @WorkflowMethod
    void createTicket(String description);
}
