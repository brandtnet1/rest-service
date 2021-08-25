package com.adp.restservice.models;

public enum Bill implements Currency {

    ONE(1),
    TWO(2),
    FIVE(5),
    TEN(10),
    TWENTY(20),
    FIFTY(50),
    HUNDRED(100);

    private final double denomination;

    Bill(double denomination) {
        this.denomination = denomination;
    }

    public static Bill fromString(String text) {
        for (Bill b : Bill.values()) {
            if (b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    @Override
    public double value() {
        return this.denomination;
    }
}
