package fr.jha.ecommerce.microcommerce;

import org.hibernate.annotations.SQLInsert;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class MicrocommerceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicrocommerceApplication.class, args);
    }

}
