package com.adp.restservice.api;

import com.adp.restservice.models.Bank;
import com.adp.restservice.models.Bill;
import com.adp.restservice.models.Coin;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static com.adp.restservice.models.Coin.*;
import static java.util.Objects.requireNonNull;

public abstract class AbstractResource {

    @Autowired
    private final @NotNull Bank bank;

    protected final @NotNull WebMvcLinkBuilderFactory linkBuilderFactory;

    protected AbstractResource(@NotNull WebMvcLinkBuilderFactory linkBuilderFactory, @NotNull Bank bank) {
        this.linkBuilderFactory = requireNonNull(linkBuilderFactory, "linkBuilderFactory cannot be null.");
        this.bank = requireNonNull(bank, "bank cannot be null.");
    }

    protected @NotNull WebMvcLinkBuilder linkTo(@NotNull Object invocationValue) {
        return this.linkBuilderFactory.linkTo(invocationValue);
    }

    protected Map<Coin, Integer> exchangeBills(Map<String, String> bills) {

        var temp = new Object() {
            Double sum = bills
                    .entrySet()
                    .stream()
                    .map(entry -> Bill.fromString(entry.getKey()).value() * Integer.parseInt(entry.getValue()))
                    .reduce(0.0, Double::sum);
        };

        if (this.bank.getTotalValue() >= temp.sum) {

            var coins = new HashMap<Coin, Integer>();

            Stream.of(QUARTER, DIME, NICKEL, PENNY).forEach(
                    coin -> {
                        int num = (int) (temp.sum / coin.value());

                        num = switch (coin) {
                            case QUARTER -> Math.min(this.bank.getQuarters(), num);
                            case DIME -> Math.min(this.bank.getDimes(), num);
                            case NICKEL -> Math.min(this.bank.getNickels(), num);
                            case PENNY -> Math.min(this.bank.getPennies(), num);
                        };

                        this.bank.subtractCoins(coin, num);
                        coins.put(coin, num);
                        temp.sum -= num * coin.value();
                    }
            );

            return coins;
        }

        return null;
    }

}
