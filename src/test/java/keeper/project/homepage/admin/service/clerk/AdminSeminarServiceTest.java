package keeper.project.homepage.admin.service.clerk;

import static keeper.project.homepage.admin.service.clerk.AdminSeminarService.ABSENCE_DEMERIT;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ABSENCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.ATTENDANCE;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.LATENESS;
import static keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity.seminarAttendanceStatus.PERSONAL;
import static keeper.project.homepage.entity.member.MemberTypeEntity.memberType.DORMANT_MEMBER;
import static keeper.project.homepage.entity.member.MemberTypeEntity.memberType.REGULAR_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import keeper.project.homepage.admin.dto.clerk.request.SeminarAttendanceUpdateRequestDto;
import keeper.project.homepage.admin.dto.clerk.request.SeminarCreateRequestDto;
import keeper.project.homepage.admin.dto.clerk.response.SeminarCreateResponseDto;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberTypeEntity;
import keeper.project.homepage.exception.clerk.CustomSeminarAttendanceNotFoundException;
import keeper.project.homepage.repository.clerk.SeminarAttendanceRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceStatusRepository;
import keeper.project.homepage.repository.clerk.SeminarRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.repository.member.MemberTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
public class AdminSeminarServiceTest {

  @Autowired
  EntityManager em;
  @Autowired
  AdminSeminarService adminSeminarService;
  @Autowired
  SeminarAttendanceRepository seminarAttendanceRepository;
  @Autowired
  SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  SeminarRepository seminarRepository;
  @Autowired
  MemberTypeRepository memberTypeRepository;

  @Test
  @DisplayName("[SUCCESS] 개인사유 출석 수정")
  void updateSeminarAttendanceTest() {
    // given
    SeminarEntity seminar = seminarRepository.getById(1L);
    MemberEntity member = memberRepository.getById(1L);
    SeminarAttendanceStatusEntity absence = seminarAttendanceStatusRepository.getById(
        ABSENCE.getId());
    adminSeminarService.generateSeminarAttendance(member, seminar, absence);
    em.flush();
    em.clear();
    SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
        .seminarAttendanceStatusId(4L)
        .absenceExcuse("예비군 훈련")
        .build();

    // when
    adminSeminarService.updateSeminarAttendanceStatus(seminar.getId(), member.getId(), request);
    SeminarAttendanceEntity find = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, member).orElseThrow(CustomSeminarAttendanceNotFoundException::new);

