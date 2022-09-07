package keeper.project.homepage.admin.controller.clerk;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.clerk.SeminarAttendanceEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceExcuseEntity;
import keeper.project.homepage.entity.clerk.SeminarAttendanceStatusEntity;
import keeper.project.homepage.entity.clerk.SeminarEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.repository.clerk.MeritLogRepository;
import keeper.project.homepage.repository.clerk.MeritTypeRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceExcuseRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceRepository;
import keeper.project.homepage.repository.clerk.SeminarAttendanceStatusRepository;
import keeper.project.homepage.repository.clerk.SeminarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.FieldDescriptor;

public class ClerkControllerTestHelper extends ApiControllerTestHelper {

  @Autowired
  protected SeminarRepository seminarRepository;

  @Autowired
  protected SeminarAttendanceRepository seminarAttendanceRepository;

  @Autowired
  protected SeminarAttendanceExcuseRepository seminarAttendanceExcuseRepository;

  @Autowired
  protected SeminarAttendanceStatusRepository seminarAttendanceStatusRepository;

  @Autowired
  protected MeritTypeRepository meritTypeRepository;

  @Autowired
  MeritLogRepository meritLogRepository;

  protected String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected String asJsonDateString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.registerModule(new JavaTimeModule()).writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  protected List<FieldDescriptor> generateJobDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".id").description("해당 ROLE의 Id"),
        fieldWithPath(prefix + ".name").description("해당 ROLE의 이름"),
        fieldWithPath(prefix + ".badgePath").description("해당 ROLE의 badge 경로")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateTypeDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".id").description("해당 TYPE의 Id"),
        fieldWithPath(prefix + ".name").description("해당 TYPE의 이름"),
        fieldWithPath(prefix + ".badgePath").description("해당 TYPE의 badge 경로")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateClerkMemberJobTypeResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".memberId").description("member의 Id"),
        fieldWithPath(prefix + ".nickName").description("member의 닉네임"),
        fieldWithPath(prefix + ".realName").description("member의 실명"),
        fieldWithPath(prefix + ".generation").description("member의 기수"),
        fieldWithPath(prefix + ".profileImagePath").description(
            "프로필 이미지 경로 (없으면 default 아미지 경로를 가르킴)"),
        subsectionWithPath(prefix + ".hasJobs[]").description("member의 현재 직책"),
        fieldWithPath(prefix + ".hasJobs[].id").description("member의 현재 직책의 id"),
        fieldWithPath(prefix + ".hasJobs[].name").description("member의 현재 직책의 이름"),
        subsectionWithPath(prefix + ".type").description("member의 활동 상태"),
        fieldWithPath(prefix + ".type.id").description("member의 활동 상태의 id"),
        fieldWithPath(prefix + ".type.name").description("member의 활동 상태의 이름")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateSeminarDtoResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".id").description("세미나 id"),
        fieldWithPath(prefix + ".name").description("세미나 이름"),
        fieldWithPath(prefix + ".openTime").description("세미나 open 시간")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateSeminarAttendanceStatusResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".id").description("세미나 출석 상태 id"),
        fieldWithPath(prefix + ".seminarAttendanceStatusType").description("세미나 출석 상태 타입")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  protected List<FieldDescriptor> generateSeminarAttendanceResponseFields(
      ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".seminarId").description("세미나 id"),
        fieldWithPath(prefix + ".seminarName").description("세미나 이름"),
        fieldWithPath(prefix + ".sortedSeminarAttendances").description("세미나 출석 정보"),
        fieldWithPath(prefix + ".sortedSeminarAttendances.[].attendanceId").description(
            "세미나 출석 id"),
        fieldWithPath(prefix + ".sortedSeminarAttendances.[].generation").description("회원 기수"),
        fieldWithPath(prefix + ".sortedSeminarAttendances.[].memberName").description("회원 이름"),
        fieldWithPath(
            prefix + ".sortedSeminarAttendances.[].attendanceStatusType").description(
            "출석 상태"),
        fieldWithPath(prefix + ".sortedSeminarAttendances.[].absenceExcuse").optional().description(
            "결석사유")
    ));
    if (type.equals(ResponseType.PAGE)) {
      commonFields.addAll(Arrays.asList(
          fieldWithPath("page.empty").description("페이지가 비었는 지 여부"),
          fieldWithPath("page.first").description("첫 페이지 인지"),
          fieldWithPath("page.last").description("마지막 페이지 인지"),
          fieldWithPath("page.number").description("요소를 가져 온 페이지 번호 (0부터 시작)"),
          fieldWithPath("page.numberOfElements").description("요소 개수"),
          subsectionWithPath("page.pageable").description("해당 페이지에 대한 DB 정보"),
          fieldWithPath("page.size").description("요청한 페이지 크기"),
          subsectionWithPath("page.sort").description("정렬에 대한 정보"),
          fieldWithPath("page.totalElements").description("총 요소 개수"),
          fieldWithPath("page.totalPages").description("총 페이지")));
    }
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  SeminarEntity generateSeminar(LocalDateTime openTime) {
    return seminarRepository.save(SeminarEntity.builder()
        .name(openTime.toLocalDate()
            .toString()
            .replaceAll("-", ""))
        .openTime(openTime)
        .build()
    );
  }

  SeminarAttendanceEntity generateSeminarAttendance(MemberEntity member,
      SeminarEntity seminar, SeminarAttendanceStatusEntity seminarAttendanceStatus) {
    return seminarAttendanceRepository.save(
        SeminarAttendanceEntity.builder()
            .memberEntity(member)
            .seminarEntity(seminar)
            .seminarAttendanceStatusEntity(seminarAttendanceStatus)
            .seminarAttendTime(LocalDateTime.now().withNano(0))
            .build()
    );
  }

  SeminarAttendanceExcuseEntity generateSeminarAttendanceExcuse(
      SeminarAttendanceEntity seminarAttendanceEntity) {
    return seminarAttendanceExcuseRepository.save(
        SeminarAttendanceExcuseEntity.builder()
            .seminarAttendanceEntity(seminarAttendanceEntity)
            .absenceExcuse("개인 사정")
            .build());
  }

  MemberEntity generateMember(String name, Float generation) {
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
            .build());
  }
}
