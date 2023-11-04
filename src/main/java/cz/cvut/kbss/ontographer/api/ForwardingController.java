package cz.cvut.kbss.ontographer.api;

import cz.cvut.kbss.ontographer.config.Configuration;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Forwards requests to the GraphDB server, using configured username and password for basic authentication.
 */
@RestController
public class ForwardingController {

    private static final Logger LOG = LoggerFactory.getLogger(ForwardingController.class);

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private final RestTemplate client = new RestTemplateBuilder().build();

    private final Configuration config;

    public ForwardingController(Configuration config) {
        this.config = config;
    }

    @RequestMapping("/**")
    public ResponseEntity<Resource> forward(@RequestBody(required = false) String body,
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

        // TODO It would be better to rewrite it to directly using a HTTP client
        final HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);
        final ResponseEntity<Resource> resp = client.exchange(uri, method, httpEntity, Resource.class);
        final HttpHeaders respHeaders = processResponseHeaders(request.getRequestURL().toString(), resp.getHeaders());
        return ResponseEntity.status(resp.getStatusCode())
                             .headers(respHeaders)
                             .body(resp.getBody());
    }

    private HttpHeaders processResponseHeaders(String requestUri, HttpHeaders respHeaders) {
        final HttpHeaders result = new HttpHeaders();
        result.addAll(respHeaders);
        removeDuplicatedTransferEncodingHeader(result);
        rewriteLocationHeaderToThisProxy(requestUri, result);
        return result;
    }

    private void removeDuplicatedTransferEncodingHeader(HttpHeaders respHeaders) {
        if (respHeaders.containsKey(HttpHeaders.TRANSFER_ENCODING)
                && Collections.singletonList("chunked").equals(respHeaders.get(HttpHeaders.TRANSFER_ENCODING))) {
            // Spring for some reason adds Transfer-Encoding: chunked header even though it is already present in the response
            // This causes errors in nginx, which checks for duplicate headers
            // So we remove the original Transfer-Encoding header so that there is only one in the end
            // See https://github.com/spring-projects/spring-framework/issues/21523 and https://github.com/spring-projects/spring-boot/issues/37646
            respHeaders.set(HttpHeaders.TRANSFER_ENCODING, null);
        }
    }

    private void rewriteLocationHeaderToThisProxy(String requestUri, HttpHeaders result) {
        if (result.containsKey(HttpHeaders.LOCATION)) {
            LOG.debug("Request URL is '{}'.", requestUri);
            final String ownUri = requestUri.substring(0, requestUri.indexOf(contextPath) + contextPath.length());
            assert result.getLocation() != null;
            final String loc = result.getLocation().toString();
            LOG.debug("Rewriting location header value from '{}' to '{}'.", loc, loc.replace(config.url(), ownUri));
            result.setLocation(URI.create(loc.replace(config.url(), ownUri)));
        }
    }
}
