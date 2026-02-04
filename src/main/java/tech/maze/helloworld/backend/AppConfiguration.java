package tech.maze.helloworld.backend;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tech.maze.commons.CommonsConfiguration;

/**
 * Configuration class for the application.
 */
@Configuration
@Import({CommonsConfiguration.class})
@ComponentScan("tech.maze.helloworld.backend")
public class AppConfiguration {

}
