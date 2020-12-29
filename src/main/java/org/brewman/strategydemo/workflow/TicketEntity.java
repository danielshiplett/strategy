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
        NEW,
        PENDING_REVIEW,
        REVIEWED,
        APPROVED,
        REJECTED,
        COMPLETE
    }

    private String name;
    private Status status;
    private String description;
}
