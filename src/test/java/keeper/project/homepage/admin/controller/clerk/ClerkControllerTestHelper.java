package keeper.project.homepage.admin.controller.clerk;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import keeper.project.homepage.ApiControllerTestHelper;
import org.springframework.restdocs.payload.FieldDescriptor;

public class ClerkControllerTestHelper extends ApiControllerTestHelper {

  protected String asJsonString(final Object obj) {
    try {
      final ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(obj);
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
}
