package keeper.project.homepage.controller.member;

import static keeper.project.homepage.service.sign.SignUpService.HALF_GENERATION_MONTH;
import static keeper.project.homepage.service.sign.SignUpService.KEEPER_FOUNDING_YEAR;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.exception.ExceptionAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class MemberControllerTestSetup extends ApiControllerTestSetUp {

  @Autowired
  public ExceptionAdvice exceptionAdvice;

  public ResponseFieldsSnippet generateMemberCommonResponseField(
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(Arrays.asList(
        fieldWithPath("success").description(success),
        fieldWithPath("code").description(code),
        fieldWithPath("msg").description(msg),
        fieldWithPath("data.id").description("아이디"),
        fieldWithPath("data.emailAddress").description("이메일 주소"),
        fieldWithPath("data.nickName").description("닉네임"),
        fieldWithPath("data.birthday").description("생일").type(Date.class).optional(),
        fieldWithPath("data.registerDate").description("가입 날짜"),
        fieldWithPath("data.point").description("포인트 점수"),
        fieldWithPath("data.level").description("레벨"),
        fieldWithPath("data.merit").description("상점"),
        fieldWithPath("data.demerit").description("벌점"),
        fieldWithPath("data.generation").description("기수 (7월 이후는 N.5기)"),
        fieldWithPath("data.thumbnailId").description("회원의 썸네일 이미지 아이디"),
        fieldWithPath("data.rank").description("회원 등급: null, 우수회원, 일반회원"),
        fieldWithPath("data.type").description("회원 상태: null, 비회원, 정회원, 휴면회원, 졸업회원, 탈퇴"),
        fieldWithPath("data.jobs").description(
            "동아리 직책: null, ROLE_회장, ROLE_부회장, ROLE_대외부장, ROLE_학술부장, ROLE_전산관리자, ROLE_서기, ROLE_총무, ROLE_사서"))
    );
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return responseFields(commonFields);
  }

  public ResponseFieldsSnippet generateMemberListCommonResponseField(
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(Arrays.asList(
        fieldWithPath("success").description(success),
        fieldWithPath("code").description(code),
        fieldWithPath("msg").description(msg),
        fieldWithPath("list[].id").description("아이디"),
        fieldWithPath("list[].emailAddress").description("이메일 주소"),
        fieldWithPath("list[].nickName").description("닉네임"),
        fieldWithPath("list[].birthday").description("생일").type(Date.class).optional(),
        fieldWithPath("list[].registerDate").description("가입 날짜"),
        fieldWithPath("list[].point").description("포인트 점수"),
        fieldWithPath("list[].level").description("레벨"),
        fieldWithPath("list[].thumbnailId").description("회원의 썸네일 이미지 아이디"),
        fieldWithPath("list[].merit").description("상점"),
        fieldWithPath("list[].demerit").description("벌점"),
        fieldWithPath("list[].generation").description("기수 (7월 이후는 N.5기)"),
        fieldWithPath("list[].rank").description("회원 등급: null, 우수회원, 일반회원"),
        fieldWithPath("list[].type").description("회원 상태: null, 비회원, 정회원, 휴면회원, 졸업회원, 탈퇴"),
        fieldWithPath("list[].jobs").description(
            "동아리 직책: null, ROLE_회장, ROLE_부회장, ROLE_대외부장, ROLE_학술부장, ROLE_전산관리자, ROLE_서기, ROLE_총무, ROLE_사서"))
    );
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return responseFields(commonFields);
  }

  public ResponseFieldsSnippet generateCommonResponseField(String success, String code,
      String msg) {
    return responseFields(fieldWithPath("success").description(success),
        fieldWithPath("code").description(code),
        fieldWithPath("msg").description(msg));
  }

  public ResponseFieldsSnippet generatePostingListResponseField(String success, String code,
      String msg, FieldDescriptor... addDescriptors) {
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(Arrays.asList(
        fieldWithPath("success").description(success),
        fieldWithPath("code").description(code),
        fieldWithPath("msg").description(msg),
//        fieldWithPath("list[].id").description("게시물 ID"),
        fieldWithPath("list[].title").description("게시물 제목"),
        fieldWithPath("list[].content").description("게시물 내용"),
//        fieldWithPath("list[].writer").description("작성자  (비밀 게시글일 경우 익명)"),
        fieldWithPath("list[].visitCount").description("조회 수"),
        fieldWithPath("list[].likeCount").description("좋아요 수"),
        fieldWithPath("list[].dislikeCount").description("싫어요 수"),
        fieldWithPath("list[].commentCount").description("댓글 수"),
        fieldWithPath("list[].registerTime").description("작성 시간"),
        fieldWithPath("list[].updateTime").description("수정 시간"),
        fieldWithPath("list[].ipAddress").description("IP 주소"),
        fieldWithPath("list[].allowComment").description("댓글 허용?"),
        fieldWithPath("list[].isNotice").description("공지글?"),
        fieldWithPath("list[].isSecret").description("비밀글?"),
        fieldWithPath("list[].isTemp").description("임시저장?"),
        fieldWithPath("list[].password").description("비밀번호").optional(),
        fieldWithPath("list[].memberId").description("작성자 아이디"),
        fieldWithPath("list[].categoryId").description("카테고리 아이디"),
        fieldWithPath("list[].thumbnailId").description("게시글 썸네일 아이디")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return responseFields(commonFields);
  }

  public Float getMemberGeneration() {
    LocalDate date = LocalDate.now();
    Float generation = (float) (date.getYear() - KEEPER_FOUNDING_YEAR);
    if (date.getMonthValue() >= HALF_GENERATION_MONTH) {
      generation += 0.5F;
    }
    return generation;
  }
}
