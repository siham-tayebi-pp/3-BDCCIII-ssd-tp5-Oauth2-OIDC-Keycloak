package net.tayebi.orderservice;

import net.tayebi.orderservice.entities.Order;
import net.tayebi.orderservice.entities.ProductItem;
import net.tayebi.orderservice.enums.OrderState;
import net.tayebi.orderservice.repositories.OrderRepository;
import net.tayebi.orderservice.repositories.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
@EnableFeignClients

public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}
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
}
