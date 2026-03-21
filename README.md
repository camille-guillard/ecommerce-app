# Guillard Shop

Application e-commerce full-stack : https://ecommerce-app-production-ca70.up.railway.app/

**Stack technique** : Java 21 / Spring Boot 3.4 / Angular 21 / PostgreSQL / Liquibase
**Architecture** : Hexagonale (domain / application / infrastructure)

## Fonctionnalités

### Authentification
- JWT + Spring Security, chiffrage bcrypt
- Rôles USER et ADMIN
- Connexion / inscription / modification du profil

### Catalogue
- 128 produits, 10 catégories + sous-catégories
- Filtres : recherche (insensible aux accents), catégorie, promotion, disponibilité, précommande, note minimum, attributs dynamiques par catégorie
- Tri : pertinence, nom, prix, date de sortie
- Pagination

### Produits
- Catégories et sous-catégories hiérarchiques
- Variantes (taille / couleur) avec stock par variante
- Promotions avec pourcentage de réduction
- Avis clients (notes 1-5 + commentaires + histogramme)
- Dates de sortie et label "Précommande" automatique
- Favoris (wishlist)

### Panier
- Persistant en base de données, synchronisé entre client et serveur
- Validation du stock par variante côté client et serveur
- Spinner de paiement avec simulation 3 secondes

### Commandes
- Workflow de statut : Confirmée → En transit → Terminée / Annulée
- Historique des événements avec timeline
- Vérification du stock au checkout (InsufficientStockException)
- Décrémentation du stock par variante

### Avis clients
- Notes de 1 à 5 étoiles + commentaire obligatoire
- Histogramme des notes
- Vérification que l'utilisateur a acheté le produit

### Interface utilisateur
- Responsive (sidebar desktop + burger menu mobile)
- Internationalisation FR / EN (ngx-translate)
- Mode sombre / clair
- Panier flottant en bas à droite

### Backoffice admin
- Gestion des utilisateurs (liste paginée avec tri)
- Gestion des commandes (avancement de statut, tri par colonnes)
- Gestion des produits : création, modification, activation/désactivation, upload d'images
- Accès restreint au rôle ADMIN (guard frontend + vérification backend)

### Tests
- 173 tests (JUnit 5 + Spring Boot Test + PITest)
- Tests unitaires (Mockito) + tests d'intégration (H2)
- Couverture mutation PITest

### Données de démonstration
- 51 utilisateurs, 96 commandes, 224 événements, 200+ avis
- Toutes les données seed via Liquibase (SQL)

### DevOps
- GitHub pour le versioning
- Docker multi-stage (build Angular + Maven + JRE)
- Déploiement Railway avec PostgreSQL

## Lancement en local

### Prérequis
- Java 21+
- Node.js 20+
- Maven 3.9+

### Backend
```bash
cd backend
mvn spring-boot:run
```
L'API démarre sur http://localhost:8080 avec H2 en mémoire.

### Frontend
```bash
cd frontend
npm install
npx ng serve --proxy-config proxy.conf.json
```
L'application démarre sur http://localhost:4200.

### Tests
```bash
cd backend
mvn test
```

## Comptes de test

| Utilisateur | Mot de passe | Rôle |
|-------------|-------------|------|
| `admin` | `admin` | ADMIN + USER |
| `user001` | `user001` | USER |
| `user002` | `user002` | USER |
| ... | ... | ... |
| `user050` | `user050` | USER |

Le mot de passe de chaque utilisateur est identique à son nom d'utilisateur.

## Déploiement

L'application est déployable sur Railway avec le `Dockerfile` fourni.

Variables d'environnement requises :
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://host:port/db
DATABASE_USER=user
DATABASE_PASSWORD=password
JWT_SECRET=une-clé-secrète-longue
CORS_ORIGINS=https://mon-domaine.up.railway.app
```
