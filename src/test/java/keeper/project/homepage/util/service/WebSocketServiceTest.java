package keeper.project.homepage.util.service;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import keeper.project.homepage.user.dto.election.response.ElectionVoteStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class WebSocketServiceTest {

  @LocalServerPort
  private int port;

  @Autowired
  private WebSocketService webSocketService;

  static final String WEBSOCKET_TOPIC = "/topic/votes/result";

  static BlockingQueue<ElectionVoteStatus> blockingQueue;
  WebSocketStompClient stompClient;

  @BeforeEach
  public void setup() {
    blockingQueue = new LinkedBlockingDeque<>();
    stompClient = new WebSocketStompClient(new SockJsClient(
        List.of(new WebSocketTransport(new StandardWebSocketClient()))));
  }

  @Test
  @DisplayName("메시지 수신")
  public void receiveMessage() throws Exception {
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());

    StompSession session = stompClient
        .connect("ws://localhost:" + port + "/v1/websocket", new StompSessionHandlerAdapter() {
        })
        .get(2, SECONDS);

    session.subscribe(WEBSOCKET_TOPIC, new DefaultStompFrameHandler());

    ElectionVoteStatus status = ElectionVoteStatus.createStatus(10, 5, true);

    webSocketService.sendVoteStatusMessage(WEBSOCKET_TOPIC, status);
    session.send(WEBSOCKET_TOPIC, status);

    ElectionVoteStatus result = blockingQueue.poll(2, SECONDS);

    assertThat(status.getTotal()).isEqualTo(result.getTotal());
    assertThat(status.getVoted()).isEqualTo(result.getVoted());
    assertThat(status.getRate()).isEqualTo(result.getRate());
    assertThat(status.getIsOpen()).isEqualTo(result.getIsOpen());
  }

  static class DefaultStompFrameHandler implements StompFrameHandler {

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
      return ElectionVoteStatus.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
      blockingQueue.offer((ElectionVoteStatus) o);
    }
  }
}
