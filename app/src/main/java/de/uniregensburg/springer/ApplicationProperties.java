package de.uniregensburg.springer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    private Properties properties = null;
    
    public ApplicationProperties() throws IOException {
        properties = new Properties();
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(is);
        }
    }

    public String getApiKey() {
        return properties.getProperty("springer.apikey");
    }
}
