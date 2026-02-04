package tech.maze.helloworld.backend;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the application.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@ConfigurationProperties(prefix = AppProperties.PREFIX)
public class AppProperties {
  public static final String PREFIX = "maze";

  String name;
}
