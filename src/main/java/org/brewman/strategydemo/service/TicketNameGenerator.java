package org.brewman.strategydemo.service;

import org.springframework.stereotype.Service;

@Service
public class TicketNameGenerator {

    // TODO: Assume that we had a more durable counter.
    private Integer count = 1;

    public String generateTicketName() {
        return String.format("TIK-%05d", count++);
    }
}
