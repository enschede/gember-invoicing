package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = App.class)
@ImportResource("classpath:application-*.xml")
@EnableJpaRepositories                                // Create JPA Repositories
@Import(RepositoryRestMvcAutoConfiguration.class)     // Expose REST API automatically
public class App {

    private static Class app = App.class;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(app, args);
    }

}
