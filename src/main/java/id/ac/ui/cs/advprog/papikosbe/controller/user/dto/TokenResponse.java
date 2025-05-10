// src/main/java/id/ac/ui/cs/advprog/papikosbe/web/dto/TokenResponse.java
package id.ac.ui.cs.advprog.papikosbe.controller.user.dto;

import java.util.UUID;

/** Balasan sukses login */
public record TokenResponse(String token, UUID userId, String role) {}
