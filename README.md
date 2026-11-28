# Guide Keycloak : étapes du cours

Ce guide reprend, dans l'ordre, les étapes montrées par le professeur dans la vidéo : installation de Keycloak, création d'un realm, d'un client, de rôles et d'utilisateurs, test de l'authentification, puis OTP et politique de mots de passe.

---

## 0. Contexte

- **Keycloak** est un serveur d'identité et d'accès open source (authentification, SSO, gestion des utilisateurs, rôles, tokens).
- Il est développé avec le framework **Quarkus**, qui est en concurrence avec Spring. L'interface d'administration est une application Angular.
- Le professeur utilise la **version 26**. Chaque nouvelle version corrige souvent une faille de sécurité, il faut donc mettre à jour régulièrement.

---

## 1. Télécharger et démarrer Keycloak

1. Télécharger l'archive de Keycloak (version 26) sur le site officiel,
   https://www.keycloak.org/downloads on elchrage

 ![1.png](images/1.png)
puis la décompresser.

![2.png](images/2.png)

2. Ouvrir un terminal dans le dossier `bin` :

    - Linux / Mac :
      ```bash
      ./kc.sh start-dev
      ```
    - Windows :
      ```bat
      kc.bat start-dev
      ```
![3.png](images/3.png)
3. Ouvrir le navigateur sur **http://localhost:8100**.

### Créer l'administrateur

Avec la version 26, l'administrateur initial se crée avec des variables d'environnement, avant le démarrage :
![7.png](images/7.png)

Se connecter ensuite à la console d'administration avec `siham_tayebi` / `admin`.
![8.png](images/8.png)
![9.png](images/9.png)

---

## 2. Créer un Realm

- Un **realm** est le territoire à sécuriser : l'ensemble des applications, utilisateurs, rôles et clients qui partagent la même identité.
- Un realm peut couvrir un projet ou plusieurs projets.

Étapes :

1. Dans le menu déroulant en haut à gauche (realm courant, par défaut *master*), cliquer sur **Create realm**.
2. Nom : **`enset-bdcc-realme`**.
3. Créer. Le realm `enset-bdcc-realme` est alors sélectionné.
![10.png](images/10.png)
![11.png](images/11.png)
---

## 3. Créer un client

