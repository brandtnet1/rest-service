package com.adp.restservice.models;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class Bank {

    @Value("${management.datasource.hardcoded.pennies}")
    private int pennies;

    @Value("${management.datasource.hardcoded.nickels}")
    private int nickels;

    @Value("${management.datasource.hardcoded.dimes}")
    private int dimes;

    @Value("${management.datasource.hardcoded.quarters}")
    private int quarters;

    public int getPennies() {
        return this.pennies;
    }

    public int getNickels() {
        return this.nickels;
    }

    public int getDimes() {
        return this.dimes;
    }

    public int getQuarters() {
        return this.quarters;
    }

    public double getTotalValue() { return this.getPennies() * .01 + this.getNickels() * .05 + this.getDimes() * .1 + this.getQuarters() * .25; }

    public void subtractCoins(Coin coin, int num) {
        switch (coin) {
            case QUARTER : this.quarters -= num; break;
            case DIME    : this.dimes    -= num; break;
            case NICKEL  : this.nickels  -= num; break;
            case PENNY   : this.pennies  -= num; break;
        }
    }
}
