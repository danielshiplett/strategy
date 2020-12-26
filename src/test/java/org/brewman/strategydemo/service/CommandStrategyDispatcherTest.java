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
        GetProtectedDataCommand getProtectedDataCommand = new GetProtectedDataCommand("somename");
        GetProtectedDataResult getProtectedDataResult = commandStrategyDispatcher.dispatch(getProtectedDataCommand);
        Assertions.assertEquals("Hello somename!", getProtectedDataResult.getGreeting());
    }
}
