package org.example.facturepro.service;

import lombok.RequiredArgsConstructor;
import org.example.facturepro.entity.Article;
import org.example.facturepro.entity.Categorie;
import org.example.facturepro.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CategorieService categorieService;

    public List<Article> findAll() {
        return articleRepository.findAll();
    }

    public List<Article> findByCategorie(Long categorieId) {
        return articleRepository.findByCategorieId(categorieId);
    }

    public Article findById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article non trouvé: " + id));
    }

    public Article create(Article article, Long categorieId) {
        if (categorieId != null) {
            Categorie categorie = categorieService.findById(categorieId);
            article.setCategorie(categorie);
        }
        return articleRepository.save(article);
    }

    public Article update(Long id, Article updated, Long categorieId) {
        Article existing = findById(id);
        existing.setDesignation(updated.getDesignation());
        existing.setReference(updated.getReference());
        existing.setDescription(updated.getDescription());
        existing.setPrixUnitaire(updated.getPrixUnitaire());
        if (categorieId != null) {
            existing.setCategorie(categorieService.findById(categorieId));
        }
        return articleRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        articleRepository.deleteById(id);
    }
}
