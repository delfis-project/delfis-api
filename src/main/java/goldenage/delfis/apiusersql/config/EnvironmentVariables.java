package goldenage.delfis.apiusersql.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvironmentVariables {
    @Bean
    public Dotenv loadEnv() {
        Dotenv env = Dotenv.configure().load();

        // Carregar as variáveis de ambiente
        String applicationName = env.get("SPRING_APPLICATION_NAME");
        String databaseUrl = env.get("SPRING_DATASOURCE_URL");
        String databaseUsername = env.get("SPRING_DATASOURCE_USERNAME");
        String databasePassword = env.get("SPRING_DATASOURCE_PASSWORD");
        String ddlAuto = env.get("SPRING_JPA_HIBERNATE_DDL_AUTO");

        // Definir as variáveis de ambiente como propriedades do sistema
        System.setProperty("SPRING_APPLICATION_NAME", applicationName);
        System.setProperty("SPRING_DATASOURCE_URL", databaseUrl);
        System.setProperty("SPRING_DATASOURCE_USERNAME", databaseUsername);
        System.setProperty("SPRING_DATASOURCE_PASSWORD", databasePassword);
        System.setProperty("SPRING_JPA_HIBERNATE_DDL_AUTO", ddlAuto);

        return env;
    }
}