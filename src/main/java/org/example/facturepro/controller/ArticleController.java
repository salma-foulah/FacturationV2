package org.example.facturepro.controller;

import lombok.RequiredArgsConstructor;
import org.example.facturepro.entity.Article;
import org.example.facturepro.service.ArticleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<Article>> getAll(
            @RequestParam(required = false) Long categorieId) {
        if (categorieId != null) {
            return ResponseEntity.ok(articleService.findByCategorie(categorieId));
        }
        return ResponseEntity.ok(articleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Article> create(@RequestBody Map<String, Object> body) {
        Article article = new Article();
        article.setDesignation((String) body.get("designation"));
        article.setReference((String) body.get("reference"));
        article.setDescription((String) body.get("description"));
        article.setPrixUnitaire(body.get("prixUnitaire") != null
                ? Double.parseDouble(body.get("prixUnitaire").toString()) : null);
        Long categorieId = body.get("categorieId") != null
                ? Long.parseLong(body.get("categorieId").toString()) : null;
        return ResponseEntity.ok(articleService.create(article, categorieId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Article> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Article article = new Article();
        article.setDesignation((String) body.get("designation"));
        article.setReference((String) body.get("reference"));
        article.setDescription((String) body.get("description"));
        article.setPrixUnitaire(body.get("prixUnitaire") != null
                ? Double.parseDouble(body.get("prixUnitaire").toString()) : null);
        Long categorieId = body.get("categorieId") != null
                ? Long.parseLong(body.get("categorieId").toString()) : null;
        return ResponseEntity.ok(articleService.update(id, article, categorieId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
