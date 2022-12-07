package keeper.project.homepage.member.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class AdminMemberServiceTest {

  @Autowired
  ScheduledTaskHolder scheduledTaskHolder;

  @Autowired
  EntityManager em;

  @Autowired
  AdminMemberService adminMemberService;

  @Autowired
  MemberRepository memberRepository;

  @Test
  @DisplayName("회원 상벌점 초기화 테스트")
  void initMembersMerit() {
    // given
    MemberEntity virtualMember = memberRepository.getById(1L);
    virtualMember.changeMerit(3);
    virtualMember.changeDemerit(2);

    // when
    adminMemberService.initMembersMerit();

    // then
    Assertions.assertThat(virtualMember.getMerit()).isZero();
    Assertions.assertThat(virtualMember.getMerit()).isZero();
  }

  @Test
  @DisplayName("스케줄이 예약되어 있는지 확인")
  void checkScheduleReserved() {
    // given
    Set<ScheduledTask> scheduledTasks = scheduledTaskHolder.getScheduledTasks();

    // when
    List<String> scheduledTaskPaths = scheduledTasks.stream()
        .filter(scheduledTask -> scheduledTask.getTask() instanceof CronTask)
        .map(scheduledTask -> (scheduledTask.getTask()).toString()).toList();

    // then
    Assertions.assertThat(scheduledTaskPaths)
        .contains("keeper.project.homepage.member.service.AdminMemberService.initMembersMerit");
  }

}