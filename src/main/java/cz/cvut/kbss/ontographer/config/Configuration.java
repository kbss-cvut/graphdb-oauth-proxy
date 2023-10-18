package cz.cvut.kbss.ontographer.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Valid
@ConfigurationProperties(prefix = "proxy.graphdb")
public record Configuration(@NotBlank String url, @NotBlank String username, @NotBlank String password) {
}
