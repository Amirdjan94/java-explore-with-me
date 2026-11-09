package client;

import dto.StatsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class StatClient {
    protected final RestTemplate rest;

    public StatClient(@Value("${stats.server.url:http://localhost:9090}") String statsServerUrl) {
        log.info("STATS SERVER URL = {}", statsServerUrl);
        rest = new RestTemplateBuilder()
                .uriTemplateHandler(new DefaultUriBuilderFactory(statsServerUrl))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build();
    }

    public ResponseEntity<List<StatsDto>> getAllStats(@Nullable Map<String, Object> parameters) {
        return makeAndSendListRequest(HttpMethod.GET, "stats", parameters, null);
    }

    public <T> void createStat(T body) {
        HttpEntity<T> requestEntity =
                new HttpEntity<>(body, defaultHeaders());

        try {
            ResponseEntity<Void> response = rest.exchange(
                    "hit",
                    HttpMethod.POST,
                    requestEntity,
                    Void.class
            );

            log.info("Статистика отправлена. Статус ответа: {}",
                    response.getStatusCode());

        } catch (HttpStatusCodeException e) {
            log.error("Ошибка отправки статистики: status={}, body={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString());
            throw e;

        } catch (org.springframework.web.client.RestClientException e) {
            log.error("Ошибка при обращении к stats-server", e);
            throw e;
        }
    }

    private <T> ResponseEntity<List<StatsDto>> makeAndSendListRequest(HttpMethod method, String path, @Nullable Map<String,
            Object> parameters, @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(path);
        if (parameters != null) {
            parameters.forEach((k, v) -> {
                if (v instanceof Collection<?> col) {
                    col.forEach(item -> builder.queryParam(k, item));   // uris=...&uris=...
                } else if (v != null) {
                    builder.queryParam(k, v);
                }
            });
        }
        String url = builder.encode().toUriString();
        log.info("GET {}", url);   // ← увидите реальный URL в логе

        ResponseEntity<List<StatsDto>> statServerResponse;
        try {
            statServerResponse = rest.exchange(url, method, requestEntity,
                    new ParameterizedTypeReference<List<StatsDto>>() {
                    });
        } catch (HttpStatusCodeException e) {
            log.error("Stats request failed: status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).build();
        }
        return prepareResponse(statServerResponse);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.ALL));
        return headers;
    }

    private static <R> ResponseEntity<R> prepareResponse(ResponseEntity<R> response) {
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
