package cz.cvut.kbss.ontographer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;

@Configuration
public class WebAppConfig implements WebMvcConfigurer {

    @Bean
    public HttpMessageConverter<?> stringMessageConverter() {
        return new StringHttpMessageConverter(StandardCharsets.UTF_8);
    }

    @Bean
    public HttpMessageConverter<?> resourceMessageConverter() {
        return new ResourceHttpMessageConverter();
    }
}
