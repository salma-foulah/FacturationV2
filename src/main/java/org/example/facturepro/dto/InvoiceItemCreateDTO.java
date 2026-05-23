package org.example.facturepro.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InvoiceItemCreateDTO {
    private Long articleId;
    private String designation; // optional override
    private Integer quantity;
    private Double prixUnitaire; // optional override
    private Double remise;       // % remise (used in 'line' method)
    private Double tva;          // % TVA (override article categorie TVA)
}
