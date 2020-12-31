package org.brewman.strategydemo.service;

import ai.applica.spring.boot.starter.temporal.WorkflowFactory;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import lombok.extern.slf4j.Slf4j;
import org.brewman.strategydemo.workflow.TicketEntity;
import org.brewman.strategydemo.workflow.TicketWorkflow;
import org.brewman.strategydemo.workflow.TicketWorkflowImpl;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static org.awaitility.Awaitility.await;

@Component
@Slf4j
public class CreateTicketCommandHandler implements
        ICommandStrategy<CreateTicketCommand, CreateTicketResult, ResourceContainer> {

    private final WorkflowFactory workflowFactory;
    private final TicketNameGenerator ticketNameGenerator;

    public CreateTicketCommandHandler(WorkflowFactory workflowFactory, TicketNameGenerator ticketNameGenerator) {
        this.workflowFactory = workflowFactory;
        this.ticketNameGenerator = ticketNameGenerator;
    }

    @Override
    public CreateTicketResult handle(CreateTicketCommand command) {
        log.info("command: {}",command);

        // TODO: Validate the input.  There is no point in creating the workflow if we are going to reject the request.

        // Generate the ticket name.  The ticket name will also be used as the workflow ID.
        String name = ticketNameGenerator.generateTicketName();

        // Customize the options with the ticket name as the workflow ID.
        WorkflowOptions.Builder builder = workflowFactory
                .defaultOptionsBuilder(TicketWorkflowImpl.class)
                .setWorkflowId(name);

        // Create the stub.
        TicketWorkflow workflow = workflowFactory.makeStub(TicketWorkflow.class, builder);

        // Start the workflow and wait for the ticket to move beyond the NOT_CREATED status.
        WorkflowExecution workflowExecution = WorkflowClient.start(workflow::createTicket, name, "this is a description");
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
