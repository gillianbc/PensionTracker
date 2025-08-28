package com.gillianbc.pensionstracker.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record PotDto(
        Long id,
        Long providerId,
        String name,
        String currency,
        String status,
        String notes,
        String planNumber,
        String schemeNumber // optional
) {
    @JsonCreator
    public PotDto(
            @JsonProperty("id") Long id,
            @JsonProperty("provider") Object provider,
            @JsonProperty("providerId") Long providerId,
            @JsonProperty("name") String name,
            @JsonProperty("currency") String currency,
            @JsonProperty("status") String status,
            @JsonProperty("notes") String notes,
            @JsonProperty("planNumber") String planNumber,
            @JsonProperty("schemeNumber") String schemeNumber
    ) {
        this(
            id,
            extractProviderId(provider, providerId),
            name,
            currency,
            status,
            notes,
            planNumber,
            schemeNumber
        );
    }

    private static Long extractProviderId(Object provider, Long providerId) {
        if (providerId != null) return providerId;
        if (provider instanceof java.util.Map<?,?> map) {
            Object idObj = map.get("id");
            if (idObj instanceof Number n) return n.longValue();
            if (idObj instanceof String str) return Long.valueOf(str);
        }
        return null;
    }
}
