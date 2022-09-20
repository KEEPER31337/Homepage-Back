package keeper.project.homepage.user.repostiory.point;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.point.entity.PointLogEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.point.PointLogRepository;
import keeper.project.homepage.user.dto.point.request.PointLogRequestDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Log4j2
public class PointLogRepositoryTest {

  private final PointLogRepository pointLogRepository;
  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private MemberEntity memberEntity1, memberEntity2;

  @Autowired
  public PointLogRepositoryTest(PointLogRepository pointLogRepository,
      MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
    this.pointLogRepository = pointLogRepository;
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @BeforeEach
  public void setUp() throws Exception {
    memberEntity1 = MemberEntity.builder()
        .loginId("loginId1")
        .emailAddress("emailAddress1")
        .password(passwordEncoder.encode("password1"))
        .realName("realName1")
        .nickName("nickName1")
        .studentId("123456789")
        .point(0)
        .level(0)
        .merit(0)
        .demerit(0)
        .build();
    memberEntity2 = MemberEntity.builder()
        .loginId("loginId2")
        .emailAddress("emailAddress2")
        .password(passwordEncoder.encode("password2"))
        .realName("realName2")
        .nickName("nickName2")
        .studentId("234567890")
        .point(0)
        .level(0)
        .merit(0)
        .demerit(0)
        .build();
    memberRepository.save(memberEntity1);
    memberRepository.save(memberEntity2);
  }

  @Test
  @DisplayName("포인트 로그 생성 후 저장")
  public void savePointLog() {
    // given
    PointLogRequestDto pointLogRequestDto = new PointLogRequestDto(LocalDateTime.now(), 100,
        "로또 게임 결과");
    PointLogEntity pointLogEntity = PointLogEntity.builder()
        .member(memberEntity1)
        .time(LocalDateTime.now())
        .point(100)
        .detail("로또 게임 결과")
        .isSpent(0)
        .build();

    // when
    pointLogRepository.save(pointLogEntity);

    // then
    assertThat(pointLogRepository.getById(pointLogEntity.getId()))
        .isEqualTo(pointLogEntity);
  }

  @Test
  @DisplayName("포인트 선물 로그 생성 후 저장")
  public void savePointGiftLog() {
    // given
    PointLogEntity pointLogEntity1 = PointLogEntity.builder()
        .member(memberEntity1)
        .time(LocalDateTime.now())
        .point(100)
        .detail("포인트 선물")
        .presentedMember(memberEntity2)
        .isSpent(1)
        .build();

    PointLogEntity pointLogEntity2 = PointLogEntity.builder()
        .member(memberEntity2)
        .time(LocalDateTime.now())
        .point(100)
        .detail("포인트 선물")
        .presentedMember(memberEntity1)
        .isSpent(0)
        .build();

    // when
    pointLogRepository.save(pointLogEntity1);
    pointLogRepository.save(pointLogEntity2);

    // then
    assertThat(pointLogRepository.getById(pointLogEntity1.getId()))
        .isEqualTo(pointLogEntity1);
    assertThat(pointLogRepository.getById(pointLogEntity2.getId()))
        .isEqualTo(pointLogEntity2);
  }

  @Test
  @DisplayName("선물한 사람 포인트 로그 목록 불러오기")
  public void getGiftPersonPointLogs() {
    // given
    PointLogEntity pointLogEntity1 = PointLogEntity.builder()
        .member(memberEntity1)
        .time(LocalDateTime.now())
        .point(100)
        .detail("로또 게임 결과")
        .isSpent(0)
        .build();
    PointLogEntity pointLogEntity2 = PointLogEntity.builder()
        .member(memberEntity1)
        .time(LocalDateTime.now())
        .point(100)
        .detail("포인트 선물")
        .presentedMember(memberEntity2)
        .isSpent(1)
        .build();

    PointLogEntity pointLogEntity3 = PointLogEntity.builder()
        .member(memberEntity2)
        .time(LocalDateTime.now())
        .point(100)
        .detail("포인트 선물")
        .presentedMember(memberEntity1)
        .isSpent(0)
        .build();
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.DESC, "id"));

    // when
    pointLogRepository.save(pointLogEntity1);
    pointLogRepository.save(pointLogEntity2);
    pointLogRepository.save(pointLogEntity3);
    Page<PointLogEntity> pointLogEntityPage = pointLogRepository.findAllByMember(
        memberEntity1, pageable);

    // then
    assertThat(pointLogEntityPage.getContent()).containsExactly(pointLogEntity2, pointLogEntity1);
  }

  @Test
  @DisplayName("선물받은 사람 포인트 로그 목록 불러오기")
  public void getGiftRecipientPointLogs() {
    // given
    PointLogEntity pointLogEntity1 = PointLogEntity.builder()
        .member(memberEntity2)
        .time(LocalDateTime.now())
        .point(100)
        .detail("로또 게임 결과")
        .isSpent(0)
        .build();
    PointLogEntity pointLogEntity2 = PointLogEntity.builder()
        .member(memberEntity1)
        .time(LocalDateTime.now())
        .point(100)
        .detail("포인트 선물")
        .presentedMember(memberEntity2)
        .isSpent(1)
        .build();

    PointLogEntity pointLogEntity3 = PointLogEntity.builder()
        .member(memberEntity2)
        .time(LocalDateTime.now())
        .point(100)
        .detail("포인트 선물")
        .presentedMember(memberEntity1)
        .isSpent(0)
        .build();
    Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.DESC, "id"));

    // when
    pointLogRepository.save(pointLogEntity1);
    pointLogRepository.save(pointLogEntity2);
    pointLogRepository.save(pointLogEntity3);
    Page<PointLogEntity> pointLogEntityPage = pointLogRepository.findAllByMember(
        memberEntity2, pageable);

    // then
    assertThat(pointLogEntityPage.getContent()).containsExactly(pointLogEntity3, pointLogEntity1);
  }

}
