// src/main/java/id/ac/ui/cs/advprog/papikosbe/web/dto/TokenResponse.java
package id.ac.ui.cs.advprog.papikosbe.controller.user.dto;

import java.util.UUID;

/** Balasan sukses login */
public class TokenResponse {
    private String token;
    private UUID userId;  // Add userId

    // Constructor
    public TokenResponse(String token, UUID userId) {
        this.token = token;
        this.userId = userId;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}

