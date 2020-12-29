package org.brewman.strategydemo.workflow;

import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface TicketActivities {

    /**
     * A call to get the TicketEntity from the DB.
     *
     * @param name the name of the TicketEntity
     *
     * @return the TicketEntity or null
     */
    TicketEntity getStoredTicket(String name);

    /**
     * Last chance to validate any user input.
     *
     * @param description the Ticket description
     */
    void validateTicketInput(String description);

    /**
     * Assume the name generator is part of another system.  Get the next Ticket name.
     *
     * @return the new Ticket name
     */
    String generateTicketName();

    /**
     * Store the Ticket in the DB.
     *
     * @param ticketEntity the new or updated ticket
     *
     * @return the ticket
     */
    TicketEntity storeTicket(TicketEntity ticketEntity);
}
