package org.example.facturepro.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardStatsDTO {
    private Long totalInvoices;
    private Double totalHt;
    private Double totalTtc;

    // By status
    private Long invoicesEnAttente;
    private Double totalHtEnAttente;
    private Double totalTtcEnAttente;

    private Long invoicesPayees;
    private Double totalHtPayees;
    private Double totalTtcPayees;

    private Long invoicesRejetees;
    private Double totalHtRejetees;
    private Double totalTtcRejetees;

    // Clients
    private Long totalClients;
    private Long clientsEnAttente;
    private Long clientsValides;
}
