package com.gillianbc.pensionstracker.dto;

public record PotDto(
        Long id,
        String name,
        String currency,
        String status,
        String notes,
        ProviderDto provider
) {}
