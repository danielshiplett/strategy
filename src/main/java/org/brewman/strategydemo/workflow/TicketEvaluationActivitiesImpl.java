package org.brewman.strategydemo.workflow;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TicketEvaluationActivitiesImpl implements TicketEvaluationActivities {

    @Override
    public void validateTicketInput(String description) {
        log.info("validateTicketInput: {}", description);
        // Does nothing if the input is OK.
    }

    @Override
    public String generateTicketNumber() {
        log.info("generateTicketNumber:");
        return "TIK-01234";
    }
}
