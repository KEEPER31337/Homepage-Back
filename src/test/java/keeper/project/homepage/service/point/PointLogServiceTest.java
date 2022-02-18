package keeper.project.homepage.service.point;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import keeper.project.homepage.dto.point.request.PointLogRequest;
import keeper.project.homepage.dto.point.result.PointLogResult;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import keeper.project.homepage.repository.member.MemberJobRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.user.service.point.PointLogService;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@Transactional
@SpringBootTest
@Log4j2
public class PointLogServiceTest {

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private MemberJobRepository memberJobRepository;

  @Autowired
  private PointLogService pointLogService;

  private MemberEntity memberEntity;

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "gusah@naver.com";
  final private String studentId = "201724579";
  final private Integer point = 100;

  public void createMemberEntity() {
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회원").get();
    MemberHasMemberJobEntity hasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberJobEntity(memberJobEntity)
        .build();
    memberEntity = MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName(nickName)
        .emailAddress(emailAddress)
        .studentId(studentId)
        .point(point)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
    memberRepository.save(memberEntity);
  }

  @Test
  @DisplayName("포인트 사용 요청 생성")
  public void createPointUseRequest() throws Exception {
    PointLogRequest pointLogRequest = new PointLogRequest(LocalDateTime.now(), 10, "포인트복권에서 사용");

    Assertions.assertThat(pointLogRequest.getPoint()).isEqualTo(10);
    Assertions.assertThat(pointLogRequest.getDetail()).isEqualTo("포인트복권에서 사용");
  }

  @Test
  @DisplayName("포인트 적립 요청 생성")
  public void createPointSaveRequest() throws Exception {
    PointLogRequest pointLogRequest = new PointLogRequest(LocalDateTime.now(), 20, "포인트복권에서 적립");

    Assertions.assertThat(pointLogRequest.getPoint()).isEqualTo(20);
    Assertions.assertThat(pointLogRequest.getDetail()).isEqualTo("포인트복권에서 적립");
  }

  @Test
  @DisplayName("포인트 사용 로그 생성 및 사용 확인")
  public void createPointUseLog() throws Exception {
    createMemberEntity();

    PointLogRequest pointLogRequest = new PointLogRequest(LocalDateTime.now(), 10, "포인트복권에서 사용");

    PointLogResult pointLogResult = pointLogService.createPointUseLog(memberEntity,
        pointLogRequest);

    Assertions.assertThat(pointLogResult.getMemberId()).isEqualTo(memberEntity.getId());
    Assertions.assertThat(pointLogResult.getPoint()).isEqualTo(10);
    Assertions.assertThat(pointLogResult.getDetail()).isEqualTo("포인트복권에서 사용");
    Assertions.assertThat(pointLogResult.getPrePoint()).isNotEqualTo(memberEntity.getPoint());
    Assertions.assertThat(pointLogResult.getFinalPoint()).isEqualTo(90);
  }

  @Test
  @DisplayName("포인트 사용 로그 생성 실패 - 포인트 부족")
  public void createPointUseLogFail() {
    createMemberEntity();

    try {
      PointLogRequest pointLogRequest = new PointLogRequest(LocalDateTime.now(), 1000, "포인트복권에서 사용");

      PointLogResult pointLogResult = pointLogService.createPointUseLog(memberEntity,
          pointLogRequest);
    } catch (Exception e) {
      Assertions.assertThat(e.getMessage()).isEqualTo("잔여 포인트가 부족합니다.");
    }
  }

  @Test
  @DisplayName("포인트 적립 로그 생성 및 적립 확인")
  public void createPointSaveLog() throws Exception {
    createMemberEntity();

    PointLogRequest pointLogRequest = new PointLogRequest(LocalDateTime.now(), 20, "포인트복권에서 적립");

    PointLogResult pointLogResult = pointLogService.createPointSaveLog(memberEntity,
        pointLogRequest);

    Assertions.assertThat(pointLogResult.getMemberId()).isEqualTo(memberEntity.getId());
    Assertions.assertThat(pointLogResult.getPoint()).isEqualTo(20);
    Assertions.assertThat(pointLogResult.getDetail()).isEqualTo("포인트복권에서 적립");
    Assertions.assertThat(pointLogResult.getPrePoint()).isNotEqualTo(memberEntity.getPoint());
    Assertions.assertThat(pointLogResult.getFinalPoint()).isEqualTo(120);
  }

}
