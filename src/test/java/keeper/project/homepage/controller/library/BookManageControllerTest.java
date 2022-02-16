package keeper.project.homepage.controller.library;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import keeper.project.homepage.ApiControllerTestSetUp;
import keeper.project.homepage.common.FileConversion;
import keeper.project.homepage.dto.result.SingleResult;
import keeper.project.homepage.dto.sign.SignInDto;
import keeper.project.homepage.entity.FileEntity;
import keeper.project.homepage.entity.ThumbnailEntity;
import keeper.project.homepage.entity.library.BookBorrowEntity;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.entity.member.MemberEntity;
import keeper.project.homepage.entity.member.MemberHasMemberJobEntity;
import keeper.project.homepage.entity.member.MemberJobEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Transactional
public class BookManageControllerTest extends ApiControllerTestSetUp {

  private String userToken;
  private String adminToken;

  final private String bookTitle1 = "Do it! 점프 투 파이썬";
  final private String bookAuthor1 = "박응용";
  final private String bookPicture1 = "JumpToPython.png";
  final private String bookInformation1 = "파이썬의 기본이 잘 정리된 책이다.";
  final private Long bookQuantity1 = 3L;
  final private Long bookBorrow1 = 0L;
  final private Long bookEnable1 = bookQuantity1 - bookBorrow1;
  final private String bookRegisterDate1 = "20220116";

  final private String bookTitle2 = "일반물리학";
  final private String bookAuthor2 = "우웩";
  final private String bookPicture2 = "우우웩.png";
  final private String bookInformation2 = "우웩우웩";
  final private Long bookQuantity2 = 2L;
  final private Long bookBorrow2 = 1L;
  final private Long bookEnable2 = bookQuantity2 - bookBorrow2;
  final private String bookRegisterDate2 = "20220116";

  final private String bookTitle3 = "일반물리학";
  final private String bookAuthor3 = "우웩1";
  final private String bookPicture3 = "우우웩1.png";
  final private String bookInformation3 = "우웩우웩1";
  final private Long bookQuantity3 = 2L;
  final private Long bookBorrow3 = 0L;
  final private Long bookEnable3 = bookQuantity3 - bookBorrow3;
  final private String bookRegisterDate3 = "20220116";

  final private String loginId = "hyeonmomo";
  final private String password = "keeper3456";
  final private String realName = "JeongHyeonMo";
  final private String nickName = "JeongHyeonMo";
  final private String emailAddress = "gusah@naver.com";
  final private String studentId = "201724579";
  final private String ipAddress1 = "127.0.0.1";

  final private String adminLoginId = "hyeonmoAdmin";
  final private String adminPassword = "keeper2345";
  final private String adminRealName = "JeongHyeonMo2";
  final private String adminNickName = "JeongHyeonMo2";
  final private String adminEmailAddress = "test2@k33p3r.com";
  final private String adminStudentId = "201724580";

  final private long epochTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond();

  private MemberEntity memberEntity;

  private ThumbnailEntity generalThumbnail;
  private FileEntity generalImageFile;

  private final String userDirectory = System.getProperty("user.dir");
  private final String generalTestImage = "keeper_files" + File.separator + "image.jpg";
  private final String generalThumbnailImage =
      "keeper_files" + File.separator + "thumbnail" + File.separator + "t_image.jpg";
  private final String createTestImage = "keeper_files" + File.separator + "createTest.jpg";

  private String getFileName(String filePath) {
    File file = new File(filePath);
    return file.getName();
  }

  @BeforeAll
  public static void createFile() {
    final String keeperFilesDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files";
    final String thumbnailDirectoryPath = System.getProperty("user.dir") + File.separator
        + "keeper_files" + File.separator + "thumbnail";
    final String generalImagePath = keeperFilesDirectoryPath + File.separator + "image.jpg";
    final String generalThumbnail = thumbnailDirectoryPath + File.separator + "t_image.jpg";
    final String createTestImage = keeperFilesDirectoryPath + File.separator + "createTest.jpg";

    File keeperFilesDir = new File(keeperFilesDirectoryPath);
    File thumbnailDir = new File(thumbnailDirectoryPath);

    if (!keeperFilesDir.exists()) {
      keeperFilesDir.mkdir();
    }

    if (!thumbnailDir.exists()) {
      thumbnailDir.mkdir();
    }

    createImageForTest(generalImagePath);
    createImageForTest(generalThumbnail);
    createImageForTest(createTestImage);
  }

