package tech.maze.helloworld.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point of the Spring Boot application.
 */
@SpringBootApplication
public class App {
  /**
   * The entry point of the Spring Boot application.
   *
   * @param args command-line arguments passed to the application
   */
  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }
}
