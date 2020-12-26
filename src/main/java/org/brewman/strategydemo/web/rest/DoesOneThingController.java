package org.brewman.strategydemo.web.rest;

import org.brewman.strategydemo.service.CommandStrategyDispatcher;
import org.brewman.strategydemo.service.GetProtectedDataCommand;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class DoesOneThingController {

    private final CommandStrategyDispatcher commandStrategyDispatcher;

    public DoesOneThingController(CommandStrategyDispatcher commandStrategyDispatcher) {
        this.commandStrategyDispatcher = commandStrategyDispatcher;
    }

    @GetMapping("/api/v1/doesOneThing/{name}")
    public void doesOneThing(@PathVariable("name") String name) {
        GetProtectedDataCommand getProtectedDataCommand = new GetProtectedDataCommand(name);
        commandStrategyDispatcher.dispatch(getProtectedDataCommand);
    }
}
