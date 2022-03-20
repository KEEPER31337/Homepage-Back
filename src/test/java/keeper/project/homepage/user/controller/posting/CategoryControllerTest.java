package keeper.project.homepage.user.controller.posting;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.entity.posting.CategoryEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class CategoryControllerTest extends ApiControllerTestSetUp {

  private CategoryEntity categoryEntity1, categoryEntity2;

  @BeforeEach
  public void setUp() throws Exception {
    categoryEntity1 = CategoryEntity.builder()
        .name("헤드 카테고리")
        .parentId(0L)
        .href("board")
        .build();
    categoryRepository.save(categoryEntity1);

    categoryEntity2 = CategoryEntity.builder()
        .name("자식 카테고리")
        .parentId(categoryEntity1.getId())
        .href("board")
        .build();
    categoryRepository.save(categoryEntity2);


  }

  @Test
  @DisplayName("헤드 카테고리 리스트 불러오기(자식 카테고리 없이 해당하는 데이터만) - 성공")
  public void getAllHeadCategory() throws Exception {
    mockMvc.perform(get("/v1/category/lists/head"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("category-headCategories",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].id").description("해당 카테고리의 ID"),
                fieldWithPath("list[].name").description("해당 카테고리의 이름"),
                fieldWithPath("list[].href").description("해당 카테고리의 참조값").optional()
            )));
  }

  @Test
  @DisplayName("헤드 카테고리 리스트 불러오기(자식 카테고리 존재) - 성공")
  public void getAllHeadCategoryAndChild() throws Exception {
    mockMvc.perform(get("/v1/category/lists/head/all"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("category-headCategories-withChild",
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[]").description("헤드 카테고리 리스트"),
                fieldWithPath("list[].id").description("해당 카테고리의 ID"),
                fieldWithPath("list[].name").description("해당 카테고리의 이름"),
                fieldWithPath("list[].href").description("해당 카테고리의 참조값").optional(),
                subsectionWithPath("list[].children[]").description("해당 카테고리의 하위카테고리")
            )));;
  }

  @Test
  @DisplayName("부모 ID를 통해 카테고리 리스트 불러오기(자식 카테고리 없이 해당하는 데이터만) - 성공")
  public void getAllCategoryByParentId() throws Exception {
    mockMvc.perform(get("/v1/category/lists/{parentId}", categoryEntity1.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("category-lists-ByParentId",
            pathParameters(
                parameterWithName("parentId").description("자식 카테고리 정보를 얻기 위한 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].id").description("해당 카테고리의 ID"),
                fieldWithPath("list[].name").description("해당 카테고리의 이름"),
                fieldWithPath("list[].href").description("해당 카테고리의 참조값")
            )));
  }

  @Test
  @DisplayName("부모 ID를 통해 카테고리 리스트 불러오기(자식 카테고리 존재) - 성공")
  public void getAllCategoryAndChildByParentId() throws Exception {
    mockMvc.perform(get("/v1/category/lists/all/{parentId}", categoryEntity1.getId()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("category-lists-withChild-ByParentId",
            pathParameters(
                parameterWithName("parentId").description("자식 카테고리 정보를 얻기 위한 ID")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].id").description("해당 카테고리의 ID"),
                fieldWithPath("list[].name").description("해당 카테고리의 이름"),
                fieldWithPath("list[].href").description("해당 카테고리의 참조값"),
                subsectionWithPath("list[].children[]").description("해당 카테고리의 하위카테고리")
            )));
  }

}