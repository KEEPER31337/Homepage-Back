package keeper.project.homepage.common.service.mail;

import java.util.ArrayList;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

  private final JavaMailSender javaMailSender;

  public void sendMail(List<String> toUserList, String subject, String text) {

//    ArrayList<String> toUserList = new ArrayList<>();
//
//    toUserList.add("gusah009@gmail.com");
//    toUserList.add("ghimmk@naver.com");

    int toUserSize = toUserList.size();
    SimpleMailMessage simpleMessage = new SimpleMailMessage();
    simpleMessage.setTo((String[]) toUserList.toArray(new String[toUserSize]));
    simpleMessage.setSubject(subject);
    simpleMessage.setText(text);
    javaMailSender.send(simpleMessage);
  }
}