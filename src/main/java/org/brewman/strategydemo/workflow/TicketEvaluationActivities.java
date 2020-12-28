package org.brewman.strategydemo.workflow;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface TicketEvaluationActivities {

    void validateTicketInput(String description);

    String generateTicketNumber();
}
