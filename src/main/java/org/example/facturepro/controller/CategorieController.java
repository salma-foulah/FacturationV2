package org.example.facturepro.controller;

import lombok.RequiredArgsConstructor;
import org.example.facturepro.entity.Categorie;
import org.example.facturepro.service.CategorieService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    @GetMapping
    public ResponseEntity<List<Categorie>> getAll() {
        return ResponseEntity.ok(categorieService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categorie> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categorieService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categorie> create(@RequestBody Categorie categorie) {
        return ResponseEntity.ok(categorieService.create(categorie));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Categorie> update(@PathVariable Long id, @RequestBody Categorie categorie) {
        return ResponseEntity.ok(categorieService.update(id, categorie));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categorieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
