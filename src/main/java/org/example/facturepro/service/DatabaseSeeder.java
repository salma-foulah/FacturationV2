package org.example.facturepro.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.facturepro.entity.Article;
import org.example.facturepro.entity.Categorie;
import org.example.facturepro.entity.Settings;
import org.example.facturepro.entity.User;
import org.example.facturepro.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategorieRepository categorieRepository;
    private final ArticleRepository articleRepository;
    private final SettingsRepository settingsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Base de données déjà initialisée, skip du seeding.");
            return;
        }

        log.info("=== Initialisation de la base de données FacturePro ===");

        // --- USERS ---
        User admin = userRepository.save(User.builder()
                .email("admin@facturepro.com")
                .password(passwordEncoder.encode("admin123"))
                .prenom("Admin")
                .nom("FacturePro")
                .role("ADMIN")
                .build());

        User commercial = userRepository.save(User.builder()
                .email("commercial@facturepro.com")
                .password(passwordEncoder.encode("user123"))
                .prenom("Commercial")
                .nom("Test")
                .role("USER")
                .build());

        log.info("Utilisateurs créés: {} | {}", admin.getEmail(), commercial.getEmail());

        // --- CATEGORIES ---
        Categorie electronique = categorieRepository.save(Categorie.builder()
                .nom("Électronique")
                .description("Produits électroniques et informatiques")
                .tva(20.0)
                .build());

        Categorie mobilier = categorieRepository.save(Categorie.builder()
                .nom("Mobilier")
                .description("Meubles et équipements de bureau")
                .tva(20.0)
                .build());

        Categorie service = categorieRepository.save(Categorie.builder()
                .nom("Service")
                .description("Prestations de service et consulting")
                .tva(20.0)
                .build());

        log.info("Catégories créées: Électronique, Mobilier, Service");

        // --- ARTICLES ---
        articleRepository.save(Article.builder()
                .designation("Ordinateur portable Dell XPS 15")
                .reference("DELL-XPS-15")
                .description("Laptop 15 pouces, i7, 16GB RAM, 512GB SSD")
                .prixUnitaire(1299.99)
                .categorie(electronique)
                .build());

        articleRepository.save(Article.builder()
                .designation("Écran 27\" 4K Samsung")
                .reference("SAM-MON-27")
                .description("Moniteur 4K UHD, 60Hz, IPS")
                .prixUnitaire(449.00)
                .categorie(electronique)
                .build());

        articleRepository.save(Article.builder()
                .designation("Bureau ergonomique 160x80")
                .reference("BUR-ERG-160")
                .description("Bureau en bois avec rangements intégrés")
                .prixUnitaire(349.00)
                .categorie(mobilier)
                .build());

        articleRepository.save(Article.builder()
                .designation("Chaise de bureau ergonomique")
                .reference("CHA-ERG-001")
                .description("Chaise avec support lombaire réglable")
                .prixUnitaire(279.00)
                .categorie(mobilier)
                .build());

        articleRepository.save(Article.builder()
                .designation("Consulting IT (journée)")
                .reference("CONS-IT-DAY")
                .description("Prestation de consulting informatique - forfait journée")
                .prixUnitaire(800.00)
                .categorie(service)
                .build());

        articleRepository.save(Article.builder()
                .designation("Formation Spring Boot (5 jours)")
                .reference("FORM-SB-5J")
                .description("Formation complète Spring Boot et microservices")
                .prixUnitaire(2500.00)
                .categorie(service)
                .build());

        log.info("Articles créés: 6 articles dans 3 catégories");

        // --- SETTINGS ---
        settingsRepository.save(Settings.builder()
                .companyName("FacturePro SARL")
                .companyAddress("123 Boulevard Mohammed V, Casablanca 20000, Maroc")
                .companyPhone("+212 5 22 00 00 00")
                .companyEmail("contact@facturepro.com")
                .companySiret("ICE: 001234567000000")
                .defaultTva(20.0)
                .currency("MAD")
                .logoUrl(null)
                .build());

        log.info("Paramètres de l'entreprise initialisés");
        log.info("=== Seeding terminé avec succès ===");
        log.info("  Admin:      admin@facturepro.com      / admin123");
        log.info("  Commercial: commercial@facturepro.com / user123");
    }
}
