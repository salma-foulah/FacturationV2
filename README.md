# FacturePro - Backend Spring Boot

API REST complète pour la gestion de factures : authentification JWT, CRUD complet, 3 méthodes de calcul, workflow de validation.

## Démarrage rapide

```powershell
# 1. Copier ce dossier dans votre projet IntelliJ existant
# 2. Lancer l'application
./mvnw spring-boot:run

# 3. Vérifier via script PowerShell
./verify.ps1
```

L'application démarre sur **http://localhost:8080**  
Console H2 : **http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:facturepro`)

---

## Comptes par défaut (créés automatiquement)

| Email | Mot de passe | Rôle |
|-------|-------------|------|
| admin@facturepro.com | admin123 | ADMIN |
| commercial@facturepro.com | user123 | USER |

---

## Endpoints API

### Authentification (public)
| Méthode | URL | Description |
|---------|-----|-------------|
| POST | `/api/auth/login` | Connexion → retourne JWT |
| POST | `/api/auth/register` | Inscription |

### Catégories
| Méthode | URL | Rôle requis |
|---------|-----|-------------|
| GET | `/api/categories` | Authentifié |
| GET | `/api/categories/{id}` | Authentifié |
| POST | `/api/categories` | ADMIN |
| PUT | `/api/categories/{id}` | ADMIN |
| DELETE | `/api/categories/{id}` | ADMIN |

### Articles
| Méthode | URL | Rôle requis |
|---------|-----|-------------|
| GET | `/api/articles` | Authentifié |
| GET | `/api/articles?categorieId=1` | Authentifié |
| POST | `/api/articles` | ADMIN |
| PUT | `/api/articles/{id}` | ADMIN |
| DELETE | `/api/articles/{id}` | ADMIN |

### Clients
| Méthode | URL | Rôle requis |
|---------|-----|-------------|
| GET | `/api/clients` | Authentifié |
| GET | `/api/clients?validated=false` | Authentifié |
| POST | `/api/clients` | Authentifié (USER → en attente, ADMIN → auto-validé) |
| PUT | `/api/clients/{id}` | Authentifié |
| PUT | `/api/clients/{id}/validate` | ADMIN |
| DELETE | `/api/clients/{id}` | ADMIN |

### Factures
| Méthode | URL | Rôle requis |
|---------|-----|-------------|
| GET | `/api/invoices` | Authentifié (ADMIN = tout, USER = les siennes) |
| GET | `/api/invoices/{id}` | Authentifié |
| POST | `/api/invoices` | Authentifié |
| PUT | `/api/invoices/{id}/approve` | ADMIN |
| PUT | `/api/invoices/{id}/reject` | ADMIN |
| GET | `/api/invoices/stats` | ADMIN |

### Paramètres
| Méthode | URL | Rôle requis |
|---------|-----|-------------|
| GET | `/api/settings` | Public |
| PUT | `/api/settings` | ADMIN |

---

## Méthodes de calcul des factures

### `simple`
```json
{ "calculationMethod": "simple", "tva": 20, "items": [...] }
```
- `totalBrutHt` = Σ(qté × prix)
- `totalHt` = `totalBrutHt` (pas de remise)
- `totalTtc` = `totalHt` × (1 + TVA/100)

### `line` *(défaut)*
```json
{ "calculationMethod": "line", "items": [{ ..., "remise": 10, "tva": 20 }] }
```
- Par ligne : `ligneHt` = qté × prix × (1 - remise/100)
- `totalHt` = Σ(`ligneHt`)
- `totalTtc` = Σ(`ligneHt` × (1 + tva_ligne/100))

### `global`
```json
{ "calculationMethod": "global", "globalRemise": 15, "items": [...] }
```
- `totalBrutHt` = Σ(qté × prix)
- `totalHt` = `totalBrutHt` × (1 - globalRemise/100)
- `totalTtc` = `totalHt` × (1 + TVA_moyenne/100)

---

## Exemple de création de facture

```json
POST /api/invoices
Authorization: Bearer <token>

{
  "clientId": 1,
  "calculationMethod": "line",
  "items": [
    { "articleId": 1, "quantity": 2, "remise": 10, "tva": 20 },
    { "articleId": 2, "quantity": 1, "remise": 0,  "tva": 20 }
  ]
}
```

---

## Passer en PostgreSQL

Dans `application.properties`, commenter la section H2 et décommenter PostgreSQL :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/facturepro
spring.datasource.username=postgres
spring.datasource.password=yourpassword
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

---

## Structure du projet

```
src/main/java/org/example/facturepro/
├── FactureProApplication.java
├── entity/          User, Categorie, Article, Client, Invoice, InvoiceItem, Settings
├── repository/      JpaRepositories
├── dto/             AuthRequest/Response, RegisterRequest, InvoiceCreateDTO, DashboardStatsDTO
├── security/        JwtTokenProvider, JwtFilter, SecurityConfig
├── service/         AuthService, CategorieService, ArticleService, ClientService,
│                    InvoiceService, SettingsService, DatabaseSeeder
└── controller/      AuthController, CategorieController, ArticleController,
                     ClientController, InvoiceController, SettingsController
```
