package com.commerce.gateway.route;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("coupon-api",
                        r -> r.path("/coupon/**")
                                .filters(f -> f.stripPrefix(1))
                                .uri("lb://COUPON-API"))
                .route("queue-for-reserve",
                        r -> r.path("/queue/**")
                                .filters(f -> f.stripPrefix(1))
                                .uri("lb://QUEUE-FOR-RESERVE"))
                .build();
    }

}
