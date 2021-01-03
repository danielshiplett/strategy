package org.brewman.strategydemo.service;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brewman.strategydemo.domain.TicketEntity;
import org.brewman.strategydemo.temporal.workflows.TicketWorkflow;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static org.awaitility.Awaitility.await;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateTicketCommandHandler implements
        ICommandStrategy<CreateTicketCommand, CreateTicketResult, ResourceContainer> {

    private final TicketNameGenerator ticketNameGenerator;
    private final WorkflowClient workflowClient;

    @Override
    public CreateTicketResult handle(CreateTicketCommand command) {
        log.info("command: {}",command);

        // TODO: Validate the input.  There is no point in creating the workflow if we are going to reject the request.

        // Generate the ticket name.  The ticket name will also be used as the workflow ID.
        String name = ticketNameGenerator.generateTicketName();

        // Customize the options with the ticket name as the workflow ID.
        // TODO: The basic options should be created elsewhere because we don't want the caller having to know
        // the task name and such.
        WorkflowOptions workflowOptions = WorkflowOptions.newBuilder().setWorkflowId(name).setTaskQueue(TicketWorkflow.TASK).build();
        TicketWorkflow workflow = workflowClient.newWorkflowStub(TicketWorkflow.class, workflowOptions);

        WorkflowExecution workflowExecution = WorkflowClient.start(workflow::createTicket, name, command.getDescription());
        TicketEntity.Status status = await()
                .atMost(Duration.ofSeconds(10))
                .until(workflow::getTicketStatus, s -> s != TicketEntity.Status.NOT_CREATED);

        if(status == TicketEntity.Status.NOT_CREATED) {
            // TODO: Throw a better exception here.  This is a system problem.  The user did nothing wrong and we
            // have a workflow that we can maybe fix.
            throw new RuntimeException("something bad happened but rest assured it wasn't you and your ticket is created");
        }

        TicketEntity ticketEntity = workflow.getTicket();

        return new CreateTicketResult(ticketEntity.getName(), ticketEntity.getStatus().name(), ticketEntity.getDescription());
    }

    @Override
    public ResourceContainer getResource(CreateTicketCommand createTicketCommand) {
        // There is no resource yet since this is the beginning of the workflow.  All ABAC
        // must be based on user attributes only.
        return null;
    }
}