    //then
    assertThat(find.getSeminarAttendanceStatusEntity().getType()).isEqualTo(PERSONAL.getType());
    assertThat(find.getSeminarAttendanceExcuseEntity().getAbsenceExcuse())
        .isEqualTo("예비군 훈련");
  }

  @Test
  @DisplayName("[SUCCESS] 결석으로 수정시 벌점 3점 증가")
  void updateAbsenceTest() {
    // given
    SeminarEntity seminar = seminarRepository.getById(1L);
    MemberEntity member = memberRepository.getById(1L);
    SeminarAttendanceStatusEntity attendance = seminarAttendanceStatusRepository.getById(
        ATTENDANCE.getId());

    adminSeminarService.generateSeminarAttendance(member, seminar, attendance);
    Integer beforeDemerit = member.getDemerit();
    em.flush();
    em.clear();
    SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
        .seminarAttendanceStatusId(3L)
        .build();

    // when
    adminSeminarService.updateSeminarAttendanceStatus(seminar.getId(), member.getId(), request);
    SeminarAttendanceEntity find = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, member).orElseThrow(CustomSeminarAttendanceNotFoundException::new);
    Integer afterDemerit = find.getMemberEntity().getDemerit();

    //then
    assertThat(find.getSeminarAttendanceStatusEntity().getType()).isEqualTo(ABSENCE.getType());
    assertThat(beforeDemerit + ABSENCE_DEMERIT).isEqualTo(afterDemerit);
  }

  @Test
  @DisplayName("[SUCCESS] 이전 상태가 결석인 경우 상태 변경시 벌점 3점 감소")
  void updateStatusWhenBeforeStateIsAbsence() {
    // given
    SeminarEntity seminar = seminarRepository.getById(1L);
    MemberEntity member = memberRepository.getById(1L);
    SeminarAttendanceStatusEntity absence = seminarAttendanceStatusRepository.getById(
        ABSENCE.getId());
    adminSeminarService.generateSeminarAttendance(member, seminar, absence);
    em.flush();
    em.clear();
    Integer beforeDemerit = memberRepository.getById(1L).getDemerit();
    SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
        .seminarAttendanceStatusId(1L)
        .build();
    // when
    adminSeminarService.updateSeminarAttendanceStatus(seminar.getId(), member.getId(), request);
    SeminarAttendanceEntity find = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar, member).orElseThrow(CustomSeminarAttendanceNotFoundException::new);
    Integer afterDemerit = find.getMemberEntity().getDemerit();

    //then
    assertThat(find.getSeminarAttendanceStatusEntity().getType()).isEqualTo(ATTENDANCE.getType());
    assertThat(beforeDemerit - ABSENCE_DEMERIT).isEqualTo(afterDemerit);
  }

  @Test
  @DisplayName("[SUCCESS] 지각으로 변경 시 세미나에서 지각을 홀수번 한 적이 있으면 결석으로 간주")
  void updateStatusToLatenessIfEverBeenLate() {
    // given
    SeminarEntity seminar1 = seminarRepository.getById(1L);
    SeminarEntity seminar2 = adminSeminarService.generateSeminar(LocalDateTime.now());
    MemberEntity member = memberRepository.getById(1L);
    SeminarAttendanceStatusEntity attendance = seminarAttendanceStatusRepository.getById(
        ATTENDANCE.getId());
    SeminarAttendanceStatusEntity lateness = seminarAttendanceStatusRepository.getById(
        LATENESS.getId());

    adminSeminarService.generateSeminarAttendance(member, seminar1, lateness);
    adminSeminarService.generateSeminarAttendance(member, seminar2, attendance);
    em.flush();
    em.clear();
    Integer beforeDemerit = memberRepository.getById(1L).getDemerit();
    SeminarAttendanceUpdateRequestDto request = SeminarAttendanceUpdateRequestDto.builder()
        .seminarAttendanceStatusId(2L)
        .build();

    // when
    adminSeminarService.updateSeminarAttendanceStatus(seminar2.getId(), member.getId(), request);
    SeminarAttendanceEntity find = seminarAttendanceRepository.findBySeminarEntityAndMemberEntity(
        seminar2, member).orElseThrow(CustomSeminarAttendanceNotFoundException::new);
    Integer afterDemerit = find.getMemberEntity().getDemerit();

    //then
    assertThat(find.getSeminarAttendanceStatusEntity().getType()).isEqualTo(LATENESS.getType());
    assertThat(beforeDemerit + ABSENCE_DEMERIT).isEqualTo(afterDemerit);
  }

  @Test
  @DisplayName("[SUCCESS] 세미나 생성 테스트")
  void seminarCreateTest() {
    // given
    MemberTypeEntity regularMember = memberTypeRepository.getById(REGULAR_MEMBER.getId());
    MemberTypeEntity dormantMember = memberTypeRepository.getById(DORMANT_MEMBER.getId());

    SeminarCreateRequestDto request = SeminarCreateRequestDto.builder()
        .openTime(LocalDateTime.now().plusWeeks(1L).withNano(0)).build();
    MemberEntity member1 = generateMember("이정학", 12.5F, dormantMember);
    MemberEntity member2 = generateMember("최우창", 12.5F, regularMember);
    MemberEntity member3 = generateMember("정현모", 8F, regularMember);
    MemberEntity member4 = generateMember("손현경", 13F, regularMember);

    // when
    SeminarCreateResponseDto responseDto = adminSeminarService.createSeminar(request);
    em.flush();
    em.clear();

    // then
    SeminarEntity seminar = seminarRepository.getById(responseDto.getId());
    List<SeminarAttendanceEntity> seminarAttendances = seminar.getSeminarAttendanceEntity();
    List<Long> memberIds = seminarAttendances.stream()
        .map(SeminarAttendanceEntity::getMemberEntity)
        .map(MemberEntity::getId)
        .toList();

    assertThat(responseDto.getId()).isEqualTo(seminar.getId());
    for (SeminarAttendanceEntity attendance : seminarAttendances) {
      assertThat(attendance.getSeminarAttendanceStatusEntity().getType()).isEqualTo(
          ATTENDANCE.getType());
    }
    assertThat(memberIds.size()).isEqualTo(3);
    assertThat(memberIds).contains(member2.getId(), member3.getId(), member4.getId());
  }

  MemberEntity generateMember(String name, Float generation, MemberTypeEntity type) {
    final long epochTime = System.nanoTime();
    return memberRepository.save(
        MemberEntity.builder()
            .loginId("abcd1234" + epochTime)
            .emailAddress("test1234@keeper.co.kr" + epochTime)
            .password("1234")
            .studentId("1234" + epochTime)
            .nickName("nick" + epochTime)
            .realName(name)
            .generation(generation)
            .memberType(type)
            .build());
  }
}
