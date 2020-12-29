package org.brewman.strategydemo.workflow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {

    public static enum Status {
        NOT_CREATED,    // Needed for status checks prior to the creation.  Never set a ticket to this.
        NEW,            // Will only be new for a brief moment until the workflow has completed initial notifications
        PENDING_REVIEW, // Waiting on the local reviewer to assess
        REVIEWED,       // Waiting on the compliance officer to assess
        APPROVED,       // Move to the next phase
        REJECTED,       // End state
        COMPLETE        // End state
    }

    private String name;
    private Status status;
    private String description;
}
