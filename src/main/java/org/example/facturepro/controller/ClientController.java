package org.example.facturepro.controller;

import lombok.RequiredArgsConstructor;
import org.example.facturepro.entity.Client;
import org.example.facturepro.entity.User;
import org.example.facturepro.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<Client>> getAll(
            @RequestParam(required = false) Boolean validated) {
        if (validated != null) {
            return ResponseEntity.ok(validated
                    ? clientService.findValidated()
                    : clientService.findPending());
        }
        return ResponseEntity.ok(clientService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Client> create(@RequestBody Client client,
                                          @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(clientService.create(client, currentUser));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable Long id, @RequestBody Client client) {
        return ResponseEntity.ok(clientService.update(id, client));
    }

    @PutMapping("/{id}/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Client> validate(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.validate(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
