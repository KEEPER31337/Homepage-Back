package keeper.project.homepage.attendance.service;


import static keeper.project.homepage.ApiControllerTestHelper.MemberJobName.회원;
import static keeper.project.homepage.ApiControllerTestHelper.MemberTypeName.정회원;
import static keeper.project.homepage.member.service.MemberUtilService.VIRTUAL_MEMBER_ID;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import keeper.project.homepage.about.dto.AttendanceDto;
import keeper.project.homepage.attendance.entity.AttendanceEntity;
import keeper.project.homepage.attendance.repository.AttendanceRepository;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import keeper.project.homepage.member.repository.MemberJobRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.member.repository.MemberTypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class AttendanceServiceTest {

  @Autowired
  private MemberJobRepository memberJobRepository;
  @Autowired
  private MemberTypeRepository memberTypeRepository;
  @Autowired
  private AttendanceRepository attendanceRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private AttendanceService attendanceService;

  @Test
  @Rollback(value = false)
  public void 출석_랭킹_동시성문제_테스트() throws InterruptedException {
    // given
    int numberOfThreads = 100;
    int memberCountPerThread = 1;
    ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
    CountDownLatch latch = new CountDownLatch(numberOfThreads);
    // when
    for (int i = 0; i < numberOfThreads; i++) {
      service.execute(() -> {
        for (int j = 0; j < memberCountPerThread; j++) {
          MemberEntity newMember = generateMember();
          setAuthentication(newMember);
          attendanceService.saveAttendance(AttendanceDto.builder()
              .ipAddress("127.0.0.1")
              .greetings("출석 테스트입니다.")
              .build(), newMember);
        }
        latch.countDown();
      });
    }
    latch.await();
    long distinctRankCount = attendanceRepository.findAll()
        .stream()
        .map(AttendanceEntity::getRank)
        .distinct()
        .count();

    // then
    attendanceRepository.deleteAll();
    memberRepository.deleteAllByIdIsNot(VIRTUAL_MEMBER_ID);
    assertThat(distinctRankCount).isEqualTo(numberOfThreads * memberCountPerThread);
  }

  MemberEntity generateMember() {
    String uniqueInfo = UUID.randomUUID().toString();
    MemberJobEntity memberJob = memberJobRepository.findByName(회원.getJobName()).get();
    MemberTypeEntity memberType = memberTypeRepository.findByName(정회원.getTypeName()).get();
    MemberEntity newMember = memberRepository.save(
        MemberEntity.builder()
            .loginId("loginId" + uniqueInfo)
            .emailAddress(uniqueInfo + "@keeper.co.kr")
            .password("keeper1234")
            .studentId("1234" + uniqueInfo)
            .nickName("nick" + uniqueInfo)
            .realName("real" + uniqueInfo)
            .generation(8F)
            .memberType(memberType)
            .build());
    newMember.addMemberJob(memberJob);
    return newMember;
  }

  private void setAuthentication(MemberEntity reqMember) {
    SecurityContext context = SecurityContextHolder.getContext();
    context.setAuthentication(
        new UsernamePasswordAuthenticationToken(reqMember.getId(), reqMember.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_회원"))));
  }
}
