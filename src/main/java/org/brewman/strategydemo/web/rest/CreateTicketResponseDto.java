package org.brewman.strategydemo.web.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTicketResponseDto {

    private String name;
    private String status;
    private String description;
}
