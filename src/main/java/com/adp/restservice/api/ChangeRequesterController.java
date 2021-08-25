package com.adp.restservice.api;

import com.adp.restservice.models.Bank;
import com.adp.restservice.models.CoinsExchangedResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Map;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(produces = { HAL_JSON_VALUE, APPLICATION_JSON_VALUE } )
public class ChangeRequesterController extends AbstractResource {

    @Autowired
    private final @NotNull Bank bank;

    private static final @NotNull ChangeRequesterController SELF = methodOn(ChangeRequesterController.class);
    private static final @NotNull Logger logger = LoggerFactory.getLogger(ChangeRequesterController.class);

    public ChangeRequesterController(@NotNull WebMvcLinkBuilderFactory linkBuilderFactory, @NotNull Bank bank) {
        super(linkBuilderFactory, bank);
        this.bank = requireNonNull(bank);
    }

    @GetMapping("/bank")
    public @NotNull ResponseEntity<EntityModel<Bank>> getAvailableCoins() {
        logger.trace("getAvailableCoins::START");
        try {
            return Optional
                    .of(EntityModel.of(this.bank))
                    .map(response -> response.add(linkTo(SELF.getAvailableCoins()).withSelfRel()))
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.badRequest().build())
                    ;
        } finally {
            logger.trace("getAvailableCoins::END");
        }
    }

    @PostMapping("/bank/exchange{bills}")
    public @NotNull ResponseEntity<EntityModel<CoinsExchangedResponse>> exchange(@MatrixVariable Map<String, String> bills) {
        logger.trace("exchange::START");

        var context = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        var links = Links.of(
                linkTo(SELF.getAvailableCoins()).withRel("BankInfo"),
                Link.of(context + "/bank/exchange{;one}{;two}{;five}{;ten}{;twenty}{;fifty}{;hundred}")
        );

        try {
            logger.debug(
                    """
                    Arguments:
                        bills = {}
                    """
                    , bills
            );

            var coins = exchangeBills(bills);


            if (coins == null) {
                return ResponseEntity
                        .status(FORBIDDEN)
                        .body(
                                EntityModel
                                        .of(CoinsExchangedResponse.badRequest("The Bank doesn't have enough coins to fulfil this request. The bank has: $" + this.bank.getTotalValue()))
                                        .add(links)
                        );
            }

            return ResponseEntity
                    .status(OK)
                    .body(
                            EntityModel
                                    .of(CoinsExchangedResponse.success(coins))
                                    .add(links)
                    );

        } catch (Exception e) {
            return ResponseEntity
                    .status(BAD_REQUEST)
                    .body(
                            EntityModel
                                    .of(CoinsExchangedResponse.failed("The request failed with error: " + e.getMessage())).add(linkTo(SELF.exchange(bills)).withSelfRel())
                                    .add(links)
                    );
        } finally {
            logger.trace("exchange::END");
        }
    }
}
