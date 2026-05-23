package org.example.facturepro.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InvoiceCreateDTO {
    private Long clientId;
    private String calculationMethod; // simple | line | global
    private Double globalRemise;      // % remise (used only in 'global' method)
    private Double tva;               // TVA globale (used in 'simple' method)
    private List<InvoiceItemCreateDTO> items;
}
