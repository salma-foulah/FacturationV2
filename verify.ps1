# ============================================================
# FacturePro - Script de vérification PowerShell
# Lancer après: ./mvnw spring-boot:run
# ============================================================

$BASE = "http://localhost:8080/api"
$headers = @{ "Content-Type" = "application/json" }

function Log($msg) { Write-Host "`n=== $msg ===" -ForegroundColor Cyan }
function Ok($msg)  { Write-Host "[OK] $msg"    -ForegroundColor Green }
function Err($msg) { Write-Host "[ERR] $msg"   -ForegroundColor Red }

# ---------------------------------------------------------------
# 1. Login Admin
# ---------------------------------------------------------------
Log "1. Login ADMIN"
$adminLogin = Invoke-RestMethod -Uri "$BASE/auth/login" -Method Post -Headers $headers -Body '{"email":"admin@facturepro.com","password":"admin123"}'
$adminToken = $adminLogin.token
Ok "Token admin obtenu: $($adminToken.Substring(0,40))..."
$adminHeaders = @{ "Content-Type" = "application/json"; "Authorization" = "Bearer $adminToken" }

# ---------------------------------------------------------------
# 2. Login Commercial (USER)
# ---------------------------------------------------------------
Log "2. Login COMMERCIAL (USER)"
$userLogin = Invoke-RestMethod -Uri "$BASE/auth/login" -Method Post -Headers $headers -Body '{"email":"commercial@facturepro.com","password":"user123"}'
$userToken = $userLogin.token
Ok "Token user obtenu: $($userToken.Substring(0,40))..."
$userHeaders = @{ "Content-Type" = "application/json"; "Authorization" = "Bearer $userToken" }

# ---------------------------------------------------------------
# 3. Vérification catégories et articles
# ---------------------------------------------------------------
Log "3. Catégories et Articles"
$cats = Invoke-RestMethod -Uri "$BASE/categories" -Method Get -Headers $userHeaders
Ok "Catégories trouvées: $($cats.Count)"
$cats | ForEach-Object { Write-Host "  - $($_.nom) (TVA: $($_.tva)%)" }

$articles = Invoke-RestMethod -Uri "$BASE/articles" -Method Get -Headers $userHeaders
Ok "Articles trouvés: $($articles.Count)"

# ---------------------------------------------------------------
# 4. Settings (public)
# ---------------------------------------------------------------
Log "4. Settings (endpoint public)"
$settings = Invoke-RestMethod -Uri "$BASE/settings" -Method Get
Ok "Entreprise: $($settings.companyName) | Devise: $($settings.currency)"

# ---------------------------------------------------------------
# 5. Créer un client (USER - pending validation)
# ---------------------------------------------------------------
Log "5. Créer un client en tant que USER"
$clientBody = '{"nom":"Dupont","prenom":"Jean","email":"jean.dupont@example.com","telephone":"+212 6 00 00 00 01","adresse":"45 Rue Hassan II, Rabat"}'
$client = Invoke-RestMethod -Uri "$BASE/clients" -Method Post -Headers $userHeaders -Body $clientBody
Ok "Client créé: $($client.nom) $($client.prenom) | Validé: $($client.validatedByAdmin)"

# ---------------------------------------------------------------
# 6. Valider le client (ADMIN)
# ---------------------------------------------------------------
Log "6. Valider le client en tant que ADMIN"
$validated = Invoke-RestMethod -Uri "$BASE/clients/$($client.id)/validate" -Method Put -Headers $adminHeaders
Ok "Client validé à: $($validated.validatedAt)"

# ---------------------------------------------------------------
# 7. Créer une facture (method=line)
# ---------------------------------------------------------------
Log "7. Créer une facture (méthode: line)"
$firstArticleId = $articles[0].id
$secondArticleId = $articles[1].id
$invoiceBody = @"
{
  "clientId": $($client.id),
  "calculationMethod": "line",
  "items": [
    { "articleId": $firstArticleId, "quantity": 2, "remise": 10, "tva": 20 },
    { "articleId": $secondArticleId, "quantity": 1, "remise": 0, "tva": 20 }
  ]
}
"@
$invoice = Invoke-RestMethod -Uri "$BASE/invoices" -Method Post -Headers $userHeaders -Body $invoiceBody
Ok "Facture créée: $($invoice.numero)"
Write-Host "  Total Brut HT : $($invoice.totalBrutHt) MAD"
Write-Host "  Total HT      : $($invoice.totalHt) MAD  (après remises)"
Write-Host "  TVA           : $($invoice.tva)%"
Write-Host "  Total TTC     : $($invoice.totalTtc) MAD"
Write-Host "  Statut        : $($invoice.statut)"

# ---------------------------------------------------------------
# 8. Approuver la facture (ADMIN)
# ---------------------------------------------------------------
Log "8. Approuver la facture en tant que ADMIN"
$approved = Invoke-RestMethod -Uri "$BASE/invoices/$($invoice.id)/approve" -Method Put -Headers $adminHeaders
Ok "Statut: $($approved.statut) | Décision: $($approved.adminDecision)"

# ---------------------------------------------------------------
# 9. Stats Dashboard (ADMIN)
# ---------------------------------------------------------------
Log "9. Dashboard Stats"
$stats = Invoke-RestMethod -Uri "$BASE/invoices/stats" -Method Get -Headers $adminHeaders
Ok "Total factures : $($stats.totalInvoices)"
Write-Host "  Total HT      : $($stats.totalHt) MAD"
Write-Host "  Total TTC     : $($stats.totalTtc) MAD"
Write-Host "  En attente    : $($stats.invoicesEnAttente)"
Write-Host "  Payées        : $($stats.invoicesPayees)"
Write-Host "  Clients total : $($stats.totalClients)"
Write-Host "  Clients OK    : $($stats.clientsValides)"

Write-Host "`n============================================" -ForegroundColor Yellow
Write-Host "   FacturePro - Vérification complète OK !" -ForegroundColor Yellow
Write-Host "============================================`n" -ForegroundColor Yellow
