package com.backend.connectable.kas.service.token.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenIdentifier {
    private String tokenId;
    private String tokenUri;

    public String getTokenUri() {
        return tokenUri;
    }

    public int getTokenId() {
        return Integer.decode(tokenId);
    }
}