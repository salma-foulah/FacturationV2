package org.example.facturepro.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String prenom;

    @Column(unique = true)
    private String email;

    private String telephone;
    private String adresse;

    @Builder.Default
    private Boolean validatedByAdmin = false;

    private LocalDateTime validatedAt;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
