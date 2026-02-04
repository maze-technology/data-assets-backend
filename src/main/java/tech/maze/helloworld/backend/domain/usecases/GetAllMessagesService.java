package tech.maze.helloworld.backend.domain.usecases;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import tech.maze.helloworld.backend.domain.models.Message;
import tech.maze.helloworld.backend.domain.ports.in.GetAllMessagesUseCase;
import tech.maze.helloworld.backend.domain.ports.out.LoadMessagePort;

/**
 * Service implementing the GetAllMessagesUseCase.
 */
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Service
public class GetAllMessagesService implements GetAllMessagesUseCase {
  LoadMessagePort loadMessagePort;

  @Override
  public List<Message> getAllMessages() {
    return loadMessagePort.loadAllMessages();
  }
}

