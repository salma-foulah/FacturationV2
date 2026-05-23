package org.example.facturepro.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthRequest {
    private String email;
    private String password;
}
