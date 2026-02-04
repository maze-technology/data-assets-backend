package tech.maze.helloworld.backend.domain.ports.in;

import tech.maze.helloworld.backend.domain.models.Message;

/**
 * Input port (use case) for saving a message.
 */
public interface SaveMessageUseCase {
  /**
   * Saves a message.
   *
   * @param message the message to save
   * @return the saved message
   */
  Message saveMessage(Message message);
}

