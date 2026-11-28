package net.tayebi.orderservice.repositories;

import net.tayebi.orderservice.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<net.tayebi.orderservice.entities.Order, String> {
}
