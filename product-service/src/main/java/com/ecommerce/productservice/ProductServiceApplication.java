package com.ecommerce.productservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Product Service API",
                version = "1.0.0",
                description = "E-Commerce Product Service — manages product catalog and inventory.",
                contact = @Contact(
                        name = "Fong Yi Ann (0138026)",
                        email = "0138026@student.uow.edu.my"
                ),
                license = @License(
                        name = "University Assignment License",
                        url = "https://university.edu"
                )
        )
)
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }

}