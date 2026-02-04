package tech.maze.helloworld.backend.domain.ports.out;

import tech.maze.helloworld.backend.domain.models.Message;

/**
 * Output port for saving messages to persistence.
 */
public interface SaveMessagePort {
  /**
   * Saves a message.
   *
   * @param message the message to save
   * @return the saved message
   */
  Message saveMessage(Message message);
}

