package net.tayebi.orderservice.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import net.tayebi.orderservice.model.Product;

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
