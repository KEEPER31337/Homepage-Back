package keeper.project.homepage.controller.library;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.common.dto.result.SingleResult;
import keeper.project.homepage.common.dto.sign.SignInDto;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional
public class LibraryMainControllerTest extends ApiControllerTestSetUp {

  private String userToken;

  final private String bookTitle1 = "Do it! ?????? ??? ?????????";
  final private String bookAuthor1 = "?????????";
  final private String bookPicture1 = "JumpToPython.png";
  final private String bookInformation1 = "???????????? ????????? ??? ????????? ?????????.";
  final private Long bookQuantity1 = 2L;
  final private Long bookBorrow1 = 0L;
  final private Long bookEnable1 = bookQuantity1;
  final private String bookRegisterDate1 = "20220116";

  final private String bookTitle2 = "???????????????";
  final private String bookAuthor2 = "??????";
  final private String bookPicture2 = "?????????.png";
  final private String bookInformation2 = "????????????";
  final private Long bookQuantity2 = 2L;
  final private Long bookBorrow2 = 1L;
  final private Long bookEnable2 = bookQuantity2 - bookBorrow2;
  final private String bookRegisterDate2 = "20220116";

  final private String bookTitle3 = "???????????????";
  final private String bookAuthor3 = "??????1";
  final private String bookPicture3 = "?????????1.png";
  final private String bookInformation3 = "???????????? ??? ?????????";
  final private Long bookQuantity3 = 2L;
  final private Long bookBorrow3 = 0L;
  final private Long bookEnable3 = bookQuantity3 - bookBorrow3;
  final private String bookRegisterDate3 = "20220116";

  final private String loginId = "hyeonmomo";
  final private String password = "keeper";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "gusah@naver.com";
  final private String studentId = "201724579";

  final private long epochTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

  private MemberEntity memberEntity;

  @BeforeEach
  public void setUp() throws Exception {
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_??????").get();
    MemberHasMemberJobEntity hasMemberJobEntity = MemberHasMemberJobEntity.builder()
        .memberJobEntity(memberJobEntity)
        .build();
    memberEntity = MemberEntity.builder()
        .loginId(loginId)
        .password(passwordEncoder.encode(password))
        .realName(realName)
        .nickName(nickName)
        .emailAddress(emailAddress)
        .studentId(studentId)
        .generation(0F)
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
    memberRepository.save(memberEntity);

    SimpleDateFormat stringToDate = new SimpleDateFormat("yyyymmdd");
    Date registerDate1 = stringToDate.parse(bookRegisterDate1);
    Date registerDate2 = stringToDate.parse(bookRegisterDate2);
    Date registerDate3 = stringToDate.parse(bookRegisterDate3);

    bookRepository.save(
        BookEntity.builder()
            .title(bookTitle1)
            .author(bookAuthor1)
            .information(bookInformation1)
            .total(bookQuantity1)
            .borrow(bookBorrow1)
            .enable(bookEnable1)
            .registerDate(registerDate1)
            .build());

    bookRepository.save(
        BookEntity.builder()
            .title(bookTitle2)
            .author(bookAuthor2)
            .information(bookInformation2)
            .total(bookQuantity2)
            .borrow(bookBorrow2)
            .enable(bookEnable2)
            .registerDate(registerDate2)
            .build());

    bookRepository.save(
        BookEntity.builder()
            .title(bookTitle3)
            .author(bookAuthor3)
            .information(bookInformation3)
            .total(bookQuantity3)
            .borrow(bookBorrow3)
            .enable(bookEnable3)
            .registerDate(registerDate3)
            .build());

    String content = "{\n"
        + "    \"loginId\": \"" + loginId + "\",\n"
        + "    \"password\": \"" + password + "\"\n"
        + "}";
    MvcResult result = mockMvc.perform(post("/v1/signin")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(content))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    String resultString = result.getResponse().getContentAsString();
    ObjectMapper mapper = new ObjectMapper();
    SingleResult<SignInDto> sign = mapper.readValue(resultString, new TypeReference<>() {
    });
    userToken = sign.getData().getToken();
  }

