package org.example.facturepro.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String numero; // ex: F-2024-01-0001

    private LocalDateTime dateCreation;

    @Builder.Default
    private String statut = "En attente"; // En attente, Payée, Rejetée

    private Double totalHt;
    private Double totalBrutHt;  // before remise
    private Double tva;
    private Double totalTtc;
    private Double globalRemise; // % remise globale (for 'global' method)

    @Column(nullable = false)
    @Builder.Default
    private String calculationMethod = "line"; // simple | line | global

    @Builder.Default
    private Boolean validatedByAdmin = false;

    private String validatedByAdminId;
    private LocalDateTime validatedAt;
    private String adminDecision; // APPROVED | REJECTED
    private String rejectionReason;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnoreProperties("invoice")
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();
}
