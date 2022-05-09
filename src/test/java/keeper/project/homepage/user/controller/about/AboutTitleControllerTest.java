package keeper.project.homepage.user.controller.about;

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

import java.io.File;
import keeper.project.homepage.ApiControllerTestHelper;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.etc.StaticWriteContentEntity;
import keeper.project.homepage.entity.etc.StaticWriteSubtitleImageEntity;
import keeper.project.homepage.entity.etc.StaticWriteTitleEntity;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Log4j2
public class AboutTitleControllerTest extends ApiControllerTestHelper {

  private StaticWriteTitleEntity staticWriteTitleEntity;
  private StaticWriteTitleEntity newStaticWriteTitleEntity;
  private ThumbnailEntity thumbnailEntity;
  private FileEntity fileEntity;

  private final String ipAddress1 = "127.0.0.1";
  private final String generalTestImage = "keeper_files" + File.separator + "image.jpg";
  private final String generalThumbnailImage =
      "keeper_files" + File.separator + "thumbnail" + File.separator + "t_image.jpg";

  @BeforeEach
  public void setUp() throws Exception {
    fileEntity = FileEntity.builder()
        .fileName(getFileName(generalTestImage))
        .filePath(generalTestImage)
        .fileSize(0L)
        .ipAddress(ipAddress1)
        .build();
    fileRepository.save(fileEntity);

    thumbnailEntity = ThumbnailEntity.builder()
        .path(generalThumbnailImage)
        .file(fileEntity).build();
    thumbnailRepository.save(thumbnailEntity);

    staticWriteTitleEntity = staticWriteTitleRepository.findAllByType("activity").get(0);

    newStaticWriteTitleEntity = StaticWriteTitleEntity.builder()
        .title("테스트 타이틀")
        .type("activity")
        .build();
    staticWriteTitleRepository.save(newStaticWriteTitleEntity);

    for (int i = 0; i < 3; i++) {
      StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = generateTestSubTitle(
          staticWriteTitleEntity, i);

      for (int j = 0; j < 2; j++) {
        generateTestContent(staticWriteSubtitleImageEntity, j);
      }
    }

  }

  public StaticWriteSubtitleImageEntity generateTestSubTitle(
      StaticWriteTitleEntity staticWriteTitleEntity, Integer index) {
    StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity = StaticWriteSubtitleImageEntity.builder()
        .subtitle("세미나" + index)
        .displayOrder(index)
        .staticWriteTitle(staticWriteTitleEntity)
        .thumbnail(thumbnailEntity)
        .build();
    return staticWriteSubtitleImageRepository.save(staticWriteSubtitleImageEntity);
  }

  public void generateTestContent(StaticWriteSubtitleImageEntity staticWriteSubtitleImageEntity,
      Integer index) {
    StaticWriteContentEntity staticWriteContent = StaticWriteContentEntity.builder()
        .content("매주 금요일마다 정기적으로 운영" + index)
        .displayOrder(index)
        .staticWriteSubtitleImage(staticWriteSubtitleImageEntity)
        .build();
    staticWriteContentRepository.save(staticWriteContent);
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 타입으로 불러오기 - 성공")
  public void findAllByTypeSuccess() throws Exception {
    mockMvc.perform(get("/v1/about/title/type/{type}", "intro"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("aboutTitle-ByType-lists",
            pathParameters(
                parameterWithName("type").description("찾고자 하는 페이지 블럭 타이틀의 타입")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("list[].id").description("해당 타입과 일치하는 페이지 블럭 타이틀 데이터의 ID"),
                fieldWithPath("list[].title").description("해당 타입과 일치하는 페이지 블럭 타이틀 데이터의 제목"),
                fieldWithPath("list[].type").description("해당 타입과 일치하는 페이지 블럭 타이틀 데이터의 타입"),
                subsectionWithPath("list[].subtitleImageResults[]").description("페이지 블럭 타이틀과 연결된 페이지 블럭 서브 타이틀 데이터 리스트")
            )));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 타입으로 불러오기 - 실패(존재하지 않는 타입)")
  public void findAllByTypeFail() throws Exception {
    mockMvc.perform(get("/v1/about/title/type/{type}", "hello"))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 타입입니다."));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 제목으로 불러오기 - 성공")
  public void findByTitleSuccess() throws Exception {
    mockMvc.perform(get("/v1/about/title/{title}", "테스트 타이틀"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andDo(document("aboutTitle-ByTitle",
            pathParameters(
                parameterWithName("title").description("찾고자 하는 페이지 블럭 타이틀의 제목")
            ),
            responseFields(
                fieldWithPath("success").description("성공: true +\n실패: false"),
                fieldWithPath("code").description("성공 시 0을 반환"),
                fieldWithPath("msg").description("성공: 성공하였습니다 +\n실패: 에러 메세지 반환"),
                fieldWithPath("data.id").description("해당 제목과 일치하는 페이지 블럭 타이틀 데이터의 ID"),
                fieldWithPath("data.title").description("해당 제목과 일치하는 페이지 블럭 타이틀 데이터의 제목"),
                fieldWithPath("data.type").description("해당 제목과 일치하는 페이지 블럭 타이틀 데이터의 타입"),
                subsectionWithPath("data.subtitleImageResults[]").description(
                    "페이지 블럭 타이틀과 연결된 페이지 블럭 서브 타이틀 데이터 리스트")
            )));
  }

  @Test
  @DisplayName("페이지 블럭 타이틀 제목으로 불러오기 - 실패(존재하지 않는 제목)")
  public void findByTitleFail() throws Exception {
    mockMvc.perform(get("/v1/about/title/{title}", "테스트 타이틀!"))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.msg").value("존재하지 않는 제목입니다."));
  }
}
