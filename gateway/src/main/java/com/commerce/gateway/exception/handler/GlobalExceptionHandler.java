package com.commerce.gateway.exception.handler;

import com.commerce.gateway.exception.response.GlobalErrorAttributes;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.*;

@Component
@Order(-1)
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {
    
    public GlobalExceptionHandler(GlobalErrorAttributes globalErrorAttributes,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer serverCodecConfigurer) {
        super(globalErrorAttributes, new WebProperties.Resources(), applicationContext);
        super.setMessageReaders(serverCodecConfigurer.getReaders());
        super.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        ErrorAttributeOptions options = ErrorAttributeOptions.of(STATUS, EXCEPTION);
        Map<String, Object> map = getErrorAttributes(request, options);
        return ServerResponse.status((HttpStatusCode) map.get("status"))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(map));
    }
}
