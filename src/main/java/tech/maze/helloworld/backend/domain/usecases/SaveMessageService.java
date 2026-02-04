package tech.maze.helloworld.backend.domain.usecases;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import tech.maze.helloworld.backend.domain.models.Message;
import tech.maze.helloworld.backend.domain.ports.in.SaveMessageUseCase;
import tech.maze.helloworld.backend.domain.ports.out.SaveMessagePort;

/**
 * Service implementing the SaveMessageUseCase.
 */
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class SaveMessageService implements SaveMessageUseCase {
  SaveMessagePort saveMessagePort;

  @Override
  public Message saveMessage(Message message) {
    return saveMessagePort.saveMessage(message);
  }
}

