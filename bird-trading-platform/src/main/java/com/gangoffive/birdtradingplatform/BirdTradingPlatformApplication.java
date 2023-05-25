package com.gangoffive.birdtradingplatform;

import com.gangoffive.birdtradingplatform.config.AppProperties;
import com.gangoffive.birdtradingplatform.entity.Address;
import com.gangoffive.birdtradingplatform.security.oauth2.RegisterRequest;
import com.gangoffive.birdtradingplatform.service.AuthenticationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import static com.gangoffive.birdtradingplatform.enums.UserRole.ADMIN;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class BirdTradingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(BirdTradingPlatformApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(
//            AuthenticationService service
//    ) {
//        return args -> {
//            var admin = RegisterRequest.builder()
//                    .firstname("Admin")
//                    .lastname("Admin")
//                    .email("admin@mail.com")
//                    .password("password")
//                    .role(ADMIN)
////					.address(new Address("123", "123", "123", "123", "123"))
//                    .build();
//            System.out.println("Admin token: " + service.register(admin).getAccessToken());
//        };
//    }


}

	


