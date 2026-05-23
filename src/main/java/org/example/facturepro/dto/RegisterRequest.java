package org.example.facturepro.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterRequest {
    private String email;
    private String password;
    private String prenom;
    private String nom;
    private String role; // ADMIN or USER (default USER if not provided)
}
