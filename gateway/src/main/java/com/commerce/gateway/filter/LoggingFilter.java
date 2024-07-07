package com.commerce.gateway.filter;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            var request = exchange.getRequest();
            var response = exchange.getResponse();

            log.info("Logging filter baseMessage. Application Name -> {}", config.getBaseMessage());

            if (config.preLogger) {
                log.info("Logging filter Start: request id -> {}", request.getId());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.postLogger) {
                    log.info("Logging filter End: response code -> {}", response.getStatusCode());
                }
            }));
        }));
    }

    @Data
    @Builder
    public static class Config {
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }
}
