package keeper.project.homepage.user.service.point;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import keeper.project.homepage.util.service.auth.AuthService;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.point.PointLogEntity;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.point.PointLogRepository;
import keeper.project.homepage.user.dto.point.request.PointGiftLogRequestDto;
import keeper.project.homepage.user.dto.point.request.PointLogRequestDto;
import keeper.project.homepage.user.dto.point.response.PointGiftLogResponseDto;
import keeper.project.homepage.user.dto.point.response.PointLogResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PointLogServiceTest {

  @Mock
  private PointLogRepository pointLogRepository;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private AuthService authService;

  @InjectMocks
  private PointLogService pointLogService;

  private LocalDateTime localDateTime = LocalDateTime.of(2022, 1, 23, 11, 59, 59);

  private MemberEntity memberEntity1 = MemberEntity.builder()
      .loginId("loginId1")
      .emailAddress("emailAddress1")
      .realName("realName1")
      .nickName("nickName1")
      .studentId("123456789")
      .point(1000)
      .level(0)
      .merit(0)
      .demerit(0)
      .build();
  private MemberEntity memberEntity2 = MemberEntity.builder()
      .loginId("loginId2")
      .emailAddress("emailAddress2")
      .realName("realName2")
      .nickName("nickName2")
      .studentId("234567890")
      .point(1000)
      .level(0)
      .merit(0)
      .demerit(0)
      .build();
  private PointLogEntity pointLogEntity1 = PointLogEntity.builder()
      .member(memberEntity1)
      .time(LocalDateTime.now())
      .point(10)
      .detail("복권 게임 사용")
      .isSpent(0)
      .build();
  private PointLogEntity pointLogEntity2 = PointLogEntity.builder()
      .member(memberEntity1)
      .time(localDateTime)
      .point(10)
      .detail("포인트 선물")
      .presentedMember(memberEntity2)
      .isSpent(1)
      .build();
  private PointLogEntity pointLogEntity3 = PointLogEntity.builder()
      .member(memberEntity2)
      .time(localDateTime)
      .point(10)
      .detail("포인트 선물")
      .presentedMember(memberEntity1)
      .isSpent(0)
      .build();

  @Test
  @DisplayName("포인트 사용")
  public void createPointUseRequest() {
    // given
    PointLogRequestDto pointLogRequestDto = new PointLogRequestDto(LocalDateTime.now(), 10,
        "복권 게임 사용");

    // when
    when(memberRepository.save(any(MemberEntity.class))).thenReturn(memberEntity1);
    when(pointLogRepository.save(any(PointLogEntity.class))).thenReturn(pointLogEntity1);
    PointLogResponseDto pointLogResponseDto = pointLogService.createPointUseLog(memberEntity1,
        pointLogRequestDto);

    // then
    assertThat(pointLogResponseDto.getMemberId()).isEqualTo(memberEntity1.getId());
    assertThat(pointLogResponseDto.getFinalPoint()).isEqualTo(990);
  }

  @Test
  @DisplayName("포인트 적립")
  public void createPointSaveRequest() {
    // given
    PointLogRequestDto pointLogRequestDto = new PointLogRequestDto(LocalDateTime.now(), 10,
        "복권 게임 적립");

    // when
    when(memberRepository.save(any(MemberEntity.class))).thenReturn(memberEntity1);
    when(pointLogRepository.save(any(PointLogEntity.class))).thenReturn(pointLogEntity1);
    PointLogResponseDto pointLogResponseDto = pointLogService.createPointSaveLog(memberEntity1,
        pointLogRequestDto);

    // then
    assertThat(pointLogResponseDto.getMemberId()).isEqualTo(memberEntity1.getId());
    assertThat(pointLogResponseDto.getFinalPoint()).isEqualTo(1010);
  }

  @Test
  @DisplayName("포인트 선물")
  public void presentPoint() {
    // given
    PointGiftLogRequestDto pointGiftLogRequestDto = new PointGiftLogRequestDto(localDateTime,
        10, "포인트 선물", memberEntity2.getId());

    // when
    when(memberRepository.save(memberEntity1)).thenReturn(memberEntity1);
    when(memberRepository.save(memberEntity2)).thenReturn(memberEntity2);
    when(memberRepository.findById(memberEntity2.getId())).thenReturn(Optional.of(memberEntity2));
    when(pointLogRepository.save(any(PointLogEntity.class))).thenReturn(pointLogEntity2);
    when(authService.getMemberEntityWithJWT()).thenReturn(memberEntity1);
    PointGiftLogResponseDto pointGiftLogResponseDto = pointLogService.presentingPoint(
        pointGiftLogRequestDto);

    // then
    assertThat(pointGiftLogResponseDto.getMemberId()).isEqualTo(memberEntity1.getId());
    assertThat(pointGiftLogResponseDto.getPresentedMemberId()).isEqualTo(
        memberEntity2.getId());
    assertThat(pointGiftLogResponseDto.getPrePointMember()).isEqualTo(1000);
    assertThat(pointGiftLogResponseDto.getFinalPointMember()).isEqualTo(990);
    assertThat(pointGiftLogResponseDto.getPrePointPresented()).isEqualTo(1000);
    assertThat(pointGiftLogResponseDto.getFinalPointPresented()).isEqualTo(1010);
  }

}