- Un **client** représente une application (chaque frontend ou backend qui doit s'authentifier auprès de Keycloak).

Étapes :

1. Dans le realm `enset-bdcc-realme` : menu **Clients** > **Create client**.
2. **Client ID** : `app-test`.
3. Laisser les paramètres par défaut, puis **Next** puis **Save**.
![12.png](images/12.png)
![13.png](images/13.png)
![14.png](images/14.png)
![15.png](images/15.png)
> **Important :** dans les paramètres du client, le champ **Web origins** (et les Valid redirect URIs) doit contenir le domaine du frontend, par exemple `http://localhost:4200`. Sinon les appels depuis le frontend seront bloqués. Pour ce test, on ne crée pas encore d'application.

---

## 4. Créer des rôles

Dans le realm `bdcc` : **Realm roles** > **Create role**.

1. Créer le rôle **`user`** puis **Save**.
![16.png](images/16.png)
2. Créer le rôle **`admin`** puis **Save**.
![17.png](images/17.png)
ce seno n bein ajoyute'
![18.png](images/18.png)
---

## 5. Créer des utilisateurs

Dans le realm `bdcc` : **Users** > **Create new user**.

**Utilisateur 1**
- Username : `user1`
- Email : `user1@example.com`
- First name : `siham` 
- Last name : au choix
- **Create**, puis onglet **Credentials** > **Set password** :
    - Mot de passe : `1234`
 ![19.png](images/19.png)
  - **Temporary** : désactivé (pour pouvoir tester directement, sinon l'utilisateur doit changer son mot de passe à la première connexion)
![21.png](images/21.png)
**Utilisateur 2**
- Username : `user2`
- Email : `user2@example.com`
- Mot de passe : `1234`, **Temporary** désactivé
![22.png](images/22.png)
![23.png](images/23.png)
---

## 6. Affecter les rôles aux utilisateurs

1. Ouvrir **user1** > onglet **Role mapping** > **Assign role** > sélectionner **`user`**.
![24.png](images/24.png)
![25.png](images/25.png)

3. Ouvrir **user2** > onglet **Role mapping** > **Assign role** > sélectionner **`user`** et **`admin`**.
![26.png](images/26.png)
![27.png](images/27.png)
---

## 7. Tester l'authentification avec un mot de passe (Grant type `password`)

### Trouver l'adresse du token endpoint

1. Dans le realm `bdcc` : **Realm settings** > **General** / section **Endpoints** > **OpenID Endpoint Configuration**.
2. Repérer le `token_endpoint`. Il ressemble à :
![28.png](images/28.png)
   ```
   http://localhost:8100/realms/bdcc/protocol/openid-connect/token
   ```
![29.png](images/29.png)
### Envoyer la requête (Postman ou IntelliJ HTTP Client)

- **Méthode :** `POST`
- **URL :** l'adresse du token endpoint ci-dessus
- **Header :** `Content-Type: application/x-www-form-urlencoded`
![30.png](images/30.png)
- **Body (x-www-form-urlencoded)** :

| Clé | Valeur |
|---|---|
| `grant_type` | `password` |
| `username` | `user1` |
| `password` | `1234` |
| `client_id` | `app-test` |
![31.png](images/31.png)
mais on fait direkc acces garnts pour pouvoir se connetcrerdepus postamn

![32.png](images/32.png)
### Résultat

La réponse JSON contient :
- **`access_token`** : expire par défaut au bout de **300 secondes** (5 minutes).
- **`refresh_token`** : expire par défaut au bout de **1800 secondes** (30 minutes).
![33.png](images/33.png)
### Inspecter le token

Copier l'`access_token` sur **https://jwt.io** pour voir son contenu :
- `exp` : date d'expiration
- `iat` : date de création
- `sub` : identifiant de l'utilisateur authentifié
- `realm_access` : les rôles de l'utilisateur (ici `user`)
- `given_name`, `family_name`, `email` : informations de profil, ajoutées par défaut
- Signature en **RS256** (le token de rafraîchissement est, lui, signé en HMAC et ne contient ni rôles ni profil)
![34.png](images/34.png)
![35.png](images/35.png)
![36.png](images/36.png)
---

## 8. Renouveler le token (Grant type `refresh_token`)

Même URL et même header que précédemment, avec ces paramètres :

| Clé | Valeur |
|---|---|
| `grant_type` | `refresh_token` |
| `refresh_token` | le refresh token reçu à l'étape 7 |
| `client_id` | `app-test` |
![37.png](images/37.png)
Keycloak renvoie un **nouvel access token** et un **nouveau refresh token**.

Cas d'usage du cours : une application mobile stocke le token de façon sécurisée et l'utilise pour accéder au backend.

---

## 9. Authentification client credentials (Grant type `client_credentials`)

Utile pour la communication **service à service** (un backend qui appelle un autre backend), sans utilisateur.

### Activer le client secret

1. Ouvrir le client **app-test** > onglet **Settings**.
2. Dans la section **Capability config** : activer **Client authentication**, puis cocher **Service accounts roles**.
3. **Save**.
4. Onglet **Credentials** : copier le **Client secret**.
![38.png](images/38.png)
![39.png](images/39.png)
![40.png](images/40.png)
### Envoyer la requête

| Clé | Valeur |
|---|---|
| `grant_type` | `client_credentials` |
| `client_id` | `app-test` |
| `client_secret` | le secret copié |

Le token est renvoyé, sans username ni mot de passe.

> **Piège du cours :** une fois le client secret activé, le grant `password` ne fonctionne plus avec le seul `client_id`. Il faut ajouter `client_secret` à la requête, sinon la requête est refusée.
![41.png](images/41.png)
---

## 10. Options de configuration à connaître

### Login (onglet *Realm settings* > *Login*)
- **User registration** : affiche un lien d'inscription sur la page de connexion.
- **Forgot password** : formulaire de réinitialisation du mot de passe.
- **Remember me**, **Email as username** (connexion avec l'email ou le username), **Login with email**.
- **Verify email** : vérification de l'adresse email.
![42.png](images/42.png)
### Thèmes
- La page de connexion est fournie par défaut, mais on peut définir un **thème personnalisé** (*Realm settings* > *Themes*).

### User Federation
- Permet de déléguer la gestion des identités à un annuaire existant, par exemple **LDAP** ou **Active Directory**, quand l'entreprise centralise déjà ses utilisateurs.

### Identity Providers
- Permet de déléguer l'authentification à un fournisseur externe : **GitHub, Google, Facebook, Twitter**, etc. Il suffit de cocher la case et de renseigner les identifiants du fournisseur.

---

## 11. Double authentification (OTP)

### Activer l'OTP pour un utilisateur

1. **Users** > **user1** > section **Required user actions**.
2. Sélectionner **Configure OTP** puis **Save**.

### Première connexion

1. Se connecter avec `user1` / `1234`.
2. Une page affiche un **QR code**.
3. Scanner le QR code avec une application comme **Google Authenticator**, **Microsoft Authenticator** ou **FreeOTP**.
4. Saisir le code à 6 chiffres affiché par l'application.

### Principe (expliqué par le professeur)

- Keycloak et l'application mobile n'échangent **aucune donnée** pendant l'utilisation.
- Le QR code transmet la **clé secrète partagée**, le nom du realm et les paramètres (algorithme, période).
- À partir de la même clé et de l'heure, les deux côtés calculent le **même code** à chaque période. C'est le principe **TOTP** (*Time-based One-Time Password*).
- Le professeur indique une période de 15 secondes ; la valeur par défaut de Keycloak est de 30 secondes. Elle est configurable dans les paramètres OTP du realm.

---

## 12. Politique de mots de passe

Dans **Authentication** > **Policies** > **Password policy**, on peut ajouter des règles :

- **Expiration** : le mot de passe expire au bout de 365 jours (exemple du cours).
- **Not recently used** : interdit de réutiliser un ancien mot de passe (nombre de mémorisation à définir).
- **Length** : longueur minimale.
- **Uppercase / Lowercase / Digits / Special characters** : nombre minimal de caractères de chaque type.
- **Not username** : le mot de passe ne doit pas contenir le nom d'utilisateur.
- **Maximum length** : longueur maximale.

Ces règles s'appliquent dès l'inscription ou au changement de mot de passe. Le professeur rappelle qu'il ne faut jamais laisser les utilisateurs choisir n'importe quel mot de passe.

---

## 13. Pour la suite

Le prochain cours (disponible sur Classroom) montrera :
- Créer une application **backend Spring** et un **frontend Angular**.
- Sécuriser le backend avec **Spring Security** et **Keycloak**.
- Sécuriser le frontend avec Keycloak.

---

## Récapitulatif rapide

| Étape | Où | Action |
|---|---|---|
| 1 | Terminal | `kc.sh start-dev` puis admin via variables d'environnement |
| 2 | Console | Créer le realm `bdcc` |
| 3 | Clients | Créer le client `app-test` |
| 4 | Realm roles | Créer `user` et `admin` |
| 5 | Users | Créer `user1` (mdp `1234`) et `user2` (mdp `1234`) |
| 6 | Role mapping | `user1` : user ; `user2` : user + admin |
| 7 | Postman | `grant_type=password` sur le token endpoint |
| 8 | Postman | `grant_type=refresh_token` |
| 9 | Client `app-test` | Client authentication + Service accounts, puis `client_credentials` |
| 10 | Login / Thèmes / Federation / IdP | Options à explorer |
| 11 | User1 | Required user action : Configure OTP |
| 12 | Authentication > Policies | Password policy |


Partie2:Sécuriser une architecture  micro-service Spring Angular avec Keycloak :  
on crer invntory sevri e:
# Guide 2 : sécuriser un backend Spring Boot et un frontend Angular avec Keycloak

Ce guide reprend, dans l'ordre, les étapes montrées par le professeur dans la deuxième partie : création du microservice `inventory-service`, mise en place de la sécurité Spring, création du frontend Angular, intégration de Keycloak (authentification, rôles, protection des routes), puis options avancées.

> **Prérequis :** avoir suivi le guide 1 (Keycloak installé, realm `bdcc`, rôles `user` et `admin`, utilisateurs `user1` et `user2`).

---

## 0. Architecture visée

```
Navigateur (Angular :4200)  --->  Keycloak (:8180)   [login, tokens]
        |
        | requête HTTP + header Authorization: Bearer <JWT>
        v
inventory-service (Spring Boot :8087)  --->  vérifie la signature du JWT via la clé publique de Keycloak
```
![43.png](images/43.png)

- **Keycloak** authentifie l'utilisateur et délivre un JWT.
- **Angular** (frontend) redirige vers Keycloak pour la connexion, puis envoie le JWT à chaque requête.
- **Spring Boot** (backend) vérifie le JWT et applique les règles d'accès selon les rôles.
![44.png](images/44.png)
 > **Port Keycloak :** dans ce guide, Keycloak est sur `http://localhost:8180`. Si Jenkins occupe ce port, lancez Keycloak sur `8180` (`kc.bat start-dev --http-port=8180`) et remplacez `8180` par `8180` partout (`application.properties`, Angular, Postman).

---

## 1. Créer le projet et le microservice Spring Boot

### 1.1 Projet parent

1. Créer un projet **Maven** nommé `ecom-bdcc-app` (groupe `ma.enset`, artifact `ecom-bdcc-app`, par exemple).

### 1.2 Module inventory-service

1. Clic droit sur le projet > **New** > **Module** > **Spring Initializr**.
2. Paramètres :
    - Name : `inventory-service`
    - Java : **21**
    - Spring Boot : **3.2.2**
3. Dépendances :
    - **Spring Web**
    - **Spring Data JPA**
    - **H2 Database**
    - **Lombok**
    - **Eureka Discovery Client** (`spring-cloud-starter-netflix-eureka-client`)
    - **Config Client** (`spring-cloud-starter-config`)
    - **OAuth2 Resource Server** (`spring-boot-starter-oauth2-resource-server`)

> Les dépendances Spring Cloud (Eureka, Config) sont présentes pour l'architecture microservices. Elles seront désactivées dans `application.properties` pour ce test. Si Maven signale un conflit de version, utilisez le BOM Spring Cloud compatible avec Spring Boot 3.2 (série `2023.0.x`).

> **Pourquoi OAuth2 Resource Server ?** Cette dépendance permet à un microservice de vérifier des tokens OpenID Connect (JWT) émis par Keycloak. Il est recommandé pour les backends.

---

## 2. Créer les entités, le repository et les données de test

Structure des packages dans `inventory-service` :

```
src/main/java/com/example/inventoryservice/
├── entities/
├── repositories/
├── web/
├── security/
└── InventoryServiceApplication.java
```

### 2.1 Entité `Product`

`entities/Product.java`

```java
package com.example.inventoryservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    private String id;
    private String name;
    private double price;
    private int quantity;
}
```

> Le professeur utilisait `@Data`, mais signalait des soucis selon les versions. `@Getter` et `@Setter` évitent ce problème.

### 2.2 Repository

`repositories/ProductRepository.java`

```java
package com.example.inventoryservice.repositories;

import com.example.inventoryservice.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
```

### 2.3 Données de test avec `CommandLineRunner`

`InventoryServiceApplication.java`

```java
package com.example.inventoryservice;

import com.example.inventoryservice.entities.Product;
import com.example.inventoryservice.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(ProductRepository productRepository) {
        return args -> {
            productRepository.save(Product.builder()
                    .id(UUID.randomUUID().toString())
                    .name("Computer")
                    .price(1200)
                    .quantity(10)
                    .build());
            productRepository.save(Product.builder()
                    .id(UUID.randomUUID().toString())
                    .name("Printer")
                    .price(100)
                    .quantity(34)
                    .build());
            productRepository.save(Product.builder()
                    .id(UUID.randomUUID().toString())
                    .name("Smart Phone")
                    .price(4000)
                    .quantity(5)
                    .build());
        };
    }
}
```

> **Explication du professeur :** `CommandLineRunner` est une interface à une seule méthode `run`. Spring exécute automatiquement cette méthode au démarrage, dès qu'il trouve un bean de ce type. La version avec lambda est équivalente à une classe qui implémente l'interface.
>
> **Pourquoi `UUID` ?** Le champ `id` est une `String` et non un entier auto-incrémenté. Il faut donc fournir la valeur soi-même. Les valeurs de prix et de quantité sont des exemples, la vidéo est peu claire sur ces chiffres.

### 2.4 Configuration

`src/main/resources/application.properties`

```properties
server.port=8087
spring.application.name=inventory-service

spring.datasource.url=jdbc:h2:mem:product-db
spring.h2.console.enabled=true

# Désactivation de Config Server et de Discovery pour ce test
spring.cloud.config.enabled=false
spring.cloud.discovery.enabled=false

```

---

## 3. Créer le contrôleur REST

`web/ProductController.java`

```java
package com.example.inventoryservice.web;

import com.example.inventoryservice.entities.Product;
import com.example.inventoryservice.repositories.ProductRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductRepository productRepository;

    // Injection par constructeur (bonne pratique)
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/products")
    public List<Product> productList() {
        return productRepository.findAll();
    }

    @GetMapping("/products/{id}")
    public Product productById(@PathVariable String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit introuvable : " + id));
    }

    // Endpoint de test : affiche le contexte de sécurité (Authentication)
    @GetMapping("/auth")
    public Authentication authentication(Authentication authentication) {
        return authentication;
    }
}
```

---

## 4. Premier test sans sécurité

Démarrez `inventory-service`, puis ouvrez `http://localhost:8087/api/products`.

![49.png](images/49.png)
Résultat attendu : **401** ou page de connexion. Spring Security, par défaut, protège **toutes** les routes et demande une authentification.

---

## 5. Configuration de Spring Security (étape par étape)

### 5.1 Première configuration : tout autoriser

Le professeur conseille de commencer avec une configuration qui autorise tout, afin de développer le frontend sans bloquer. On sécurisera ensuite.

`security/SecurityConfig.java` (version initiale)

```java
package com.example.inventoryservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(ar -> ar
                .requestMatchers("/api/**").permitAll()
                .anyRequest().authenticated());
        return http.build();
    }
}
```

> **CSRF :** la protection CSRF est utile pour les sessions (stateful). Ici l'authentification est stateless (JWT), donc on la désactive. Sans cela, les requêtes POST recevraient une erreur **403**.

### 5.2 Erreurs courantes à comprendre

| Code | Signification dans ce cours |
|---|---|
| **401** | Non authentifié : aucun token, ou token invalide ou expiré |
| **403** | Authentifié, mais sans le droit (rôle insuffisant), ou CSRF non désactivé |

### 5.3 Autoriser la console H2

La console H2 utilise des frames HTML. Spring Security les refuse par défaut.

Dans `SecurityConfig`, ajoutez :

```java
http.csrf(csrf -> csrf.disable())
    .headers(headers -> headers.frameOptions(frame -> frame.disable()))
    .authorizeHttpRequests(ar -> ar
        .requestMatchers("/h2-console/**").permitAll()
        .requestMatchers("/api/**").permitAll()
        .anyRequest().authenticated());
```

Ouvrez ensuite `http://localhost:8087/h2-console`, JDBC URL : `jdbc:h2:mem:product-db`.
![48.png](images/48.png)
---



## 9. Créer le frontend Angular

### 9.1 Générer le projet dans le dossier courant

Créez un dossier `app-angular`, ouvrez-le dans un terminal, puis :

```bash
ng new app-angular --directory ./ --no-standalone --style=css --ssr=false --routing
```
![45.png](images/45.png)
![46.png](images/46.png)

> Le professeur utilise `--no-standalone` pour rester sur l'approche avec modules (NgModule), plus proche de l'ancien fonctionnement d'Angular. Il est possible d'utiliser le mode standalone, mais le code sera différent.

### 9.2 Installer Bootstrap

```bash
npm install bootstrap bootstrap-icons
```
![47.png](images/47.png)

Dans `angular.json`, section `build > options` :

```json
"styles": [
  "node_modules/bootstrap/dist/css/bootstrap.min.css",
  "src/styles.css"
],
"scripts": [
  "node_modules/bootstrap/dist/js/bootstrap.bundle.js"
]
```
On ajout rnvabar depusi botstrap 5
![50.png](images/50.png)
### 9.3 Générer les composants

```bash
ng g c ui/products
ng g c ui/customers
```
![51.png](images/51.png)

### 9.4 Routage

`src/app/app-routing.module.ts`

```ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductsComponent } from './ui/products/products.component';
import { CustomersComponent } from './ui/customers/customers.component';

const routes: Routes = [
  { path: 'products', component: ProductsComponent },
  { path: 'customers', component: CustomersComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
```

> Les routes seront protégées avec un guard à l'étape 11.

### 9.5 Barre de navigation

`src/app/app.component.html` : remplacez le contenu par une barre de navigation Bootstrap (copiée d'un exemple, comme dans le cours) puis gardez uniquement :

```html
<nav class="navbar navbar-expand-lg bg-body-tertiary">
  <div class="container-fluid">
    <a class="navbar-brand" href="#">Ecom</a>
    <ul class="navbar-nav me-auto">
      <li class="nav-item">
        <a class="nav-link" routerLink="products">Products</a>
      </li>
      <li class="nav-item">
        <a class="nav-link" routerLink="customers">Customers</a>
      </li>
    </ul>
  </div>
</nav>

<div class="container mt-4">
  <router-outlet></router-outlet>
</div>
```

Lancez `ng serve` et ouvrez `http://localhost:4200`.

---
n tetse lapp la ya  
customers 
![52.png](images/52.png)
products
![53.png](images/53.png)
mais que avec producs et customers affiches dnas les pages
### 10.1 Composant produits

`src/app/ui/products/products.component.ts`

```ts
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css'],
})
export class ProductsComponent implements OnInit {
  public products: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<any[]>('http://localhost:8087/api/products').subscribe({
      next: (data) => (this.products = data),
      error: (err) => console.log(err),
    });
  }
}
```

`src/app/ui/products/products.component.html`

```html
<h2>Products</h2>
<table class="table">
  <thead>
    <tr>
      <th>ID</th>
      <th>Name</th>
      <th>Price</th>
      <th>Quantity</th>
    </tr>
  </thead>
  <tbody>
    <tr *ngFor="let p of products">
      <td>{{ p.id }}</td>
      <td>{{ p.name }}</td>
      <td>{{ p.price }}</td>
      <td>{{ p.quantity }}</td>
    </tr>
  </tbody>
</table>
```

Ajoutez `HttpClientModule` dans les `imports` de `app.module.ts`.

> **Problème de CORS :** si la liste ne s'affiche pas et que la console indique *blocked by CORS policy*, vérifiez que la configuration CORS (étape 6) est bien présente et que le service a bien redémarré.
![54.png](images/54.png)
On me cross origin * dans contorller et c bon nos prdsucs se marche nbein
![55.png](images/55.png)

On eput supprimer cet cross orignin et mttre  
---
## 6. Configuration CORS

Sans CORS, le navigateur bloque les requêtes d'Angular (`localhost:4200`) vers Spring (`localhost:8087`). L'erreur apparaît dans les outils de développement : *blocked by CORS policy*.

Le professeur montre d'abord une solution rapide : `@CrossOrigin(origins = "*")` sur le contrôleur. Il précise que **ce n'est pas la bonne solution**, car elle ne fonctionne pas sur les endpoints protégés par Spring Security. La solution recommandée est un bean `CorsConfigurationSource`, qui est utilisé par la chaîne de sécurité.

Ajoutez dans `SecurityConfig` :

```java
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Bean
CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    // Pour le cours : "*". En production, indiquez uniquement l'origine du frontend.
    config.setAllowedOrigins(List.of("*"));
    config.setAllowedMethods(List.of("*"));
    config.setAllowedHeaders(List.of("*"));
    config.setExposedHeaders(List.of("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```
on restets aprs vaoir mit a et supriemr cross oriigni et c bon
 et ca marhc tres bein:
![56.png](images/56.png)

> En production, remplacez `List.of("*")` par `List.of("http://localhost:4200")`.

---
COmem notre keyclock et deja demarre 
don c on apsse a 
on cree un nv ccleint dna keycock
![57.png](images/57.png)
![58.png](images/58.png)
les roles user et amin sont dej acreer
et les user1 e euser 2 dsont deja creer
pour els ste dauth c deja fait sur postman
on essaye d tse rmiantnn ss http client e intellij:
POST http://localhost:8180/realms/enset-bdcc-realme/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&username=user2&password=Wyuw6089?&client_id=ecom-client-ang

<> 2026-11-27T140652.200.json
<> 2026-11-27T140604.400.json
<> 2026-11-27T140434.404.json

###

![59.png](images/59.png) 
ett voila lauth c amrche
pour auth avce refrsh token en fiat
POST http://localhost:8180/realms/enset-bdcc-realme/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=refresh_token&client_id=ecom-client-ang&refresh_token=eyJhbGciOiJIUzUxMiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIwNzUyOWRmNC04MmVhLTRkMDAtYTg2NC01MDk4MjE0NzlkNmMifQ.eyJleHAiOjE3OTU3ODY2MTIsImlhdCI6MTc5NTc4NDgxMiwianRpIjoiMzFlM2MyNWQtM2FjYi1hNTFkLWU5MGUtNTA1ZDk3MzMyNWQ3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL3JlYWxtcy9lbnNldC1iZGNjLXJlYWxtZSIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODE4MC9yZWFsbXMvZW5zZXQtYmRjYy1yZWFsbWUiLCJzdWIiOiJkMjZmOTY5NS1lZWVhLTRjZmMtODY3Mi02MWI4NzNmMjgyOTAiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoiZWNvbS1jbGllbnQtYW5nIiwic2lkIjoicVdGWmxueHJ0YU5hRVp0ZFZDT3pBYXFuIiwic2NvcGUiOiJiYXNpYyBwcm9maWxlIHdlYi1vcmlnaW5zIGVtYWlsIGFjciByb2xlcyIsImF1ZF94IjoiYWNjb3VudCIsInByb3YiOiJkZWZhdWx0In0.zdp7ph5uj1sozl5EdAhjhNsLB9oQgV-mVe845G5Mz2A90E-hGM73pIsdszPDSg5A-TXTXyg9teVXJ_z8N-Oihw

<> 2026-11-27T141019.200.json

![60.png](images/60.png)
on active ca pour abor nore secr dna s ecom-client -ang 
![61.png](images/61.png)
la on a le secre on rsert donc dans intellij http cient:

POST http://localhost:8180/realms/enset-bdcc-realme/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials&client_id=ecom-client-ang&client_secret=mAYzWdO6Ga1Pfomfr49fWEIpcGTI7cgAtyyWKqwI5g7RxH1PLvVaKUaP7IGHi35hMLe5oNxelEt2kpN1Ta3PR7
![62.png](images/62.png)
on reste avec refrh tokn et clint secre
comm ca
POST http://localhost:8180/realms/enset-bdcc-realme/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=refresh_token&client_id=ecom-client-ang&refresh_token=eyJhbGciOiJIUzUxMiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIwNzUyOWRmNC04MmVhLTRkMDAtYTg2NC01MDk4MjE0NzlkNmMifQ.eyJleHAiOjE3OTU3ODY2MTIsImlhdCI6MTc5NTc4NDgxMiwianRpIjoiMzFlM2MyNWQtM2FjYi1hNTFkLWU5MGUtNTA1ZDk3MzMyNWQ3IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL3JlYWxtcy9lbnNldC1iZGNjLXJlYWxtZSIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODE4MC9yZWFsbXMvZW5zZXQtYmRjYy1yZWFsbWUiLCJzdWIiOiJkMjZmOTY5NS1lZWVhLTRjZmMtODY3Mi02MWI4NzNmMjgyOTAiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoiZWNvbS1jbGllbnQtYW5nIiwic2lkIjoicVdGWmxueHJ0YU5hRVp0ZFZDT3pBYXFuIiwic2NvcGUiOiJiYXNpYyBwcm9maWxlIHdlYi1vcmlnaW5zIGVtYWlsIGFjciByb2xlcyIsImF1ZF94IjoiYWNjb3VudCIsInByb3YiOiJkZWZhdWx0In0.zdp7ph5uj1sozl5EdAhjhNsLB9oQgV-mVe845G5Mz2A90E-hGM73pIsdszPDSg5A-TXTXyg9teVXJ_z8N-Oihw&client_secret=mAYzWdO6Ga1Pfomfr49fWEIpcGTI7cgAtyyWKqwI5g7RxH1PLvVaKUaP7IGHi35hMLe5oNxelEt2kpN1Ta3PR7

et voial ca passe 
![63.png](images/63.png)

on reveint a sec config et pour fiatre sec avce keclock il fuat ajoute oauth2ressource server
.oauth2ResourceServer(o2->o2.jwt(Customizer.withDefaults()));
on ajoute ca dan sapp proerties
ou se trouve kyclock
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/ecom-web-app
pour endpoints cetrifs
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://Localhost:8080/realms/ecom-web-app/protocol/openid-connect/certs
se sont ceux mofdiier et corri
http://localhost:8180/realms/enset-bdcc-realme
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8180/realms/enset-bdcc-realme/protocol/openid-connect/certs
lzven
on reexec
on suppr ca n sec conf
//                        .requestMatchers("/api/**").permitAll()
 on essya consulter les prod depusi
http://localhost:8087/api/products
et voial err 401 not auhorized

![64.png](images/64.png)
on essay envouer req http
 avec get mme prob 401
![65.png](images/65.png)
je me reathtif pour recucpe jwt et ke rssey
POST http://localhost:8180/realms/enset-bdcc-realme/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&username=user2&password=Wyuw6089?&client_id=ecom-client-ang&client_secret=mAYzWdO6Ga1Pfomfr49fWEIpcGTI7cgAtyyWKqwI5g7RxH1PLvVaKUaP7IGHi35hMLe5oNxelEt2kpN1Ta3PR7


e voila donc
GET http://localhost:8087/api/products
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGeVFaTXNzbEpVUTM3ZUx5SFlKY0RpMmVHRUZ2VTRrbGhwbjh3bXBVclZJIn0.eyJleHAiOjE3OTU3ODY3MzUsImlhdCI6MTc5NTc4NjQzNSwianRpIjoib25ydHJvOjJlZDQ0YTQxLWMwZDktMjJhNi03NzA3LWMwYmE4YmQ1ZTM3NCIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODE4MC9yZWFsbXMvZW5zZXQtYmRjYy1yZWFsbWUiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZDI2Zjk2OTUtZWVlYS00Y2ZjLTg2NzItNjFiODczZjI4MjkwIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZWNvbS1jbGllbnQtYW5nIiwic2lkIjoiM3MwdE5jeGNLcHpjWmxTNDNrMjV4a2JtIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsIkFETUlOIiwidW1hX2F1dGhvcml6YXRpb24iLCJVU0VSIiwiZGVmYXVsdC1yb2xlcy1lbnNldC1iZGNjLXJlYWxtZSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6ImltYW5lIHRheWViaSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIyIiwiZ2l2ZW5fbmFtZSI6ImltYW5lIiwiZmFtaWx5X25hbWUiOiJ0YXllYmkiLCJlbWFpbCI6InVzZXIyQGdtYWlsLmNvbSJ9.P3ssk0Yw3pbSGlbpAm0rn8V5t_Y96n8LJ5LWfciEkmLjZAfz7vYQFDW745aFENkOAk3xqsaiOnBBwNQoaGSwSS02SCH2T7Pej2YSq100PN6_X51jscSyKbvZFea4M0BV-umXFX3wmmn_5v6pDjyBvhKa1ADp5d737I9ZXyGaCYUK28am8ydE5fbHSyM27XNQ-CvapVlhkx4zChxWMXmO55aYcrBPbv16E5Biny5xJlxz1UQks-0jHanBDdXmuQ-n6YxlR6nD-y9ysNduHbISd8TIwFXPnwMkX8M_moWqerPaFSLWP7AaJn6cCs56Lm9qdHc9nhwlXYKUcLwE0NjPAg

je voxi l slite des prods
![66.png](images/66.png)
on ajoue ca n controler
@GetMapping("/auth")
public Authentication authentication(Authentication authentication){
return authentication;
on ressaye uen endpt
GET http://localhost:8087/api/auth
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGeVFaTXNzbEpVUTM3ZUx5SFlKY0RpMmVHRUZ2VTRrbGhwbjh3bXBVclZJIn0.eyJleHAiOjE3OTU3ODg5NDIsImlhdCI6MTc5NTc4ODY0MiwianRpIjoib25ydHJvOmU0ZjJlMjJiLTNjOGYtN2E1MS0yNGUwLTFkYjg3NTk4OTE5NSIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODE4MC9yZWFsbXMvZW5zZXQtYmRjYy1yZWFsbWUiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZDI2Zjk2OTUtZWVlYS00Y2ZjLTg2NzItNjFiODczZjI4MjkwIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZWNvbS1jbGllbnQtYW5nIiwic2lkIjoib3BRMFJJRml0N0dUYUxOa01SQjBaaDVMIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsIkFETUlOIiwidW1hX2F1dGhvcml6YXRpb24iLCJVU0VSIiwiZGVmYXVsdC1yb2xlcy1lbnNldC1iZGNjLXJlYWxtZSJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwibmFtZSI6ImltYW5lIHRheWViaSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIyIiwiZ2l2ZW5fbmFtZSI6ImltYW5lIiwiZmFtaWx5X25hbWUiOiJ0YXllYmkiLCJlbWFpbCI6InVzZXIyQGdtYWlsLmNvbSJ9.ecAU6w01LhxMZrh34wDCaIy6B5HSz5unjhB69hA-sKbQqoQ2MT7lo5Q2fXUdZjNLoMDEpDpiNRQ9xfCbZEZo-kekDKaKBcL71ufh2kJ-FYKwuFdEQ1FZuhQzgThqX9ud_e3azgx5zcWXaFQLxlUc7Buyc4vSXoT_LEzBLFp6qk6eWgk9hM2uq3gausRsHGicEkcm1IjrrHOt-93jbaFx6uJncvPQUH-1sPo29Jd9-ql_6alhjePubZr-5HIInyASJlG43OQ65JOil8Q0RI9PrDM0IEoqfMQIglLSau4usP4pZ-CEiEnTMr65Iz-9HdZNqiscISKQoOeVZfCqn-VjZA


![67.png](images/67.png)
on ajou ca en sec conf
.authorizeHttpRequests(ar->ar.requestMatchers("/api/products/**").hasAuthority("ADMIN"))
![68.png](images/68.png)
et voila les roles
## 7. Sécuriser avec Keycloak (JWT et rôles)

### 7.1 Le convertisseur de rôles

Par défaut, Spring lit les autorités dans le claim `scope`. Keycloak place les rôles dans un claim `realm_access`, sous la clé `roles`. Il faut donc un convertisseur personnalisé.

`security/JwtAuthConverter.java`

```java
package com.example.inventoryservice.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = Stream.concat(
                        defaultConverter.convert(jwt).stream(),
                        extractRealmRoles(jwt).stream())
                .collect(Collectors.toSet());
        return new JwtAuthenticationToken(jwt, authorities);
    }

    @SuppressWarnings("unchecked")
    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null || realmAccess.get("roles") == null) {
            return List.of();
        }
        Collection<String> roles = (Collection<String>) realmAccess.get("roles");
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
```

> **Explication :** le convertisseur lit le claim `realm_access.roles` du JWT, par exemple `["user", "admin"]`, et crée une autorité Spring pour chaque rôle. Les rôles sont donc utilisés **sans préfixe** `ROLE_` : on vérifiera avec `hasAuthority("admin")`.
on tetse on sahentife pusi o test et la on voix liste authorities 
![69.png](images/69.png)
> ui o  tets avec user1 qui na pas role admin on voix 403
.a
.authorizeHttpRequests(ar->ar.requestMatchers("/api/products/**").hasAuthority("ADMIN"))
> comem on a 
 ![70.png](images/70.png)
> 
> 
### 7.2 Règles d'accès finales

Mettez à jour `SecurityConfig` :
on commente ca

[//]: # (.authorizeHttpRequests&#40;ar->ar.requestMatchers&#40;"/api/products/**"&#41;.hasAuthority&#40;"ADMIN"&#41;&#41;)

```java
package com.example.inventoryservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final KeycloakJwtConverter keycloakJwtConverter;

    public SecurityConfig(KeycloakJwtConverter keycloakJwtConverter) {
        this.keycloakJwtConverter = keycloakJwtConverter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(ar -> ar
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/api/products").hasAuthority("admin")
                .requestMatchers("/api/products/**").hasAuthority("user")
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter)));
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```
et apres lavoir commnt on emt dans conollr
@PreAuthorize("hasAuthority('ADMIN')")
products
@PreAuthorize("hasAuthority('USER')")

gProduByd id
et pare son ajout dans sceurity config
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
et on teseavc user1 qui na pas droit admin t va voir 403

> **Règles :**
> - `GET /api/products` (liste complète) : rôle **admin** uniquement.
> - `GET /api/products/{id}` : rôle **user**.
> - Tout le reste : être authentifié.
>
> Le professeur précise que l'autorisation peut aussi se faire par annotation, avec `@EnableMethodSecurity` et `@PreAuthorize("hasAuthority('admin')")` sur la méthode du contrôleur.

Redémarrez le service.

---

## 8. Tester le backend avec Postman

### 8.1 Activer le Direct Access Grants (si besoin)

Pour que la requête `password` fonctionne, le client doit autoriser le grant. Dans Keycloak : **Clients > app-test > Settings > Capability config > Direct access grants** (coché) > **Save**.

Si le client a **Client authentication** activé, la requête doit aussi contenir `client_secret`.

### 8.2 Obtenir un token

- **Méthode :** `POST`
- **URL :** `http://localhost:8180/realms/bdcc/protocol/openid-connect/token`
- **Header :** `Content-Type: application/x-www-form-urlencoded`
- **Body :**

| Clé | Valeur |
|---|---|
| `grant_type` | `password` |
| `username` | `user2` |
| `password` | `1234` |
| `client_id` | `app-test` |

Copiez la valeur de `access_token`.

### 8.3 Appeler le backend

Dans Postman, onglet **Headers** :

```
Authorization: Bearer <access_token>
```

Testez :

| Requête | user1 (rôle user) | user2 (rôles user + admin) |
|---|---|---|
| `GET /api/products` | 403 | 200 |
| `GET /api/products/{id}` | 200 | 200 |
| `GET /api/auth` | 200 (affiche l'authentification) | 200 |

Sans header `Authorization`, la réponse est **401**.

> Dans `/api/auth`, vérifiez la section `authorities` : elle doit contenir `user` et `admin` pour `user2`.

---

## 11. Installer et configurer le client Keycloak pour Angular

### 11.1 Dans Keycloak : créer le client Angular

1. Realm **bdcc** > **Clients** > **Create client**.
2. **Client ID** : `app-ang`
3. **Next** : laisser **Client authentication** **désactivé** (client public, pas de secret pour un frontend).
4. **Login settings** :
    - **Valid redirect URIs** : `http://localhost:4200/*`
    - **Web origins** : `http://localhost:4200`
5. **Save**.

> **Important :** ne pas activer **Client authentication** pour le frontend. Le professeur montre que le client secret ne peut pas être utilisé dans une application Angular, car il serait visible dans le navigateur. Si vous l'avez activé pendant les tests, désactivez-le.

> **Recommandation actuelle (Keycloak 26) :** activez aussi **PKCE** dans l'onglet *Advanced* du client, avec **Proof Key for Code Exchange Code Challenge Method** = `S256`.

### 11.2 Installer les librairies

```bash
npm install keycloak-angular keycloak-js
```
![72.png](images/72.png)
puis modules.ts on ajoute ca :
import {APP_INITIALIZER, NgModule} from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ProductsComponent } from './ui/products/products.component';
import { CustomersComponent } from './ui/customers/customers.component';
import {HttpClientModule} from "@angular/common/http";
import {KeycloakAngularModule, KeycloakService} from "keycloak-angular";
import { OrdersComponent } from './ui/orders/orders.component';
import { OrderDetailsComponent } from './ui/order-details/order-details.component';

function initializeKeycloak(keycloak: KeycloakService) {
return () =>
keycloak.init({
config: {
url: 'http://localhost:8080',
realm: 'bdcc-realm',
clientId: 'ecom-client-ang'
},
initOptions: {
onLoad: 'check-sso',
silentCheckSsoRedirectUri:
window.location.origin + '/assets/silent-check-sso.html'
}
});
}
@NgModule({
declarations: [
AppComponent,
ProductsComponent,
CustomersComponent,
OrdersComponent,
OrderDetailsComponent
],
imports: [
BrowserModule,
AppRoutingModule, HttpClientModule, KeycloakAngularModule
],
providers: [
{provide : APP_INITIALIZER, useFactory : initializeKeycloak, multi :true, deps : [KeycloakService]}
],
bootstrap: [AppComponent]
})
export class AppModule { }

### 11.3 Page de vérification silencieuse

Créez `src/assets/silent-check-sso.html` :

```html
<!doctype html>
<html>
  <body>
    <script>
      parent.postMessage(location.href, location.origin);
    </script>
  </body>
</html>
```
et on jaoute ce provider en modusles.s:
provide: APP_INITIALIZER,useFactory:initializeKeycloak,multi: true,deps: [KeycloakService]

> Cette page permet à Keycloak de vérifier la session sans rediriger toute l'application (check-sso).

### 11.4 Initialisation dans `app.module.ts`

```ts
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ProductsComponent } from './ui/products/products.component';
import { CustomersComponent } from './ui/customers/customers.component';

function initializeKeycloak(keycloak: KeycloakService) {
  return () =>
    keycloak.init({
      config: {
        url: 'http://localhost:8180',
        realm: 'bdcc',
        clientId: 'app-ang',
      },
      initOptions: {
        onLoad: 'check-sso',
        silentCheckSsoRedirectUri:
          window.location.origin + '/assets/silent-check-sso.html',
      },
      enableBearerInterceptor: true,
    });
}

@NgModule({
  declarations: [AppComponent, ProductsComponent, CustomersComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    KeycloakAngularModule,
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializeKeycloak,
      multi: true,
      deps: [KeycloakService],
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
```

> **Explication du professeur :** `APP_INITIALIZER` exécute la fonction **avant** le démarrage de l'application. Elle initialise Keycloak et vérifie si l'utilisateur est déjà authentifié. `check-sso` affiche l'interface sans forcer la connexion. La redirection vers Keycloak n'a lieu que lorsqu'on accède à une route protégée.
>
> `enableBearerInterceptor: true` ajoute automatiquement le header `Authorization: Bearer <token>` à chaque requête HTTP.
>
> Les noms de paramètres peuvent changer selon la version de `keycloak-angular`. Vérifiez la documentation officielle de la version installée.

---

## 12. Protéger les routes avec un Guard

### 12.1 Générer le guard

```bash
ng g guard guards/auth
```
![73.png](images/73.png)
Choisissez `CanActivate` si Angular le demande.

### 12.2 Code du guard

`src/app/guards/auth.guard.ts`

```ts
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { KeycloakAuthGuard, KeycloakService } from 'keycloak-angular';

@Injectable({ providedIn: 'root' })
export class AuthGuard extends KeycloakAuthGuard {
  constructor(
    protected override readonly router: Router,
    protected readonly keycloakAngular: KeycloakService
  ) {
    super(router, keycloakAngular);
  }

  public async isAccessAllowed(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Promise<boolean> {
    // 1. L'utilisateur doit être authentifié
    if (!this.authenticated) {
      await this.keycloakAngular.login({
        // Après la connexion, Keycloak renvoie l'utilisateur sur la page demandée
        redirectUri: window.location.origin + state.url,
      });
      return false;
    }

    // 2. Rôles requis pour la route (définis dans route.data)
    const requiredRoles: string[] = route.data['roles'];

    // Pas de rôle requis : l'authentification suffit
    if (!requiredRoles || requiredRoles.length === 0) {
      return true;
    }

    // L'utilisateur doit posséder tous les rôles requis
    return requiredRoles.every((role) => this.roles.includes(role));
  }
}
```

> **Note :** le mot-clé `override` est nécessaire avec Angular 17 et versions suivantes, car l'attribut `router` est déjà déclaré dans la classe parente.

### 12.3 Protéger les routes

`src/app/app-routing.module.ts`

```ts
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  {
    path: 'products',
    component: ProductsComponent,
    canActivate: [AuthGuard],
    data: { roles: ['admin'] },
  },
  {
    path: 'customers',
    component: CustomersComponent,
    canActivate: [AuthGuard],
    data: { roles: ['user'] },
  },
];
```
{ path: 'products', component: Products,canActivate:[KeycloakAuthGuard] },
on met ca en producs 

{ path: 'products', component: Products,canActivate:[KeycloakAuthGuard] ,data: { roles: ['ADMIN'] } },
{ path: 'customers', component: Customers,canActivate:[KeycloakAuthGuard] ,data: { roles: ['USER'] } },
KeycloakAngularModule
et on importe 
> Un utilisateur non connecté est redirigé vers Keycloak. Un utilisateur connecté sans le rôle requis n'accède pas à la route.
ya prob 403 on descatove clein seret
> 
> et on autorize ca
> ![74.png](images/74.png)
> je va dan mon app angualr  je fait  products et voila keyclock auth
> ![75.png](images/75.png)
> e voial nos products
> ![76.png](images/76.png)
---

## 13. Connexion, déconnexion et affichage du profil

### 13.1 Composant principal

`src/app/app.component.ts`

```ts
import { Component, OnInit } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit {
  public isLoggedIn = false;
  public username = '';

  constructor(private keycloak: KeycloakService) {}

  async ngOnInit(): Promise<void> {
    this.isLoggedIn = await this.keycloak.isLoggedIn();
    if (this.isLoggedIn) {
      this.username = this.keycloak.getUsername();
    }
  }

  handleLogin(): void {
    this.keycloak.login();
  }

  handleLogout(): void {
    this.keycloak.logout(window.location.origin);
  }

  hasRole(role: string): boolean {
    return this.keycloak.isUserInRole(role);
  }
}
```

### 13.2 Menu et bouton selon l'état de connexion

Dans `app.component.html`, ajoutez à la barre de navigation :

```html
<ul class="navbar-nav me-auto">
  <li class="nav-item" *ngIf="hasRole('admin')">
    <a class="nav-link" routerLink="products">Products</a>
  </li>
  <li class="nav-item" *ngIf="hasRole('user')">
    <a class="nav-link" routerLink="customers">Customers</a>
  </li>
</ul>

<div class="d-flex align-items-center">
  <span class="me-3" *ngIf="isLoggedIn">{{ username }}</span>

  <button *ngIf="!isLoggedIn" class="btn btn-success" (click)="handleLogin()">
    Login
  </button>
  <button *ngIf="isLoggedIn" class="btn btn-danger" (click)="handleLogout()">
    Logout
  </button>
</div>
```

> **Contextualisation :** le menu n'affiche que les liens auxquels l'utilisateur a accès. C'est une aide pour l'interface. La **vraie** sécurité reste côté backend et dans le guard.
on tese avec user 2 on voit products
> 
> ![77.png](images/77.png)
> ms avec users on vos aps products car [a aamdin]
---
![78.png](images/78.png)

pour amliorr form login
![79.png](images/79.png) avec rrememeber me , pasarod foregted ec
s
![80.png](images/80.png)

on asisgn roel user by default to new users
![81.png](images/81.png) et voial c assinr
on cree run user ndee notre app
![82.png](images/82.png)
![83.png](images/83.png)
on cifg user2 OTP:
![84.png](images/84.png)
et on voie qau il dmenae und code op qui doi tre utlise avc app mbile autheticator
![85.png](images/85.png)
uen fois on scanne on voix u  code et on le siasit
 ms si on veux par mail parr phone on les configure
puis on va a athentif et policies password et on seppeciife le sporlicies
![86.png](images/86.png)
pour form auth
on peut soit cre rnos propre them eou tilsier ceux esixtnat depusi icic 

![87.png](images/87.png)
## 14. Tester le frontend

| Test | Résultat attendu |
|---|---|
| Ouvrir `localhost:4200` sans être connecté | Interface visible, bouton Login |
| Cliquer sur `products` sans être connecté | Redirection vers Keycloak |
| Se connecter avec `user1` | Affiche `customers`, pas `products` |
| Accéder à `/products` avec `user1` | Refus (route protégée par le rôle `admin`) |
| Se connecter avec `user2` | Affiche `products` et `customers` |
| Afficher la liste des produits avec `user2` | Liste affichée (token envoyé automatiquement) |
| Cliquer sur Logout | Déconnexion, retour à la page d'accueil |

---

## 15. Formulaire de connexion et inscription (Keycloak)

Dans Keycloak : **Realm settings > Login** :

- **User registration** : active le lien d'inscription.
- **Forgot password** : active la réinitialisation du mot de passe.
- **Remember me** : garder la session.
- **Email as username** : connexion avec l'adresse e-mail.

Rôle par défaut pour les nouveaux inscrits : **Realm roles > default-roles-bdcc** (ou à configurer dans les rôles par défaut du realm). Ainsi, un utilisateur inscrit reçoit le rôle `user`.

---

## 16. Double authentification (OTP) pour un utilisateur

1. Keycloak : **Users > user2 > Required user actions > Configure OTP** > **Save**.
2. Connectez-vous à Angular avec `user2`.
3. Keycloak affiche un QR code. Scannez-le avec **Google Authenticator** (ou un équivalent).
4. Saisissez le code à 6 chiffres affiché dans l'application.

> Le professeur indique un code qui change toutes les 15 secondes. La valeur par défaut de Keycloak est de 30 secondes.

---

## 17. Politique de mots de passe (rappel)

Dans **Authentication > Policies > Password policy**, le professeur configure notamment :

- Expiration : 360 jours
- Not recently used : 3 derniers mots de passe refusés
- Longueur minimale : 8
- Interdiction de contenir le nom d'utilisateur ou l'adresse e-mail
- Au moins 1 caractère spécial, 1 majuscule, 1 chiffre

Les utilisateurs qui s'inscrivent ou changent de mot de passe doivent respecter ces règles.

---

## 18. Résumé du fonctionnement

1. L'utilisateur ouvre une route protégée dans Angular.
2. Le guard vérifie l'authentification. Sinon, il redirige vers Keycloak.
3. Keycloak authentifie l'utilisateur et renvoie vers Angular avec un token.
4. L'intercepteur Keycloak ajoute `Authorization: Bearer <token>` à chaque requête.
5. Spring Security vérifie la signature du JWT grâce à la clé publique de Keycloak.
6. Le convertisseur lit `realm_access.roles` et crée les autorités.
7. Les règles `hasAuthority(...)` autorisent ou refusent l'accès (**401** si non authentifié, **403** si le rôle manque).

---

## 19. Erreurs rencontrées dans le cours et solutions

| Erreur                             | Cause | Solution |
|------------------------------------|---|---|
| `Address already in use: 8180`     | Jenkins ou autre service utilise le port | Lancer Keycloak sur `8180` |
| `401` sur `/api/products`          | Pas de token ou token expiré | Obtenir un nouveau token, ou utiliser le refresh token |
| `403` sur la console H2            | Route non autorisée, ou frames bloquées | `permitAll()` sur `/h2-console/**` et `frameOptions.disable()` |
| `403` avec un token valide         | Rôle insuffisant, ou rôles non convertis | Vérifier le convertisseur et la section `authorities` de `/api/auth` |
| `blocked by CORS policy`           | Pas de configuration CORS, ou CORS ignoré par Spring Security | Bean `CorsConfigurationSource` + `http.cors(...)` |
| `Invalid client secret` (Angular)  | Client authentication activé pour le frontend | Désactiver **Client authentication** pour `app-ang` |
| `unauthorized_client` avec Postman | Direct access grants désactivé | Cocher **Direct access grants** sur le client |
| `No provider for KeycloakService`  | Module Keycloak non importé | Ajouter `KeycloakAngularModule` dans `app.module.ts` |
| Erreur de compilation sur `router` | Angular 17+ demande `override` | Ajouter `protected override readonly router` |
| Redirection en boucle après login  | Route ou URL de redirection mal configurée | Vérifier **Valid redirect URIs** et **Web origins** dans Keycloak |

---

## 20. Pour la suite

Le professeur indique que le prochain cours montrera :
- les thèmes personnalisés pour la page de connexion Keycloak (un thème par défaut est copié, puis modifié en CSS) ;
- les identity providers (GitHub, Facebook, Google) ;
- la configuration complète de Google, avec son client ID et son client secret.






## Étape 3 : Créer le microservice `order-service`

### 3.1 Créer le module

Dans le projet parent (le même que `inventory-service`) :

1. Clic droit sur le projet > **New** > **Module** > **Spring Initializr**.
2. Paramètres :
    - Name : `order-service`
    - Group : `ma.enset` (ou le groupe utilisé dans votre projet)
    - Java : **21**
    - Spring Boot : **3.2.2**

### 3.2 Dépendances

- **Spring Web**
- **Spring Data JPA**
- **H2 Database**
- **Lombok**
- **Eureka Discovery Client** (désactivé plus tard dans `application.properties`)
- **Config Client** (désactivé plus tard)
- **OAuth2 Resource Server**
- **OpenFeign** (`spring-cloud-starter-openfeign`) : permet de communiquer avec `inventory-service`
![88.png](images/88.png)
Ajouter aussi la documentation Swagger, comme le professeur (`springdoc-openapi`) :

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

> Si Maven signale un conflit avec Spring Cloud, utilisez le BOM Spring Cloud compatible avec Spring Boot 3.2 (série `2023.0.x`).

### 3.3 Packages

Créer ces packages dans `src/main/java/<votre.package>/` :

```
entities/
repositories/
service/        (prévu pour plus tard)
web/
security/       (copié depuis inventory-service)
feign/
model/
```

---

## Étape 4 : Configurer `order-service`

Fichier `src/main/resources/application.properties` :

```properties
server.port=8088
spring.application.name=order-service

spring.datasource.url=jdbc:h2:mem:order-db
spring.h2.console.enabled=true

spring.cloud.config.enabled=false
spring.cloud.discovery.enabled=false

spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/bdcc/protocol/openid-connect/certs
```

> **Important :** si Keycloak est sur `8180`, remplacez `8080` par `8180` dans `jwk-set-uri`.
>
> Le professeur a oublié ces deux propriétés au départ : sans `jwk-set-uri`, le service ne peut pas vérifier la signature du JWT. Vous obtiendrez alors une erreur au démarrage (`Unable to find a JWT decoder` ou similaire).

---

## Étape 5 : Créer les entités

### 5.1 Énumération `OrderState`

`entities/OrderState.java`

```java
package ma.enset.orderservice.entities;

public enum OrderState {
    PENDING,
    CONFIRMED,
    CANCELLED
}
```

### 5.2 Entité `Order`

`entities/Order.java`

```java
package ma.enset.orderservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
// Le nom "order" est un mot réservé en SQL (ORDER BY) : on nomme la table "orders"
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private String id;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    @OneToMany(mappedBy = "order")
    private List<ProductItem> productItems;
}
```

> **Pourquoi `@Table(name = "orders")` ?** Le professeur a rencontré une erreur lors de la création de la table : `order` est un mot-clé SQL. Le nom de la table doit donc être différent.

### 5.3 Entité `ProductItem`

`entities/ProductItem.java`

```java
package ma.enset.orderservice.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import ma.enset.orderservice.model.Product;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identifiant du produit dans inventory-service
    private String productId;

    private double price;
    private int quantity;

    @ManyToOne
    // WRITE_ONLY : évite la boucle infinie order -> productItems -> order -> ...
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Order order;

    // Information récupérée depuis inventory-service (pas une colonne en base)
    @Transient
    private Product product;
}
```

> **Boucle infinie :** la relation est bidirectionnelle (`Order` contient une liste de `ProductItem`, et `ProductItem` contient un `Order`). Lors de la sérialisation JSON, Jackson tourne en boucle. Le professeur propose une solution rapide avec `@JsonProperty`. La solution recommandée est d'utiliser des **DTO**, présentés plus tard dans le cours.
>
> **`@Transient` :** l'attribut `product` ne doit pas être enregistré en base. Il sert uniquement à afficher le détail du produit.

---

## Étape 6 : Créer les repositories

`repositories/OrderRepository.java`

```java
package ma.enset.orderservice.repositories;

import ma.enset.orderservice.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
```

`repositories/ProductItemRepository.java`

```java
package ma.enset.orderservice.repositories;

import ma.enset.orderservice.entities.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductItemRepository extends JpaRepository<ProductItem, Long> {
}
```

---

## Étape 7 : Client Feign vers `inventory-service`

### 7.1 Modèle `Product` côté order-service

Le professeur copie la classe `Product` de l'inventaire et supprime les annotations JPA. Il ne reste que les attributs.

`model/Product.java`

```java
package ma.enset.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String id;
    private String name;
    private double price;
    private int quantity;
}
```

### 7.2 Interface Feign

`feign/ProductRestClient.java`

```java
package ma.enset.orderservice.feign;

import ma.enset.orderservice.model.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

// Dans le cours, l'URL est écrite en dur. Avec Eureka, on mettra simplement le nom du service.
@FeignClient(name = "inventory-service", url = "http://localhost:8087")
public interface ProductRestClient {

    @GetMapping("/api/products")
    List<Product> getAllProducts();

    @GetMapping("/api/products/{id}")
    Product getProductById(@PathVariable("id") String id);
}
```

### 7.3 Activer Feign

Dans la classe principale :

```java
@SpringBootApplication
@EnableFeignClients
public class OrderServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
```

Import : `org.springframework.cloud.openfeign.EnableFeignClients`.

---

## Étape 8 : Préparer `inventory-service` (identifiants fixes)

Le professeur constate qu'au démarrage, `order-service` appelle `inventory-service` pour récupérer les produits. Comme `inventory-service` est sécurisé, cet appel échoue avec **401**, car il n'y a pas de token au démarrage.

La solution retenue est de **ne pas appeler l'inventaire au démarrage** et d'utiliser des identifiants fixes. Dans `inventory-service`, modifier le `CommandLineRunner` :

```java
@Bean
CommandLineRunner start(ProductRepository productRepository) {
    return args -> {
        productRepository.save(Product.builder()
                .id("P01").name("Computer").price(1200).quantity(10).build());
        productRepository.save(Product.builder()
                .id("P02").name("Printer").price(100).quantity(34).build());
        productRepository.save(Product.builder()
                .id("P03").name("Smart Phone").price(4000).quantity(5).build());
    };
}
```

Redémarrer `inventory-service`.

> Le professeur précise que, dans la réalité, un service ne démarre pas en interrogeant un autre service pour insérer ses données. Ici, c'est seulement pour le test.

---

## Étape 9 : Créer les données de test dans `order-service`

`InventoryServiceApplication` devient `OrderServiceApplication` dans le package de `order-service`. Ajouter le `CommandLineRunner` :

```java
@Bean
CommandLineRunner start(OrderRepository orderRepository,
                        ProductItemRepository productItemRepository) {
    return args -> {
        List<String> productIds = List.of("P01", "P02", "P03");
        Random random = new Random();

        for (int i = 1; i <= 5; i++) {
            Order order = Order.builder()
                    .id(UUID.randomUUID().toString())
                    .date(LocalDate.now())
                    .state(OrderState.PENDING)
                    .build();
            orderRepository.save(order);

            for (String productId : productIds) {
                ProductItem item = ProductItem.builder()
                        .productId(productId)
                        .price(Math.random() * 6000 + 1000)  // prix aléatoire, pour le test
                        .quantity(1 + random.nextInt(20))
                        .order(order)
                        .build();
                productItemRepository.save(item);
            }
        }
    };
}
```

Imports nécessaires : `java.time.LocalDate`, `java.util.List`, `java.util.Random`, `java.util.UUID`.

> Le professeur crée d'abord des commandes sans sécurité, puis vérifie dans la console H2. La sécurité est ajoutée à l'étape suivante.

Démarrer `order-service`, puis ouvrir `http://localhost:8088/h2-console` avec la JDBC URL `jdbc:h2:mem:order-db`. Vérifier que les tables `orders` et `product_item` contiennent les données.
et voial don nos orders bein ajoute ts

![89.png](images/89.png)

e nos products itmes bien ajotue
![90.png](images/90.png)

---

## Étape 10 : Sécurité de `order-service`

### 10.1 Copier la configuration

Copier le package `security/` de `inventory-service` :

- `SecurityConfig`
- `KeycloakJwtConverter`

Dans `SecurityConfig`, adapter les règles d'accès.

**Première version (développement, sans token) :**

```java
.authorizeHttpRequests(ar -> ar
    .requestMatchers("/h2-console/**").permitAll()
    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
    .requestMatchers("/api/orders/**").permitAll()
    .anyRequest().authenticated())
```

**Version finale (sécurisée) :**

```java
.authorizeHttpRequests(ar -> ar
    .requestMatchers("/h2-console/**").permitAll()
    .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
    .requestMatchers("/api/orders/**").hasAuthority("user")
    .anyRequest().authenticated())
```

> Ne pas oublier `http.cors(Customizer.withDefaults())` et le bean `CorsConfigurationSource`, comme dans `inventory-service`. Sans cela, Angular ne peut pas appeler `localhost:8088`.
> 

### 10.2 Documentation Swagger

Ouvrir `http://localhost:8088/swagger-ui.html`. Si la page demande une authentification, vérifier que les chemins `/swagger-ui/**` et `/v3/api-docs/**` sont autorisés (étape 10.1).

---

## Étape 11 : Contrôleur REST des commandes

`web/OrderRestController.java`

```java
package net.tayebi.orderservice.web;

import lombok.RequiredArgsConstructor;
import net.tayebi.orderservice.entities.Order;
import net.tayebi.orderservice.entities.ProductItem;
import net.tayebi.orderservice.feign.InventoryRestClient;
import net.tayebi.orderservice.repositories.OrderRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderRestController {
    private OrderRepository orderRepository;
    private InventoryRestClient inventoryRestClient;

    public OrderRestController(OrderRepository orderRepository, InventoryRestClient inventoryRestClient) {
        this.orderRepository = orderRepository;
        this.inventoryRestClient = inventoryRestClient;
    }

    @GetMapping("/orders")
    public List<Order> findAllOrders(){
        List<Order> allOrders = orderRepository.findAll();
//        allOrders.forEach(o->{
//            o.getProductItems().forEach(pi->{
//                pi.setProduct(inventoryRestClient.findProductById(pi.getProductId()));
//            });
//        });
        return allOrders;
    }
    @GetMapping("/orders/{id}")
    public Order findOrderById(@PathVariable String id){
        Order order = orderRepository.findById(id).get();
//        order.getProductItems().forEach(pi->{
//            pi.setProduct(inventoryRestClient.findProductById(pi.getProductId()));
//        });
        return order;
    }
}

```

Pour la liste (`/api/orders`), le professeur ne recharge pas les produits. Vous pouvez le faire de la même manière dans une boucle.

Redémarrer `order-service`.

---

on et est:http://localhost:8088/api/orders
![91.png](images/91.png)

on ets aussi avc ca
http://localhost:8088/api/orders/b926d1af-31e3-4309-9f2f-1ce8a4a4cd0c
e ca marce
![92.png](images/92.png)

et on apres vaori modfe en conrotller pour voir mme proedutsietmes ca marche don:
http://localhost:8088/api/orders
![94.png](images/94.png)
http://localhost:8088/api/orders/08ce3560-4aee-453e-b53c-6c99d06476d5
![95.png](images/95.png)
## Étape 12 : Propager le

on jaute sawagger en pemrit all
on tetse swagger ca mrche
![93.png](images/93.png)
et voial on essaye
avec id dans swagger
e o voial orders bu id saffiche avce ses prducts
![96.png](images/96.png) 
on suppr ca de onifg pur ca dmenagde auth
"/api/orders/**"
et err 401 bien vu: si on comuslte  http://localhost:8088/api/orders

![97.png](images/97.png)
mainntnn il dmenade auth donc on satuh depuis ttp slcient de intellij e on nevoie rq apres
Quand le frontend appelle `order-service` avec un token, `order-service` appelle ensuite `inventory-service`. Sans token, l'appel est refusé (**401**). Il faut donc transmettre le même token dans l'appel Feign.
on fiat donc ca
POST http://localhost:8180/realms/enset-bdcc-realme/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

grant_type=password&username=user2&password=Wyuw6089?&client_id=ecom-client-ang

![98.png](images/98.png)
GET http://localhost:8088/api/orders
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJGeVFaTXNzbEpVUTM3ZUx5SFlKY0RpMmVHRUZ2VTRrbGhwbjh3bXBVclZJIn0.eyJleHAiOjE3OTU4NjQyOTksImlhdCI6MTc5NTg2Mzk5OSwianRpIjoib25ydHJvOmQwNDU4ZTcxLWQwMzAtY2IwOC04NDljLTA0NzQyNGM5ZWVmZCIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODE4MC9yZWFsbXMvZW5zZXQtYmRjYy1yZWFsbWUiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZDI2Zjk2OTUtZWVlYS00Y2ZjLTg2NzItNjFiODczZjI4MjkwIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiZWNvbS1jbGllbnQtYW5nIiwic2lkIjoiNXM2NFVmcnptQlRnVFBFLU5qc0dkYXlLIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjQyMDAiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiQURNSU4iLCJ1bWFfYXV0aG9yaXphdGlvbiIsIlVTRVIiLCJkZWZhdWx0LXJvbGVzLWVuc2V0LWJkY2MtcmVhbG1lIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiaW1hbmUgdGF5ZWJpIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidXNlcjIiLCJnaXZlbl9uYW1lIjoiaW1hbmUiLCJmYW1pbHlfbmFtZSI6InRheWViaSIsImVtYWlsIjoidXNlcjJAZ21haWwuY29tIn0.b5_Cisp6YtcUXgYXNUotDSUykBsYMdiRkBtlK6VZDRo0AdXj019N101pHusftBF-bTSQicSxbMZoHEV8XTF7B2GqcZO89JMh1BwUo8MWgYtVCDBwfI8I8j2ZF-b6LsmoXQvFOf0eyTmxdR0S-nbeNmuxBqvDFESVjzZelQ-saJAF0Q8h55ewGfZrhG47eiKdjfhwmPAldmWFLoAkMtDQTysEyOhvPU9nDoTBP2wQ6ifsssm4BQ2fMnzJ6gmkgKNTi9icg4reR1bNoqQ5kSm85pyu92rtFzh41plcpmh4amUqGCldA8rN2_gXUu4e8l5Hyj1VkI7JPGQ8IRZ6wW4uGw
![99.png](images/99.png) a liste de spr orders avce leus prods
maintn eon apsse agrnner notre fornts compnennt

`feign/FeignInterceptor.java`

```java
package ma.enset.orderservice.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            String token = jwtAuth.getToken().getTokenValue();
            template.header("Authorization", "Bearer " + token);
        }
    }
}
```

> **Fonctionnement :** à chaque requête Feign, l'intercepteur lit le contexte de sécurité de la requête entrante, récupère le JWT et l'ajoute dans le header `Authorization`.
>
> **Astuce du professeur :** pour vérifier le type de l'objet, affichez `authentication.getClass().getName()` dans l'intercepteur. Le type affiché est `JwtAuthenticationToken`.

---

## Étape 13 : Tester avec Postman

### 13.1 Obtenir un token

- **Méthode :** `POST`
- **URL :** `http://localhost:8080/realms/bdcc/protocol/openid-connect/token`
- **Header :** `Content-Type: application/x-www-form-urlencoded`
- **Body :**

| Clé | Valeur |
|---|---|
| `grant_type` | `password` |
| `username` | `user2` |
| `password` | `1234` |
| `client_id` | `eCom client ang` (votre client Angular) |

Copier `access_token`.

> Si Keycloak répond `unauthorized_client`, activez **Direct access grants** sur le client.

### 13.2 Appeler `order-service`

- **Méthode :** `GET`
- **URL :** `http://localhost:8088/api/orders`
- **Header :** `Authorization: Bearer <access_token>`

Résultat attendu : la liste des commandes avec leurs `productItems`.

Tester aussi `http://localhost:8088/api/orders/<id>` avec un ID de commande. La réponse doit contenir le nom du produit (`product.name`) grâce à l'appel Feign.

Sans header : **401**.

---

## Étape 14 : Frontend Angular : liste des commandes

### 14.1 Créer le composant

Dans le projet Angular :

```bash
ng g c ui/orders
```
![100.png](images/100.png)

### 14.2 Code du composant liste

`src/app/ui/orders/orders.component.ts`

```ts
import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-orders',
  templateUrl: './orders.component.html',
  styleUrls: ['./orders.component.css'],
})
export class OrdersComponent implements OnInit {
  public orders: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<any[]>('http://localhost:8088/api/orders').subscribe({
      next: (data) => (this.orders = data),
      error: (err) => console.error(err),
    });
  }
}
```

`src/app/ui/orders/orders.component.html`

```html
<h2>Orders</h2>
<table class="table">
  <thead>
    <tr>
      <th>ID</th>
      <th>Date</th>
      <th>State</th>
      <th>Products</th>
      <th>Détail</th>
    </tr>
  </thead>
  <tbody>
    <tr *ngFor="let order of orders">
      <td>{{ order.id }}</td>
      <td>{{ order.date | date: 'dd/MM/yyyy' }}</td>
      <td>{{ order.state }}</td>
      <td>{{ order.productItems?.length }}</td>
      <td>
        <button class="btn btn-primary btn-sm" (click)="showDetails(order)">
          Voir
        </button>
      </td>
    </tr>
  </tbody>
</table>
```

Ajouter `showDetails` dans la classe (étape 15).

### 14.3 Route protégée

Dans `app-routing.module.ts` :

```ts
import { OrdersComponent } from './ui/orders/orders.component';
import { OrderDetailsComponent } from './ui/order-details/order-details.component';

const routes: Routes = [
  // ... routes existantes ...
  {
    path: 'orders',
    component: OrdersComponent,
    canActivate: [AuthGuard],
    data: { roles: ['user'] },
  },
  {
    path: 'orders/:id',
    component: OrderDetailsComponent,
    canActivate: [AuthGuard],
    data: { roles: ['user'] },
  },
];
```

Ajouter un lien `Orders` dans la barre de navigation (`app.component.html`) :

```html
<li class="nav-item" *ngIf="hasRole('user')">
  <a class="nav-link" routerLink="orders">Orders</a>
</li>
```

### 14.4 Ajouter `HttpClientModule`

Dans `app.module.ts`, `HttpClientModule` doit être dans les `imports` (déjà fait si vous avez suivi le guide 2).

---

## Étape 15 : Détail d'une commande

### 15.1 Navigation depuis la liste

Dans `orders.component.ts`, ajouter `Router` :

```ts
import { Router } from '@angular/router';

constructor(private http: HttpClient, private router: Router) {}

showDetails(order: any): void {
  this.router.navigateByUrl('/orders/' + order.id);
}
```

### 15.2 Composant détail

```bash
ng g c ui/order-details
```

Le professeur a créé le composant dans le dossier `ui`. Le composant doit être déclaré dans `app.module.ts` (ou importé si vous êtes en standalone).

`src/app/ui/order-details/order-details.component.ts`

```ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-order-details',
  templateUrl: './order-details.component.html',
  styleUrls: ['./order-details.component.css'],
})
export class OrderDetailsComponent implements OnInit {
  public orderDetails: any;

  constructor(private route: ActivatedRoute, private http: HttpClient) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.http.get<any>('http://localhost:8088/api/orders/' + id).subscribe({
      next: (data) => (this.orderDetails = data),
      error: (err) => console.error(err),
    });
  }

  getTotal(): number {
    let total = 0;
    for (let item of this.orderDetails.productItems) {
      total += item.price * item.quantity;
    }
    return total;
  }
}
```

`src/app/ui/order-details/order-details.component.html`

```html
<div *ngIf="orderDetails">
  <h2>Commande {{ orderDetails.id }}</h2>

  <ul class="list-group mb-3">
    <li class="list-group-item">
      Date : {{ orderDetails.date | date: 'dd/MM/yyyy' }}
    </li>
    <li class="list-group-item">
      État : {{ orderDetails.state }}
    </li>
  </ul>

  <table class="table">
    <thead>
      <tr>
        <th>Produit</th>
        <th>Prix unitaire</th>
        <th>Quantité</th>
        <th>Montant</th>
      </tr>
    </thead>
    <tbody>
      <tr *ngFor="let item of orderDetails.productItems">
        <td>{{ item.product?.name }}</td>
        <td>{{ item.price | number: '1.2-2' }}</td>
        <td>{{ item.quantity }}</td>
        <td>{{ item.price * item.quantity | number: '1.2-2' }}</td>
      </tr>
    </tbody>
    <tfoot>
      <tr>
        <th colspan="3">Total</th>
        <th>{{ getTotal() | number: '1.2-2' }}</th>
      </tr>
    </tfoot>
  </table>
</div>
```

> **Erreur fréquente :** `Cannot read property 'date' of undefined`. Elle apparaît si on affiche les données avant leur chargement. Le `*ngIf="orderDetails"` règle ce problème.
>
> **Formatage :** `number: '1.2-2'` affiche deux décimales. `date: 'dd/MM/yyyy'` formate la date.

---

## Étape 16 : Tester l'ensemble

1. Démarrer dans cet ordre : Keycloak, `inventory-service` (8087), `order-service` (8088), puis Angular (`ng serve`, port 4200).
2. Ouvrir `http://localhost:4200`, cliquer sur **Login**, puis se connecter avec `user2`.
3. Cliquer sur **Orders** : la liste doit s'afficher.
4. Cliquer sur **Voir** : le détail avec le nom des produits, les prix, les quantités et le total doit s'afficher.
5. Se déconnecter, puis se connecter avec `user1` (rôle `user` uniquement) : la liste des commandes doit rester accessible, la liste des produits (`/products`) doit être refusée.

Vérifier aussi dans les outils de développement (F12, onglet **Réseau**) que la requête `GET /api/orders` contient bien l'en-tête `Authorization: Bearer ...`.

---

## Étape 17 : Problèmes rencontrés et solutions

| Erreur | Cause | Solution |
|---|---|---|
| `Table "ORDER" not found` ou erreur SQL sur la table `order` | `order` est un mot réservé SQL | `@Table(name = "orders")` |
| Boucle infinie / `StackOverflowError` en JSON | Relation bidirectionnelle `Order` <-> `ProductItem` | `@JsonProperty(access = WRITE_ONLY)` sur `order`, ou DTO |
| `401` au démarrage de `order-service` | Appel Feign vers un service sécurisé sans token | Ne pas appeler l'inventaire au démarrage (identifiants fixes P01, P02, P03) |
| `401` sur `inventory-service` depuis `order-service` | Token non transmis | Intercepteur Feign `FeignInterceptor` |
| `No JWT decoder` ou `Unable to find ...` au démarrage | `jwk-set-uri` absent | Ajouter la propriété dans `application.properties` |
| `blocked by CORS policy` sur le port 8088 | CORS non configuré dans `order-service` | Copier le bean `CorsConfigurationSource` et `http.cors(...)` |
| `Cannot read property 'date' of undefined` | Données affichées avant chargement | `*ngIf="orderDetails"` |
| Erreur de compilation sur `for each` | Syntaxe TypeScript incorrecte | Utiliser `for (let item of ...)` |
| Total affiché incorrect | Attribut mal orthographié | Vérifier `item.price` et `item.quantity` |

---

## Étape 18 : À faire ensuite (évoqué par le professeur)

- Créer une couche **service** et des **DTO** pour séparer les entités des réponses JSON.
- Ajouter **Gateway** (Spring Cloud Gateway), **Eureka Discovery** et **Config Server** pour finaliser l'architecture microservices.
- Remplacer les URL écrites en dur (`localhost:8087`) par le nom des services (`inventory-service`) dans Feign.
- Ajouter une méthode de formatage pour les montants et les dates.

---

## Correspondances avec votre installation

| Élément du cours | Votre installation |
|---|---|
| Realm `bdcc` | `enset-bdcc-realme` |
| Keycloak `8080` | `8180` |
| Client `eCom client ang` | Le Client ID exact de votre client Angular |
| Inventory `8087` | Inchangé |
| Order `8088` | Inchangé |

Si vous utilisez `8180` et `enset-bdcc-realme`, remplacez-les dans toutes les URL de ce guide : `application.properties`, `ProductRestClient` (si besoin), `initializeKeycloak` (Angular) et Postman.