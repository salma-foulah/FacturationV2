package org.example.facturepro.controller;

import lombok.RequiredArgsConstructor;
import org.example.facturepro.dto.DashboardStatsDTO;
import org.example.facturepro.dto.InvoiceCreateDTO;
import org.example.facturepro.entity.Invoice;
import org.example.facturepro.entity.User;
import org.example.facturepro.service.ClientService;
import org.example.facturepro.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<Invoice>> getAll(@AuthenticationPrincipal User currentUser) {
        // ADMIN sees all invoices, USER sees only their own
        if ("ADMIN".equals(currentUser.getRole())) {
            return ResponseEntity.ok(invoiceService.findAll());
        }
        return ResponseEntity.ok(invoiceService.findByUser(currentUser.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Invoice> create(@RequestBody InvoiceCreateDTO dto,
                                           @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(invoiceService.create(dto, currentUser));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Invoice> approve(@PathVariable Long id,
                                            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(invoiceService.approve(id, currentUser.getEmail()));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Invoice> reject(@PathVariable Long id,
                                           @AuthenticationPrincipal User currentUser,
                                           @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "Aucune raison spécifiée");
        return ResponseEntity.ok(invoiceService.reject(id, currentUser.getEmail(), reason));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(invoiceService.getDashboardStats(clientService));
    }
}