  //-------------------------------?????? ?????? ?????? ??????-----------------------------------
  @Test
  @DisplayName("?????? ?????? 10??? ??????")
  public void displayRecentBooks() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    mockMvc.perform(get("/v1/recentbooks").contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("display-recentbooks",
            requestParameters(
                parameterWithName("page").optional().description("????????? ??????(default = 0)"),
                parameterWithName("size").optional().description("??? ???????????? ?????? ???(default = 10)")
            ),
            responseFields(
                fieldWithPath("success").description("?????? ????????? ????????? ?????? true"),
                fieldWithPath("code").description("?????? ????????? ????????? ?????? 0"),
                fieldWithPath("msg").description("?????? ????????? ????????? ?????? ?????????????????????"),
                fieldWithPath("list[].id").description("??? ID"),
                fieldWithPath("list[].title").description("??? ??????"),
                fieldWithPath("list[].author").description("??? ??????"),
                fieldWithPath("list[].information").description("??? ??????"),
                fieldWithPath("list[].total").description("?????? ???"),
                fieldWithPath("list[].borrow").description("?????? ?????? ???"),
                fieldWithPath("list[].enable").description("?????? ????????? ???"),
                fieldWithPath("list[].registerDate").description("????????? ??????"),
                fieldWithPath("list[].department").description("?????? ?????? ??????").optional(),
                fieldWithPath("list[].thumbnailPath").description("????????? ?????? api path").optional()
            )));
  }

  //-------------------------------?????? ?????? ??????-----------------------------------
  @Test
  @DisplayName("?????? ??????")
  public void searchBooks() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("keyword", "?????????");

    mockMvc.perform(get("/v1/searchbooks")
            .params(params)
            .param("page", "0")
            .param("size", "5")
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("search-books",
            requestParameters(
                parameterWithName("keyword").description("?????????"),
                parameterWithName("page").optional().description("????????? ??????(default = 0)"),
                parameterWithName("size").optional().description("??? ???????????? ?????? ???(default = 10)")
            ),
            responseFields(
                fieldWithPath("success").description("?????? ????????? ????????? ?????? true"),
                fieldWithPath("code").description("?????? ????????? ????????? ?????? 0"),
                fieldWithPath("msg").description("?????? ????????? ????????? ?????? ?????????????????????"),
                fieldWithPath("list[].id").description("??? ID"),
                fieldWithPath("list[].title").description("??? ??????"),
                fieldWithPath("list[].author").description("??? ??????"),
                fieldWithPath("list[].information").description("??? ??????"),
                fieldWithPath("list[].total").description("?????? ???"),
                fieldWithPath("list[].borrow").description("?????? ?????? ???"),
                fieldWithPath("list[].enable").description("?????? ????????? ???"),
                fieldWithPath("list[].registerDate").description("????????? ??????"),
                fieldWithPath("list[].department").description("?????? ?????? ??????").optional(),
                fieldWithPath("list[].thumbnailPath").description("????????? ?????? api path").optional()
            )));
  }

  //-------------------------------?????? ?????? ??????-----------------------------------
  @Test
  @DisplayName("?????? ?????? ??????")
  public void selectedBookInformation() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle1);
    params.add("author", bookAuthor1);

    mockMvc.perform(get("/v1/selectedbook/information")
            .params(params)
            .contentType(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("selected-book",
            requestParameters(
                parameterWithName("title").description("??? ??????"),
                parameterWithName("author").description("??? ??????")
            ),
            responseFields(
                fieldWithPath("success").description("?????? ????????? ????????? ?????? true"),
                fieldWithPath("code").description("?????? ????????? ????????? ?????? 0"),
                fieldWithPath("msg").description("?????? ????????? ????????? ?????? ?????????????????????"),
                fieldWithPath("data.id").description("??? ID"),
                fieldWithPath("data.title").description("??? ??????"),
                fieldWithPath("data.author").description("??? ??????"),
                fieldWithPath("data.information").description("??? ??????"),
                fieldWithPath("data.total").description("?????? ???"),
                fieldWithPath("data.borrow").description("?????? ?????? ???"),
                fieldWithPath("data.enable").description("?????? ????????? ???"),
                fieldWithPath("data.registerDate").description("????????? ??????"),
                fieldWithPath("data.department").description("?????? ?????? ??????").optional(),
                fieldWithPath("data.thumbnailPath").description("????????? ?????? api path").optional()
            )));
  }
}
