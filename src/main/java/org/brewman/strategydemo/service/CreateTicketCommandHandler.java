package org.brewman.strategydemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreateTicketCommandHandler implements
        ICommandStrategy<CreateTicketCommand, CreateTicketResult, ResourceContainer> {

    @Override
    public CreateTicketResult handle(CreateTicketCommand command) {
        log.info("command: {}",command);

        // Do some stuff here to create the ticket.
        // TODO: Replace with call to create Workflow

        return new CreateTicketResult("TIK-01234", command.getDescription());
    }

    @Override
    public ResourceContainer getResource(CreateTicketCommand createTicketCommand) {
        // There is no resource yet since this is the beginning of the workflow.  All ABAC
        // must be based on user attributes only.
        return null;
    }
}
