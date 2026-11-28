
package net.tayebi.orderservice.web;

import lombok.RequiredArgsConstructor;
import net.tayebi.orderservice.entities.Order;
import net.tayebi.orderservice.entities.ProductItem;
import net.tayebi.orderservice.feign.InventoryRestClient;
import net.tayebi.orderservice.repositories.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderRepository orderRepository;
    private final InventoryRestClient inventoryRestClient;

    @GetMapping("/orders")
    public List<Order> findAllOrders() {
        List<Order> allOrders = orderRepository.findAll();

        allOrders.forEach(o -> {
            o.getProductItems().forEach(pi -> {
                pi.setProduct(
                        inventoryRestClient.findProductById(pi.getProductId())
                );
            });
        });

        return allOrders;
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> findOrderById(@PathVariable String id) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.getProductItems().forEach(pi -> {
                        pi.setProduct(
                                inventoryRestClient.findProductById(pi.getProductId())
                        );
                    });
                    return ResponseEntity.ok(order);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