  private static void createImageForTest(String filePath) {
    FileConversion fileConversion = new FileConversion();
    fileConversion.makeSampleJPEGImage(filePath);
  }


  @BeforeEach
  public void setUp() throws Exception {
    MemberJobEntity memberJobEntity = memberJobRepository.findByName("ROLE_회장").get();
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
        .memberJobs(new ArrayList<>(List.of(hasMemberJobEntity)))
        .build();
    memberRepository.save(memberEntity);

    MemberJobEntity memberAdminJobEntity = memberJobRepository.findByName("ROLE_회장").get();
    MemberHasMemberJobEntity hasMemberAdminJobEntity = MemberHasMemberJobEntity.builder()
        .memberJobEntity(memberAdminJobEntity)
        .build();
    MemberEntity memberAdmin = MemberEntity.builder()
        .loginId(adminLoginId)
        .password(passwordEncoder.encode(adminPassword))
        .realName(adminRealName)
        .nickName(adminNickName)
        .emailAddress(adminEmailAddress)
        .studentId(adminStudentId)
        .memberJobs(new ArrayList<>(List.of(hasMemberAdminJobEntity)))
        .build();
    memberRepository.save(memberAdmin);

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

    String adminContent = "{\n"
        + "    \"loginId\": \"" + adminLoginId + "\",\n"
        + "    \"password\": \"" + adminPassword + "\"\n"
        + "}";
    MvcResult adminResult = mockMvc.perform(post("/v1/signin")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(adminContent))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andExpect(jsonPath("$.data").exists())
        .andReturn();

    String adminResultString = adminResult.getResponse().getContentAsString();
    SingleResult<SignInDto> adminSign = mapper.readValue(adminResultString, new TypeReference<>() {
    });
    adminToken = adminSign.getData().getToken();

    generalImageFile = FileEntity.builder()
        .fileName(getFileName(generalTestImage))
        .filePath(generalTestImage)
        .fileSize(0L)
        .ipAddress(ipAddress1)
        .build();
    fileRepository.save(generalImageFile);

    generalThumbnail = ThumbnailEntity.builder()
        .path(generalThumbnailImage)
        .file(generalImageFile).build();
    thumbnailRepository.save(generalThumbnail);

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
            .thumbnailId(generalThumbnail)
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

    BookEntity bookId = bookRepository.findByTitleAndAuthor(bookTitle1, bookAuthor1).get();
    MemberEntity memberId = memberRepository.findByLoginId(loginId).get();

    bookBorrowRepository.save(
        BookBorrowEntity.builder()
            .member(memberId)
            .book(bookId)
            .quantity(1L)
            .borrowDate(java.sql.Date.valueOf(getDate(-17)))
            .expireDate(java.sql.Date.valueOf(getDate(-3)))
            .build());

    bookBorrowRepository.save(
        BookBorrowEntity.builder()
            .member(memberId)
            .book(bookId)
            .quantity(1L)
            .borrowDate(java.sql.Date.valueOf(getDate(-8)))
            .expireDate(java.sql.Date.valueOf(getDate(1)))
            .build());
  }

  private String getDate(int date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DATE, date);

