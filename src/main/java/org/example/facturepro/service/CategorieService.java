package org.example.facturepro.service;

import lombok.RequiredArgsConstructor;
import org.example.facturepro.entity.Categorie;
import org.example.facturepro.repository.CategorieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategorieService {

    private final CategorieRepository categorieRepository;

    public List<Categorie> findAll() {
        return categorieRepository.findAll();
    }

    public Categorie findById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée: " + id));
    }

    public Categorie create(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    public Categorie update(Long id, Categorie updated) {
        Categorie existing = findById(id);
        existing.setNom(updated.getNom());
        existing.setDescription(updated.getDescription());
        existing.setTva(updated.getTva());
        return categorieRepository.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        categorieRepository.deleteById(id);
    }
}
