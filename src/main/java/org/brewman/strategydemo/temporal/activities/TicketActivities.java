package org.brewman.strategydemo.temporal.activities;

import io.temporal.activity.ActivityInterface;
import org.brewman.strategydemo.domain.TicketEntity;

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
     * Send out notifications to the correct users about the event.
     *
     * @param event the type of the event that just occurred
     *
     * @param ticketEntity the new or updated ticket
     */
    void sendNotifications(String event, TicketEntity ticketEntity);
}
