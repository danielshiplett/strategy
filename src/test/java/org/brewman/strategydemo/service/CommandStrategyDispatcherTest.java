package org.brewman.strategydemo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommandStrategyDispatcherTest {

    @Autowired
    private CommandStrategyDispatcher commandStrategyDispatcher;

    @Test
    public void test() {
        CreateTicketCommand createTicketCommand = new CreateTicketCommand("some description");
        CreateTicketResult createTicketResult = commandStrategyDispatcher.dispatch(createTicketCommand);
        Assertions.assertEquals("some description", createTicketResult.getDescription());
        Assertions.assertEquals("TIK-01234", createTicketResult.getName());
    }
}
