package org.example.facturepro.service;

import lombok.RequiredArgsConstructor;
import org.example.facturepro.dto.DashboardStatsDTO;
import org.example.facturepro.dto.InvoiceCreateDTO;
import org.example.facturepro.dto.InvoiceItemCreateDTO;
import org.example.facturepro.entity.*;
import org.example.facturepro.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientService clientService;
    private final ArticleService articleService;

    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> findByUser(Long userId) {
        return invoiceRepository.findByUserId(userId);
    }

    public Invoice findById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée: " + id));
    }

    @Transactional
    public Invoice create(InvoiceCreateDTO dto, User currentUser) {
        Client client = clientService.findById(dto.getClientId());

        if (!client.getValidatedByAdmin()) {
            throw new RuntimeException("Le client n'est pas encore validé par un administrateur.");
        }

        Invoice invoice = Invoice.builder()
                .client(client)
                .user(currentUser)
                .calculationMethod(dto.getCalculationMethod() != null ? dto.getCalculationMethod() : "line")
                .globalRemise(dto.getGlobalRemise() != null ? dto.getGlobalRemise() : 0.0)
                .tva(dto.getTva())
                .dateCreation(LocalDateTime.now())
                .statut("En attente")
                .build();

        // Build items
        List<InvoiceItem> items = new ArrayList<>();
        for (InvoiceItemCreateDTO itemDto : dto.getItems()) {
            Article article = articleService.findById(itemDto.getArticleId());
            InvoiceItem item = InvoiceItem.builder()
                    .article(article)
                    .designation(itemDto.getDesignation() != null ? itemDto.getDesignation() : article.getDesignation())
                    .quantity(itemDto.getQuantity())
                    .prixUnitaire(itemDto.getPrixUnitaire() != null ? itemDto.getPrixUnitaire() : article.getPrixUnitaire())
                    .remise(itemDto.getRemise() != null ? itemDto.getRemise() : 0.0)
                    .tva(itemDto.getTva() != null ? itemDto.getTva()
                            : (article.getCategorie() != null ? article.getCategorie().getTva() : 20.0))
                    .invoice(invoice)
                    .build();
            items.add(item);
        }
        invoice.setItems(items);

        // Calculate totals based on method
        calculateTotals(invoice);

        // Generate sequential invoice number: F-2024-06-0001
        String numero = generateInvoiceNumber();
        invoice.setNumero(numero);

        return invoiceRepository.save(invoice);
    }

    /**
     * THREE CALCULATION METHODS:
     *
     * SIMPLE: totalBrutHt = SUM(qty * prixUnitaire)
     *         totalHt = totalBrutHt (no line discounts)
     *         TVA from invoice-level tva field
     *         totalTtc = totalHt * (1 + tva/100)
     *
     * LINE:   Each line: ligneHt = qty * pu * (1 - remise/100)
     *         totalHt = SUM(ligneHt)
     *         TVA per line (from item.tva)
     *         totalTtc = SUM(ligneHt * (1 + item.tva/100))
     *
     * GLOBAL: totalBrutHt = SUM(qty * pu)
     *         totalHt = totalBrutHt * (1 - globalRemise/100)
     *         TVA from items (weighted average or first item's)
     *         totalTtc = totalHt * (1 + avgTva/100)
     */
    private void calculateTotals(Invoice invoice) {
        String method = invoice.getCalculationMethod();
        List<InvoiceItem> items = invoice.getItems();

        switch (method) {
            case "simple" -> calculateSimple(invoice, items);
            case "global" -> calculateGlobal(invoice, items);
            default -> calculateLine(invoice, items); // "line" is default
        }
    }

    private void calculateSimple(Invoice invoice, List<InvoiceItem> items) {
        double totalBrutHt = items.stream()
                .mapToDouble(i -> i.getQuantity() * i.getPrixUnitaire())
                .sum();
        double tvaPct = invoice.getTva() != null ? invoice.getTva() : 20.0;
        invoice.setTotalBrutHt(round(totalBrutHt));
        invoice.setTotalHt(round(totalBrutHt));
        invoice.setTva(tvaPct);
        invoice.setTotalTtc(round(totalBrutHt * (1 + tvaPct / 100)));
    }

    private void calculateLine(Invoice invoice, List<InvoiceItem> items) {
        double totalHt = 0;
        double totalTtc = 0;
        double totalBrutHt = 0;
        for (InvoiceItem item : items) {
            double brutHt = item.getQuantity() * item.getPrixUnitaire();
            double ligneHt = brutHt * (1 - (item.getRemise() != null ? item.getRemise() : 0.0) / 100);
            double ligneTtc = ligneHt * (1 + item.getTva() / 100);
            totalBrutHt += brutHt;
            totalHt += ligneHt;
            totalTtc += ligneTtc;
        }
        double avgTva = totalHt > 0 ? ((totalTtc - totalHt) / totalHt) * 100 : 20.0;
        invoice.setTotalBrutHt(round(totalBrutHt));
        invoice.setTotalHt(round(totalHt));
        invoice.setTva(round(avgTva));
        invoice.setTotalTtc(round(totalTtc));
    }

    private void calculateGlobal(Invoice invoice, List<InvoiceItem> items) {
        double totalBrutHt = items.stream()
                .mapToDouble(i -> i.getQuantity() * i.getPrixUnitaire())
                .sum();
        double remise = invoice.getGlobalRemise() != null ? invoice.getGlobalRemise() : 0.0;
        double totalHt = totalBrutHt * (1 - remise / 100);

        // Use average TVA across items
        double avgTva = items.stream()
                .mapToDouble(i -> i.getTva() != null ? i.getTva() : 20.0)
                .average().orElse(20.0);

        invoice.setTotalBrutHt(round(totalBrutHt));
        invoice.setTotalHt(round(totalHt));
        invoice.setTva(round(avgTva));
        invoice.setTotalTtc(round(totalHt * (1 + avgTva / 100)));
    }

    private String generateInvoiceNumber() {
        LocalDateTime now = LocalDateTime.now();
        String prefix = "F-" + now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        long count = invoiceRepository.count() + 1;
        return String.format("%s-%04d", prefix, count);
    }

    public Invoice approve(Long id, String adminEmail) {
        Invoice invoice = findById(id);
        invoice.setValidatedByAdmin(true);
        invoice.setValidatedByAdminId(adminEmail);
        invoice.setValidatedAt(LocalDateTime.now());
        invoice.setAdminDecision("APPROVED");
        invoice.setStatut("Payée");
        invoice.setUpdatedAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    public Invoice reject(Long id, String adminEmail, String reason) {
        Invoice invoice = findById(id);
        invoice.setValidatedByAdmin(false);
        invoice.setValidatedByAdminId(adminEmail);
        invoice.setValidatedAt(LocalDateTime.now());
        invoice.setAdminDecision("REJECTED");
        invoice.setRejectionReason(reason);
        invoice.setStatut("Rejetée");
        invoice.setUpdatedAt(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }

    public DashboardStatsDTO getDashboardStats(ClientService clientService) {
        return DashboardStatsDTO.builder()
                .totalInvoices(invoiceRepository.count())
                .totalHt(invoiceRepository.sumAllTotalHt())
                .totalTtc(invoiceRepository.sumAllTotalTtc())
                // En attente
                .invoicesEnAttente(invoiceRepository.countByStatut("En attente"))
                .totalHtEnAttente(invoiceRepository.sumTotalHtByStatut("En attente"))
                .totalTtcEnAttente(invoiceRepository.sumTotalTtcByStatut("En attente"))
                // Payées
                .invoicesPayees(invoiceRepository.countByStatut("Payée"))
                .totalHtPayees(invoiceRepository.sumTotalHtByStatut("Payée"))
                .totalTtcPayees(invoiceRepository.sumTotalTtcByStatut("Payée"))
                // Rejetées
                .invoicesRejetees(invoiceRepository.countByStatut("Rejetée"))
                .totalHtRejetees(invoiceRepository.sumTotalHtByStatut("Rejetée"))
                .totalTtcRejetees(invoiceRepository.sumTotalTtcByStatut("Rejetée"))
                // Clients
                .totalClients(clientService.countAll())
                .clientsEnAttente(clientService.countPending())
                .clientsValides(clientService.countValidated())
                .build();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
