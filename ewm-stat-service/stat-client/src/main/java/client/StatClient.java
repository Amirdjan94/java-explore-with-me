package client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;

public class StatClient {
    protected final RestTemplate rest;

    public StatClient(@Value("${stat-service.url}") String serverUrl) {
        rest = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    protected ResponseEntity<Object> getAllStats(@Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, "stats", parameters, null);
    }

    protected <T> ResponseEntity<Object> createStat(T body) {
        return makeAndSendRequest(HttpMethod.POST, "hit", null, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, @Nullable Map<String,
            Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        ResponseEntity<Object> statServerResponse;
        try {
            if (parameters != null) {
                statServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                statServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        return prepareResponse(statServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN));
        return headers;
    }

    private static ResponseEntity<Object> prepareResponse(ResponseEntity<Object> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
