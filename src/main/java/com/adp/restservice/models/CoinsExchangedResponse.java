package com.adp.restservice.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record CoinsExchangedResponse(
        @JsonSerialize Boolean success,
        @JsonSerialize String message,
        @JsonSerialize Map<Coin, Integer> coins
) {
    public static @NotNull CoinsExchangedResponse badRequest(String message) {
        return new CoinsExchangedResponse(false, message, null);
    }

    public static @NotNull CoinsExchangedResponse failed(String message) {
        return new CoinsExchangedResponse(false, message, null);
    }

    public static @NotNull CoinsExchangedResponse success(Map<Coin, Integer> coins) {
        return new CoinsExchangedResponse(true, "success", coins);
    }
}
