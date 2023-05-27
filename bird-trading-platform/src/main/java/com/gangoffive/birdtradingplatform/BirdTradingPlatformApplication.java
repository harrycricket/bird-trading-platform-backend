package com.gangoffive.birdtradingplatform;

import com.gangoffive.birdtradingplatform.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class BirdTradingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(BirdTradingPlatformApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner commandLineRunner(
//            AuthenticationServiceImpl service
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

	


