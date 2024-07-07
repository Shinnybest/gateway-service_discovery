package com.commerce.gateway.exception.response;

import com.commerce.gateway.exception.CustomException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> map = new HashMap<>();
        Throwable throwable = getError(request);
        if (throwable instanceof CustomException) {
            var ex = (CustomException) getError(request);
            map.put("status", ex.getErrorCode().getHttpStatus());
            map.put("code", ex.getErrorCode().getCode());
            map.put("message", ex.getErrorCode().getMessage());
        } else {
            map.put("status", determineHttpStatus(throwable));
            map.put("message",throwable.getMessage());
        }
        return map;
    }

    private HttpStatusCode determineHttpStatus(Throwable throwable) {
        if (throwable instanceof ResponseStatusException) {
            return ((ResponseStatusException) throwable).getStatusCode();
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
