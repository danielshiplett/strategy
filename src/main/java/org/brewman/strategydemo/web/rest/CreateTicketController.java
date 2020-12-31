package org.brewman.strategydemo.web.rest;

import org.brewman.strategydemo.service.CommandStrategyDispatcher;
import org.brewman.strategydemo.service.CreateTicketCommand;
import org.brewman.strategydemo.service.CreateTicketResult;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class CreateTicketController {

    private final CommandStrategyDispatcher commandStrategyDispatcher;

    public CreateTicketController(CommandStrategyDispatcher commandStrategyDispatcher) {
        this.commandStrategyDispatcher = commandStrategyDispatcher;
    }

    @PostMapping("/api/v1/create-ticket")
    public ResponseEntity<CreateTicketResponseDto> createTicket(@RequestBody CreateTicketRequestDto createTicketRequestDto) {
        CreateTicketCommand createTicketCommand = new CreateTicketCommand(createTicketRequestDto.getDescription());

        CreateTicketResult createTicketResult = commandStrategyDispatcher.dispatch(createTicketCommand);

        return ResponseEntity.ok(
                new CreateTicketResponseDto(
                        createTicketResult.getName(),
                        createTicketResult.getStatus(),
                        createTicketResult.getDescription()));
    }
}
