package org.brewman.strategydemo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GetProtectedDataCommandHandler implements
        ICommandStrategy<GetProtectedDataCommand, GetProtectedDataResult, ResourceContainer> {

    @Override
    public GetProtectedDataResult handle(GetProtectedDataCommand command) {
        log.info("command: {}",command);
        return new GetProtectedDataResult("Hello " + command.getName() + "!");
    }

    @Override
    public ResourceContainer getResource(GetProtectedDataCommand getProtectedDataCommand) {
        return null;
    }
}
