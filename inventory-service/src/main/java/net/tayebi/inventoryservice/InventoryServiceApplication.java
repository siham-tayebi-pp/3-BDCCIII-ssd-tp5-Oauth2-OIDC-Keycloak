package net.tayebi.inventoryservice;

import net.tayebi.inventoryservice.entities.Product;
import net.tayebi.inventoryservice.repositories.ProductRepository;
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
					.id("P01").name("Computer").price(1200).quantity(10).build());
			productRepository.save(Product.builder()
					.id("P02").name("Printer").price(100).quantity(34).build());
			productRepository.save(Product.builder()
					.id("P03").name("Smart Phone").price(4000).quantity(5).build());
		};
	}
}