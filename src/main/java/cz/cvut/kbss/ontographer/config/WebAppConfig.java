package cz.cvut.kbss.ontographer.config;

import cz.cvut.kbss.ontographer.Constants;
import cz.cvut.kbss.ontographer.servlet.AdjustedUriTemplateProxyServlet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ServletWrappingController;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

@Configuration
public class WebAppConfig {

    private final cz.cvut.kbss.ontographer.config.Configuration config;

    public WebAppConfig(cz.cvut.kbss.ontographer.config.Configuration config) {
        this.config = config;
    }

    /**
     * Register the proxy for SPARQL endpoint.
     *
     * @return Returns the ServletWrappingController for the SPARQL endpoint.
     */
    @Bean(name = "graphdbApiProxyServlet")
    public ServletWrappingController graphdbApiController() throws Exception {
        ServletWrappingController controller = new ServletWrappingController();
        controller.setServletClass(AdjustedUriTemplateProxyServlet.class);
        controller.setBeanName("graphdbApiProxyServlet");
        final Properties p = new Properties();
        p.setProperty("targetUri", config.url());
        p.setProperty("log", "false");
        p.setProperty(Constants.USERNAME_PARAM, config.username());
        p.setProperty(Constants.USERNAME_PARAM, config.password());
        controller.setInitParameters(p);
        controller.afterPropertiesSet();
        return controller;
    }

    @Bean
    public SimpleUrlHandlerMapping sparqlQueryControllerMapping() throws Exception {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(0);
        final Map<String, Object> urlMap = Collections.singletonMap("/*", graphdbApiController());
        mapping.setUrlMap(urlMap);
        return mapping;
    }
}
