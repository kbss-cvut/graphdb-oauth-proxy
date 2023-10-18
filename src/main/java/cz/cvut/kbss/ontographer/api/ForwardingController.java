package cz.cvut.kbss.ontographer.api;

import cz.cvut.kbss.ontographer.config.Configuration;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Enumeration;

/**
 * Forwards requests to the GraphDB server, using configured username and password for basic authentication.
 */
@RestController
public class ForwardingController {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private final RestTemplate client = new RestTemplateBuilder().build();

    private final Configuration config;

    public ForwardingController(Configuration config) {
        this.config = config;
    }

    @RequestMapping("/**")
    public ResponseEntity<String> forward(@RequestBody(required = false) String body,
                                          HttpMethod method, HttpServletRequest request) {
        final String requestUri = request.getRequestURI();

        URI uri = URI.create(config.url());
        uri = UriComponentsBuilder.fromUri(uri)
                .path(requestUri.substring(contextPath.length()))
                                  .query(request.getQueryString())
                                  .build(true).toUri();

        final HttpHeaders headers = new HttpHeaders();
        final Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            final String headerName = headerNames.nextElement();
            headers.set(headerName, request.getHeader(headerName));
        }
        headers.setBasicAuth(config.username(), config.password());

        final HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        try {
            return client.exchange(uri, method, httpEntity, String.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                                 .headers(e.getResponseHeaders())
                                 .body(e.getResponseBodyAsString());
        }
    }
}