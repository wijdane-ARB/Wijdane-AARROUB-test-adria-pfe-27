package ma.enset.security.gatewayservice;


import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutes {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("account-service", r -> r.path("/api/accounts/**", "/account/**")
                        .uri("lb://ACCOUNT-SERVICE"))
                .route("transaction-service", r -> r.path("/api/transactions/**", "/transaction/**")
                        .uri("lb://TRANSACTION-SERVICE"))
                .route("chatbot-service", r -> r.path("/api/chat/**")
                        .uri("http://localhost:8090"))
                .build();
    }
}
