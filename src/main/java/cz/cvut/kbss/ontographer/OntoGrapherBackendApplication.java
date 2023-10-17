package cz.cvut.kbss.ontographer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OntoGrapherBackendApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(OntoGrapherBackendApplication.class, args);
    }
}
