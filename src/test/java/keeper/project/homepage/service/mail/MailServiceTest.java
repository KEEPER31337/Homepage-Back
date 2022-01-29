package keeper.project.homepage.service.mail;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailServiceTest {

  final private List<String> toUserList = new ArrayList<>(List.of("test@k33p3r.com"));

  @Autowired
  MailService mailService;

  /*
   *  // 해당 테스트는 메일을 전송하는 테스트임으로 되돌리기가 불가능합니다.
   *  // 따라서 불필요한 메일전송을 막고자 주석처리 해 두었습니다.
   *  @Test
   *  @DisplayName("메일 전송 테스트")
   *  public void passwordMatchWithSHA256() throws Exception {
   *    mailService.sendMail(toUserList, "keeper Test Subject", "keeper Test Text");
   *  }
   */

}
