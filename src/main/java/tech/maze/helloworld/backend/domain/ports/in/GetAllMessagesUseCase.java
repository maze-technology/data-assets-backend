package tech.maze.helloworld.backend.domain.ports.in;

import java.util.List;
import tech.maze.helloworld.backend.domain.models.Message;

/**
 * Input port (use case) for retrieving all messages.
 */
public interface GetAllMessagesUseCase {
  /**
   * Retrieves all messages.
   *
   * @return a list of all messages
   */
  List<Message> getAllMessages();
}

