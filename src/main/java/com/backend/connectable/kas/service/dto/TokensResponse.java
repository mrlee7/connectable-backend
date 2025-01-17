package com.backend.connectable.kas.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokensResponse {
    private String cursor;
    private List<TokenResponse> items;

    public List<String> getTokenUris() {
        return items.stream()
            .map(TokenResponse::getTokenUri)
            .collect(Collectors.toList());
    }
}
