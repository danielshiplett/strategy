package org.brewman.strategydemo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class CommandStrategyDispatcherTest {

    @Autowired
    private CommandStrategyDispatcher commandStrategyDispatcher;

    @MockBean
    private TicketNameGenerator ticketNameGenerator;

    @Test
    public void test() {
        Mockito.doReturn("TIK-01234").when(ticketNameGenerator).generateTicketName();

        CreateTicketCommand createTicketCommand = new CreateTicketCommand("some description");
        CreateTicketResult createTicketResult = commandStrategyDispatcher.dispatch(createTicketCommand);
        Assertions.assertEquals("some description", createTicketResult.getDescription());
        Assertions.assertEquals("TIK-01234", createTicketResult.getName());
    }
}