    return bookManageService.transferFormat(calendar.getTime());
  }

  //--------------------------도서 등록------------------------------------
  @Test
  @DisplayName("책 등록 성공(기존 책)")
  public void addBook() throws Exception {
    Long bookQuantity1 = 1L;

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    MockMultipartFile thumbnail = new MockMultipartFile("thumbnail", getFileName(createTestImage),
        "image/jpg", new FileInputStream(userDirectory + File.separator + createTestImage));

    params.add("title", bookTitle1);
    params.add("author", bookAuthor1);
    params.add("information", bookInformation1);
    params.add("quantity", String.valueOf(bookQuantity1));

    mockMvc.perform(multipart("/v1/admin/addbook")
            .file(thumbnail)
            .header("Authorization", adminToken)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .params(params)
            .with(request -> {
              request.setMethod("POST");
              return request;
            }))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andDo(document("add-book",
            requestParameters(
                parameterWithName("title").description("책 제목"),
                parameterWithName("author").description("저자"),
                parameterWithName("information").description("한줄평(없어도 됨)"),
                parameterWithName("quantity").description("추가 할 수량")
            ),
            requestParts(
                partWithName("thumbnail").description(
                    "썸네일 용 이미지 (form-data 에서 thumbnail= parameter 부분)")
            ),
            responseFields(
                fieldWithPath("success").description("책 추가 완료 시 true, 실패 시 false 값을 보냅니다."),
                fieldWithPath("code").description("책 추가 완료 시 0, 수량 초과로 실패 시 -1 코드를 보냅니다."),
                fieldWithPath("msg").description("책 추가 실패가 수량 초과 일 때만 발생하므로 수량 초과 메시지를 발생시킵니다.")
            )));
  }

  @Test
  @DisplayName("수량 초과 책 등록 실패(기존 책)")
  public void addBookFailedOverMax() throws Exception {
    Long bookQuantity1 = 3L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle1);
    params.add("author", bookAuthor1);
    params.add("quantity", String.valueOf(bookQuantity1));

    mockMvc.perform(post("/v1/addbook")
            .params(params)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1))
        .andExpect(jsonPath("$.msg").exists());
  }

  @Test
  @DisplayName("새 책 등록 성공")
  public void addNewBook() throws Exception {
    Long bookQuantity2 = 4L;
    String newTitle = "일반물리학2";
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", newTitle);
    params.add("author", bookAuthor1);
    params.add("quantity", String.valueOf(bookQuantity2));

    mockMvc.perform(post("/v1/addbook")
            .params(params)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists());
  }

  @Test
  @DisplayName("제목 같고 작가 다른 책 등록 성공")
  public void addNewBookSameTitle() throws Exception {
    Long bookQuantity2 = 4L;
    String newTitle = "Do it! 점프 투 파이썬";
    String newAuthor = "박재열";
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", newTitle);
    params.add("author", newAuthor);
    params.add("quantity", String.valueOf(bookQuantity2));

    mockMvc.perform(post("/v1/addbook")
            .params(params)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists());
  }

  @Test
  @DisplayName("수량 초과 새 책 등록 실패")
  public void addNewBookFailedOverMax() throws Exception {
    Long bookQuantity3 = 5L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle1 + epochTime);
    params.add("author", bookAuthor1);
    params.add("quantity", String.valueOf(bookQuantity3));

    mockMvc.perform(post("/v1/addbook")
            .params(params)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1))
        .andExpect(jsonPath("$.msg").exists());
  }

  //--------------------------도서 삭제------------------------------------
  @Test
  @DisplayName("책 삭제 성공(일부 삭제)")
  public void deleteBook() throws Exception {
    Long bookQuantity1 = 1L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle1);
    params.add("author", bookAuthor1);
    params.add("quantity", String.valueOf(bookQuantity1));

    mockMvc.perform(post("/v1/deletebook")
            .params(params)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andDo(document("delete-book",
            requestParameters(
                parameterWithName("title").description("책 제목"),
                parameterWithName("author").description("책 저자"),
                parameterWithName("quantity").description("삭제 할 수량")
            ),
            responseFields(
                fieldWithPath("success").description("책 삭제 완료 시 true, 실패 시 false 값을 보냅니다."),
                fieldWithPath("code").description(
                    "책 삭제 완료 시 0, 최대 수량 초과로 실패 시 -1, 없는 책으로 실패 시 -2 코드를 보냅니다."),
                fieldWithPath("msg").description(
                    "책 삭제 실패가 수량 초과 일 때 수량 초과 메시지를, 없는 책일 때 책이 없다는 메시지를 발생시킵니다.")
            )));
  }

  @Test
  @DisplayName("책 삭제 성공(전체 삭제)")
  public void deleteBookMax() throws Exception {
    Long bookQuantity3 = 3L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle1);
    params.add("author", bookAuthor1);
    params.add("quantity", String.valueOf(bookQuantity3));

    mockMvc.perform(post("/v1/deletebook")
            .params(params)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists());
  }

  @Test
  @DisplayName("책 삭제 실패(없는 책)")
  public void deleteBookFailedNoExist() throws Exception {
    Long bookQuantity3 = 1L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle1 + epochTime);
    params.add("author", bookAuthor1 + epochTime);
    params.add("quantity", String.valueOf(bookQuantity3));

    mockMvc.perform(post("/v1/deletebook")
            .params(params)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-2))
        .andExpect(jsonPath("$.msg").exists());
  }

  @Test
  @DisplayName("책 삭제 실패(기존보다 많은 수량-total기준)")
  public void deleteBookFailedOverMax1() throws Exception {
    Long bookQuantity3 = 5L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle1);
    params.add("author", bookAuthor1);
    params.add("quantity", String.valueOf(bookQuantity3));

    mockMvc.perform(post("/v1/deletebook")
            .params(params)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1))
        .andExpect(jsonPath("$.msg").exists());
  }

  @Test
  @DisplayName("책 삭제 실패(기존보다 많은 수량-enable기준)")
  public void deleteBookFailedOverMax2() throws Exception {
    Long bookQuantity3 = 2L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle2);
    params.add("author", bookAuthor2);
    params.add("quantity", String.valueOf(bookQuantity3));

    mockMvc.perform(post("/v1/deletebook")
            .params(params)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1))
        .andExpect(jsonPath("$.msg").exists());
  }

  //--------------------------도서 대여------------------------------------
  @Test
  @DisplayName("책 대여 성공")
  public void borrowBook() throws Exception {
    Long borrowQuantity = 2L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle1);
    params.add("author", bookAuthor1);
    params.add("quantity", String.valueOf(borrowQuantity));

    mockMvc.perform(post("/v1/borrowbook").params(params).header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andDo(document("borrow-book",
            requestParameters(
                parameterWithName("title").description("책 제목"),
                parameterWithName("author").description("저자"),
                parameterWithName("quantity").description("대여 할 수량")
            ),
            responseFields(
                fieldWithPath("success").description("책 대여 완료 시 true, 실패 시 false 값을 보냅니다."),
                fieldWithPath("code").description(
                    "책 대여 완료 시 0, 수량 초과로 실패 시 -1, 존재하지 않을 시 -2 코드를 보냅니다."),
                fieldWithPath("msg").description(
                    "책 대여 실패가 수량 초과 일 때 수량 초과 메시지를, 없는 책일 때 책이 없다는 메시지를 발생시킵니다.")
            )));
  }

  @Test
  @DisplayName("책 대여 실패(수량 초과)")
  public void borrowBookFailedOverMax() throws Exception {
    Long borrowQuantity = 2L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle2);
    params.add("author", bookAuthor2);
    params.add("quantity", String.valueOf(borrowQuantity));

    mockMvc.perform(post("/v1/borrowbook").params(params).header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1))
        .andExpect(jsonPath("$.msg").exists());
  }

  @Test
  @DisplayName("책 대여 실패(없는 책)")
  public void borrowBookFailedNotExist() throws Exception {
    Long borrowQuantity = 1L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle2 + epochTime);
    params.add("author", bookAuthor2);
    params.add("quantity", String.valueOf(borrowQuantity));

    mockMvc.perform(post("/v1/borrowbook").params(params).header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-2))
        .andExpect(jsonPath("$.msg").exists());
  }

  //--------------------------도서 반납------------------------------------
  @Test
  @DisplayName("책 반납 성공(전부 반납)")
  public void returnBookAll() throws Exception {
    Long returnQuantity = 1L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle1);
    params.add("author", bookAuthor1);
    params.add("quantity", String.valueOf(returnQuantity));

    mockMvc.perform(post("/v1/returnbook")
            .params(params)
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists())
        .andDo(document("return-book",
            requestParameters(
                parameterWithName("title").description("책 제목"),
                parameterWithName("author").description("저자"),
                parameterWithName("quantity").description("반납 할 수량")
            ),
            responseFields(
                fieldWithPath("success").description("책 반납 완료 시 true, 실패 시 false 값을 보냅니다."),
                fieldWithPath("code").description(
                    "책 반납 완료 시 0, 수량 초과로 실패 시 -1, 존재하지 않을 시 -2 코드를 보냅니다."),
                fieldWithPath("msg").description(
                    "책 반납 실패가 수량 초과 일 때 수량 초과 메시지를, 없는 책일 때 책이 없다는 메시지를 발생시킵니다.")
            )));
  }

  @Test
  @DisplayName("책 반납 성공(일부 반납)")
  public void returnBookPart() throws Exception {
    Long returnQuantity = 1L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle1);
    params.add("author", bookAuthor1);
    params.add("quantity", String.valueOf(returnQuantity));

    mockMvc.perform(post("/v1/returnbook")
            .params(params)
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists());
  }

  @Test
  @DisplayName("책 반납 실패(수량 초과)")
  public void returnBookFailedOverMax() throws Exception {
    Long borrowQuantity = 3L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle1);
    params.add("author", bookAuthor1);
    params.add("quantity", String.valueOf(borrowQuantity));

    mockMvc.perform(post("/v1/returnbook")
            .params(params)
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-1))
        .andExpect(jsonPath("$.msg").exists());
  }

  @Test
  @DisplayName("책 반납 실패(없는 책)")
  public void returnBookFailedNotExist() throws Exception {
    Long borrowQuantity = 1L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle2 + epochTime);
    params.add("author", bookAuthor2);
    params.add("quantity", String.valueOf(borrowQuantity));

    mockMvc.perform(post("/v1/returnbook")
            .params(params)
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-2))
        .andExpect(jsonPath("$.msg").exists());
  }

  @Test
  @DisplayName("책 반납 실패(대출 안 한 책)")
  public void returnBookFailedNotBorrowExist() throws Exception {
    Long borrowQuantity = 1L;
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle2);
    params.add("author", bookAuthor2);
    params.add("quantity", String.valueOf(borrowQuantity));

    mockMvc.perform(post("/v1/returnbook")
            .params(params)
            .header("Authorization", userToken))
        .andDo(print())
        .andExpect(status().is5xxServerError())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value(-3))
        .andExpect(jsonPath("$.msg").exists());
  }

  //--------------------------연체 도서 표시------------------------------------
  @Test
  @DisplayName("연체 도서 표시(연체, 3일전)")
  public void sendOverdueBooks() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    mockMvc.perform(get("/v1/overduebooks")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", adminToken))
        .andDo(print())
        .andExpect(status().isOk())
        .andDo(document("overdue-books",
            requestParameters(
                parameterWithName("page").optional().description("페이지 번호(default = 0)"),
                parameterWithName("size").optional().description("한 페이지당 출력 수(default = 10)")
            ),
            responseFields(
                fieldWithPath("success").description("연체 도서 전달 시 true, 실패 시 false 값을 보냅니다."),
                fieldWithPath("code").description(
                    "연체 도서 전달 성공 시 0, 실패 시 -11 코드를 보냅니다."),
                fieldWithPath("msg").description(
                    "연체 도서 전달 실패 시 연체 도서가 없다는 메시지를 전달합니다.")
            ).andWithPrefix("list.", fieldWithPath("[].id").description("대여정보 ID"),
                subsectionWithPath("[].member").description("대여자 ID"),
                subsectionWithPath("[].book").description("책 ID"),
                fieldWithPath("[].quantity").description("대여 수량"),
                fieldWithPath("[].borrowDate").description("대여일"),
                fieldWithPath("[].expireDate").description("만기일"))));
  }
}
