package tech.maze.helloworld.backend.domain.ports.out;

import java.util.List;
import tech.maze.helloworld.backend.domain.models.Message;

/**
 * Output port for loading messages from persistence.
 */
public interface LoadMessagePort {
  /**
   * Loads all messages.
   *
   * @return a list of all messages
   */
  List<Message> loadAllMessages();
}

