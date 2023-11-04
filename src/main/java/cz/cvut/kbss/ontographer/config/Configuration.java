package cz.cvut.kbss.ontographer.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Valid
@ConfigurationProperties(prefix = "proxy")
public class Configuration {

    private GraphDbConfig graphdb = new GraphDbConfig();

    private String url;

    public GraphDbConfig getGraphdb() {
        return graphdb;
    }

    public void setGraphdb(GraphDbConfig graphdb) {
        this.graphdb = graphdb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static class GraphDbConfig {

        @NotBlank
        private String url;

        @NotBlank
        private String username;

        @NotBlank
        private String password;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
