package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@ComponentScan(basePackageClasses = App.class)
@Import(RepositoryRestMvcAutoConfiguration.class)     // Expose REST API automatically
public class App {

    private static Class app = App.class;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(app, args);
    }

}
