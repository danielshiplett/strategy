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
     * Store the Ticket in the DB.
     *
     * @param ticketEntity the new or updated ticket
     *
     * @return the ticket
     */
    TicketEntity storeTicket(TicketEntity ticketEntity);

    /**
     * Send all of the notifications required at the beginning of the workflow.
     *
     * @param ticketEntity the new ticket
     */
    void sendStartTicketNotifications(TicketEntity ticketEntity);
}
