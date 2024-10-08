package org.example.entity.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Details about currency rate response")
public record RateResponse(@Schema(description = "Currency code", example = "USD") String currency,
                           @Schema(description = "Currency rate", example = "93.04") Double amount) {
}
