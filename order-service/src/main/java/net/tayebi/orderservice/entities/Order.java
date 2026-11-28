package net.tayebi.orderservice.entities;

import jakarta.persistence.*;
import lombok.*;
import net.tayebi.orderservice.enums.OrderState;

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
