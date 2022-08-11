package keeper.project.homepage.util.service;

import keeper.project.homepage.user.dto.election.response.ElectionVoteStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

  private final SimpMessagingTemplate webSocket;

  public void sendVoteStatusMessage(String payload, ElectionVoteStatus status) {
    webSocket.convertAndSend(payload, status);
  }

}
