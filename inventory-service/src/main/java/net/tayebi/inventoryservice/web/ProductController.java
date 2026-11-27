package net.tayebi.inventoryservice.web;

import net.tayebi.inventoryservice.entities.Product;
import net.tayebi.inventoryservice.repositories.ProductRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")

//@CrossOrigin("*")
public class ProductController {

    private final ProductRepository productRepository;

    // Injection par constructeur (bonne pratique)
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/products")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Product> productList() {
        return productRepository.findAll();
    }

    @GetMapping("/products/{id}")
    @PreAuthorize("hasAuthority('USER')")

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