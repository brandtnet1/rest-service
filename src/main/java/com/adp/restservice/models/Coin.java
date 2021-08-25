package com.adp.restservice.models;

public enum Coin implements Currency {

    PENNY(0.01),
    NICKEL(0.05),
    DIME(0.1),
    QUARTER(0.25);

    private final double denomination;

    Coin(double denomination) {
        this.denomination = denomination;
    }

    @Override
    public double value() {
        return this.denomination;
    }
}
