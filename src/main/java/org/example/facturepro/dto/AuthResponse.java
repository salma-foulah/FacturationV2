package org.example.facturepro.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String email;
    private String role;
    private String prenom;
    private String nom;
    private Long id;
}
