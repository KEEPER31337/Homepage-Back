package keeper.project.homepage.controller.posting;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import keeper.project.homepage.ApiControllerTestSetUp;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class CategoryControllerTest extends ApiControllerTestSetUp {

//  @Test
//  @DisplayName("부모 카테고리 ID로 카테고리 불러오기")
//  public void getAllCategoryByParentId() throws Exception {
//    ResultActions result = mockMvc.perform(
//        RestDocumentationRequestBuilders.get("/v1/category/id/{parentId}", 0L));
//
//    result.andExpect(MockMvcResultMatchers.status().isOk())
//        .andDo(print())
//        .andDo(document("categories-getByParentId",
//            pathParameters(
//                parameterWithName("parentId").description("찾고자 하는 카테고리의 이름")
//            ),
//            relaxedResponseFields(
//                fieldWithPath("[].id").description("카테고리 ID"),
//                fieldWithPath("[].name").description("카테고리 이름"),
//                fieldWithPath("[].children[]").description("하위 카테고리 리스트")
//            )
//        ));
//  }
//
//  @Test
//  @DisplayName("카테고리 이름으로 카테고리 불러오기")
//  public void getAllCategoryByName() throws Exception {
//    ResultActions result = mockMvc.perform(
//        RestDocumentationRequestBuilders.get("/v1/category/name/{name}", "one"));
//
//    result.andExpect(MockMvcResultMatchers.status().isOk())
//        .andDo(print())
//        .andDo(document("categories-getByName",
//            pathParameters(
//                parameterWithName("name").description("찾고자 하는 카테고리의 이름")
//            ),
//            relaxedResponseFields(
//                fieldWithPath("[].id").description("카테고리 ID"),
//                fieldWithPath("[].name").description("카테고리 이름"),
//                fieldWithPath("[].children[]").description("하위 카테고리 리스트")
//            )
//        ));
//  }
}
