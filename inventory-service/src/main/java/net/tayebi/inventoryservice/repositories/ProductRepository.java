package net.tayebi.inventoryservice.repositories;


import net.tayebi.inventoryservice.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<net.tayebi.inventoryservice.entities.Product, String> {
}
