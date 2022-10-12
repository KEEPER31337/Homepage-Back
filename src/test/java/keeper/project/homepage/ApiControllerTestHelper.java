package keeper.project.homepage;

import static java.util.stream.Collectors.toList;
import static keeper.project.homepage.sign.service.SignUpService.HALF_GENERATION_MONTH;
import static keeper.project.homepage.sign.service.SignUpService.KEEPER_FOUNDING_YEAR;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import keeper.project.homepage.member.entity.MemberHasMemberJobEntity;
import keeper.project.homepage.util.dto.result.SingleResult;
import keeper.project.homepage.sign.dto.SignInDto;
import keeper.project.homepage.util.entity.FileEntity;
import keeper.project.homepage.util.entity.ThumbnailEntity;
import keeper.project.homepage.attendance.entity.AttendanceEntity;
import keeper.project.homepage.member.entity.MemberEntity;
import keeper.project.homepage.member.entity.MemberJobEntity;
import keeper.project.homepage.member.entity.MemberRankEntity;
import keeper.project.homepage.member.entity.MemberTypeEntity;
import keeper.project.homepage.point.entity.PointLogEntity;
import keeper.project.homepage.posting.entity.CategoryEntity;
import keeper.project.homepage.posting.entity.CommentEntity;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.util.FileConversion;
import keeper.project.homepage.util.dto.FileDto;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.MvcResult;

public class ApiControllerTestHelper extends ApiControllerTestSetUp {

  public final String memberPassword = "memberPassword";
  public final String postingPassword = "postingPassword";

  public static final String usrDir = System.getProperty("user.dir") + File.separator;
  public static final String testFileRelDir =
      "keeper_files" + File.separator + "test";
  public static final String testThumbRelDir =
      testFileRelDir + File.separator + "thumbnail";

  public enum MemberJobName {
    회장("ROLE_회장"),
    부회장("ROLE_부회장"),
    대외부장("ROLE_대외부장"),
    학술부장("ROLE_학술부장"),
    전산관리자("ROLE_전산관리자"),
    서기("ROLE_서기"),
    총무("ROLE_총무"),
    사서("ROLE_사서"),
    회원("ROLE_회원"),
    출제자("ROLE_출제자");

    private String jobName;

    MemberJobName(String jobName) {
      this.jobName = jobName;
    }

    public String getJobName() {
      return jobName;
    }
  }

  public enum MemberRankName {
    일반회원("일반회원"),
    우수회원("우수회원");

    private String rankName;

    MemberRankName(String rankName) {
      this.rankName = rankName;
    }

    public String getRankName() {
      return rankName;
    }
  }

  public enum MemberTypeName {
    비회원("비회원"), 정회원("정회원"), 휴면회원("휴면회원"), 졸업("졸업"), 탈퇴("탈퇴");

    private String typeName;

    MemberTypeName(String typeName) {
      this.typeName = typeName;
    }

    public String getTypeName() {
      return typeName;
    }

  }

  public enum ResponseType {
    SINGLE("data"),
    LIST("list[]"),
    PAGE("page.content[]");

    private final String reponseFieldPrefix;

    ResponseType(String reponseFieldPrefix) {
      this.reponseFieldPrefix = reponseFieldPrefix;
    }

    public String getReponseFieldPrefix() {
      return reponseFieldPrefix;
    }
  }

  public Float getMemberGeneration() {
    LocalDate date = LocalDate.now();
    Float generation = (float) (date.getYear() - KEEPER_FOUNDING_YEAR);
    if (date.getMonthValue() >= HALF_GENERATION_MONTH) {
      generation += 0.5F;
    }
    return generation;
  }

  public String getFileName(String filePath) {
    File file = new File(filePath);
    return file.getName();
  }

  public void createFileForTest(String filePath) {
    FileConversion fileConversion = new FileConversion();
    fileConversion.makeSampleJPEGImage(filePath);
  }

  public void deleteTestFile(FileEntity fileEntity) {
    if (fileService.isDefaultFileId(fileEntity.getId())) {
      return;
    }

    final String usrDir = System.getProperty("user.dir") + File.separator;
    File file = new File(usrDir + fileEntity.getFilePath());
    try {
      Files.deleteIfExists(file.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void deleteTestThumbnailFile(ThumbnailEntity thumbnailEntity) {
    if (thumbnailService.isDefaultThumbnail(thumbnailEntity.getId())) {
      return;
    }

    final String usrDir = System.getProperty("user.dir") + File.separator;
    File file = new File(usrDir + thumbnailEntity.getPath());
    try {
      Files.deleteIfExists(file.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
    deleteTestFile(thumbnailEntity.getFile());
  }

  private static void deleteFolder(String dirPath) {
    Path path = Paths.get(dirPath);
    try {
      if (Files.exists(path) && Files.isDirectory(path)) {
        List<Path> folder_list = Files.list(path).collect(toList()); //파일리스트 얻어오기

        for (int i = 0; i < folder_list.size(); i++) {
          if (Files.isRegularFile(folder_list.get(i))) {
            if (Files.deleteIfExists(folder_list.get(i))) {
              System.out.println("파일이 삭제되었습니다.");
            } else {
              System.out.println("파일이 삭제되지 않았습니다. (파일 경로: " + folder_list.get(i).toString() + ")");
            }
          } else if (Files.isDirectory(folder_list.get(i))) {
            deleteFolder(folder_list.get(i).toString()); //재귀함수호출
          }
        }
        if (Files.deleteIfExists(path)) { //폴더 삭제
          System.out.println("폴더가 삭제되었습니다.");
        } else {
          System.out.println("폴더가 삭제되지 않았습니다. (파일 경로: " + path.toString() + ")");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void deleteTestFiles() {
    // fixme :
    //  Bug : file delete가 제대로 수행되지 않음
    //  Exception Msg : "다른 프로세스가 파일을 사용 중이기 때문에 프로세스가 액세스 할 수 없습니다"
    //  Caused By : (추측) file output stream을 close했지만, gc가 메모리 상에서 완전히 풀어주기 전에 삭제를 시도하려 해서 그런 듯 함.
    //  Solve : System.gc()를 호출해서 gc가 stream을 정리하게 함.
    //    하지만 System.gc()를 사용자가 호출하는 것은 성능에 매우 안좋은 영향을 끼침. 테스트가 아니면 사용하지 말 것.
    System.gc();
    deleteFolder(usrDir + testFileRelDir);
  }

  private void checkFileDirectoryExist() {
    if (!new File(usrDir + testFileRelDir).exists()) {
      new File(usrDir + testFileRelDir).mkdir();
    }
  }

  private void checkThumbnailDirectoryExist() {
    checkFileDirectoryExist();
    if (!new File(usrDir + testThumbRelDir).exists()) {
      new File(usrDir + testThumbRelDir).mkdir();
    }
  }

  public FileEntity generateFileEntity() {
    final String epochTime = Long.toHexString(System.nanoTime());
    final String fileName = epochTime + ".jpg";

    checkFileDirectoryExist();
    createFileForTest(usrDir + testFileRelDir + File.separator + fileName);

    return fileRepository.save(FileEntity.builder()
        .fileName(fileName)
        .filePath(testFileRelDir + File.separator + fileName)
        .fileSize(0L)
        .ipAddress("111.111.111.111")
        .build());
  }

  public ThumbnailEntity generateThumbnailEntity() {
    final String epochTime = Long.toHexString(System.nanoTime());
    final String thumbName = "thumb_" + epochTime + ".jpg";

    checkThumbnailDirectoryExist();
    createFileForTest(usrDir + testThumbRelDir + File.separator + thumbName);

    FileEntity fileEntity = generateFileEntity();

    return thumbnailRepository.save(ThumbnailEntity.builder()
        .path(testThumbRelDir + File.separator + thumbName)
        .file(fileEntity).build());
  }

  public MemberEntity generateMemberEntity(MemberJobName jobName, MemberTypeName typeName,
      MemberRankName rankName) {
    final String epochTime = Long.toHexString(System.nanoTime());
    ThumbnailEntity thumbnailEntity = generateThumbnailEntity();
    MemberJobEntity memberJob = memberJobRepository.findByName(jobName.getJobName()).get();
    MemberTypeEntity memberType = memberTypeRepository.findByName(typeName.getTypeName()).get();
    MemberRankEntity memberRank = memberRankRepository.findByName(rankName.getRankName()).get();

    MemberEntity memberEntity = MemberEntity.builder()
        .loginId("LoginId" + epochTime)
        .password(passwordEncoder.encode(memberPassword))
        .realName("RealName" + epochTime)
        .nickName("NickName")
        .emailAddress("member" + epochTime + "@k33p3r.com")
        .studentId(epochTime)
        .generation(getMemberGeneration())
        .memberType(memberType)
        .memberRank(memberRank)
        .point(1000)
        .thumbnail(thumbnailEntity)
        .build();
    memberEntity.addMemberJob(memberJob);
    memberRepository.save(memberEntity);

    return memberEntity;
  }

  private String memberLogin(String content) throws Exception {
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
    return sign.getData().getToken();
  }

  public String generateJWTToken(MemberEntity member) throws Exception {
    String content = "{\n"
        + "    \"loginId\": \"" + member.getLoginId() + "\",\n"
        + "    \"password\": \"" + memberPassword + "\"\n"
        + "}";
    return memberLogin(content);
  }

  public String generateJWTToken(String loginId, String password) throws Exception {
    String content = "{\n"
        + "    \"loginId\": \"" + loginId + "\",\n"
        + "    \"password\": \"" + password + "\"\n"
        + "}";
    return memberLogin(content);
  }

  public CategoryEntity generateCategoryEntity() {
    final String epochTime = Long.toHexString(System.nanoTime());
    return categoryRepository.save(
        CategoryEntity.builder().name("testCategory" + epochTime).build());
  }

  public CategoryEntity generateAnonymousCategoryEntity() {
    return categoryRepository.findByName("익명게시판");
  }

  public PostingEntity generatePostingEntity(MemberEntity writer, CategoryEntity category,
      Integer isNotice, Integer isSecret, Integer isTemp) {
    final String epochTime = Long.toHexString(System.nanoTime());
    final LocalDateTime now = LocalDateTime.now();
    PostingEntity posting = postingRepository.save(PostingEntity.builder()
        .title("posting 제목 " + epochTime)
        .content("posting 내용 " + epochTime)
        .categoryId(category)
        .ipAddress("111.111.111.111")
        .allowComment(0)
        .isNotice(isNotice)
        .isSecret(isSecret)
        .isTemp(isTemp)
        .likeCount(0)
        .dislikeCount(0)
        .commentCount(0)
        .visitCount(0)
        .registerTime(now)
        .updateTime(now)
        .password(postingPassword)
        .memberId(writer)
        .build());
    writer.getPosting().add(posting);
    return posting;
  }

  public PointLogEntity generatePointLogEntity(MemberEntity member, Integer point,
      Integer isSpent) {
    final long epochTime = System.nanoTime();
    final LocalDateTime now = LocalDateTime.now();
    String pointUse = null;
    if (isSpent == 0) {
      pointUse = "적립";
    } else if (isSpent == 1) {
      pointUse = "사용";
    } else {
      pointUse = "예외";
    }
    return pointLogRepository.save(
        PointLogEntity.builder()
            .member(member)
            .point(point)
            .detail(pointUse + epochTime)
            .time(now)
            .isSpent(isSpent)
            .build()
    );
  }

  public PointLogEntity generatePointGiftLogEntity(MemberEntity sender, MemberEntity receiver,
      Integer point) {
    final long epochTime = System.nanoTime();
    final LocalDateTime now = LocalDateTime.now();
    pointLogRepository.save(
        PointLogEntity.builder()
            .member(receiver)
            .point(point)
            .detail("받은 선물" + epochTime)
            .time(now)
            .presentedMember(sender)
            .isSpent(0)
            .build()
    );
    return pointLogRepository.save(
        PointLogEntity.builder()
            .member(sender)
            .point(point)
            .detail("준 선물" + epochTime)
            .time(now)
            .presentedMember(receiver)
            .isSpent(1)
            .build()
    );
  }

  public CommentEntity generateCommentEntity(PostingEntity posting, MemberEntity writer,
      Long parentId) {
    final String epochTime = Long.toHexString(System.nanoTime());
    final LocalDateTime now = LocalDateTime.now();
    final String content = (parentId == 0L ? "댓글 내용 " : parentId + "의 대댓글 내용 ") + epochTime;

    posting.increaseCommentCount();
    postingRepository.save(posting);
    return commentRepository.save(CommentEntity.builder()
        .content(content)
        .registerTime(now)
        .updateTime(now)
        .ipAddress("111.111.111.111")
        .likeCount(0)
        .dislikeCount(0)
        .parentId(parentId)
        .member(writer)
        .postingId(posting)
        .build());
  }

  public void generateNewAttendanceWithTime(LocalDateTime time, MemberEntity memberEntity)
      throws Exception {
    attendanceRepository.save(
        AttendanceEntity.builder()
            .time(time)
            .date(time.toLocalDate())
            .point(10)
            .rankPoint(500)
            .continuousPoint(0)
            .randomPoint((int) (Math.random() * 900 + 100))
            .ipAddress("127.0.0.1")
            .greetings("hi")
            .continuousDay(1)
            .rank(3)
            .member(memberEntity)
            .build());
  }

  public List<FieldDescriptor> generateCommonResponseFields(String docSuccess, String docCode,
      String docMsg) {
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(Arrays.asList(
        fieldWithPath("success").description(docSuccess),
        fieldWithPath("code").description(docCode),
        fieldWithPath("msg").description(docMsg)));
    return commonFields;
  }

  public List<FieldDescriptor> generateCommonMemberCommonResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".id").description("아이디"),
        fieldWithPath(prefix + ".nickName").description("닉네임"),
        fieldWithPath(prefix + ".generation").description("기수 (7월 이후는 N.5기)"),
        fieldWithPath(prefix + ".thumbnailPath").description("회원의 썸네일 이미지 주소"),
        fieldWithPath(prefix + ".jobs").description(
            "동아리 직책: null, ROLE_회장, ROLE_부회장, ROLE_대외부장, ROLE_학술부장, ROLE_전산관리자, ROLE_서기, ROLE_총무, ROLE_사서"))
    );
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  public List<FieldDescriptor> generateMemberCommonResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".id").description("아이디"),
        fieldWithPath(prefix + ".emailAddress").description("이메일 주소"),
        fieldWithPath(prefix + ".nickName").description("닉네임"),
        fieldWithPath(prefix + ".birthday").description("생일").type(Date.class).optional(),
        fieldWithPath(prefix + ".registerDate").description("가입 날짜"),
        fieldWithPath(prefix + ".point").description("포인트 점수"),
        fieldWithPath(prefix + ".level").description("레벨"),
        fieldWithPath(prefix + ".merit").description("상점"),
        fieldWithPath(prefix + ".demerit").description("벌점"),
        fieldWithPath(prefix + ".generation").description("기수 (7월 이후는 N.5기)"),
        fieldWithPath(prefix + ".thumbnailPath").description("회원의 썸네일 이미지 조회 api path"),
        fieldWithPath(prefix + ".rank").description("회원 등급: null, 우수회원, 일반회원"),
        fieldWithPath(prefix + ".type").description("회원 상태: null, 비회원, 정회원, 휴면회원, 졸업회원, 탈퇴"),
        fieldWithPath(prefix + ".jobs").description(
            "동아리 직책: null, ROLE_회장, ROLE_부회장, ROLE_대외부장, ROLE_학술부장, ROLE_전산관리자, ROLE_서기, ROLE_총무, ROLE_사서"))
    );
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  public List<FieldDescriptor> generatePrivateMemberCommonResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".id").description("아이디"),
        fieldWithPath(prefix + ".loginId").description("로그인 아이디"),
        fieldWithPath(prefix + ".emailAddress").description("이메일 주소"),
        fieldWithPath(prefix + ".realName").description("실제 이름"),
        fieldWithPath(prefix + ".nickName").description("닉네임"),
        fieldWithPath(prefix + ".birthday").description("생일").type(Date.class).optional(),
        fieldWithPath(prefix + ".registerDate").description("학"),
        fieldWithPath(prefix + ".studentId").description("가입 날짜"),
        fieldWithPath(prefix + ".point").description("포인트 점수"),
        fieldWithPath(prefix + ".level").description("레벨"),
        fieldWithPath(prefix + ".merit").description("상점"),
        fieldWithPath(prefix + ".demerit").description("벌점"),
        fieldWithPath(prefix + ".generation").description("기수 (7월 이후는 N.5기)"),
        fieldWithPath(prefix + ".thumbnailPath").description("회원의 썸네일 이미지 조회 api path").optional(),
        fieldWithPath(prefix + ".rank").description("회원 등급: null, 우수회원, 일반회원"),
        fieldWithPath(prefix + ".type").description("회원 상태: null, 비회원, 정회원, 휴면회원, 졸업회원, 탈퇴"),
        fieldWithPath(prefix + ".jobs").description(
            "동아리 직책: null, ROLE_회장, ROLE_부회장, ROLE_대외부장, ROLE_학술부장, ROLE_전산관리자, ROLE_서기, ROLE_총무, ROLE_사서"))
    );
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  public List<FieldDescriptor> generateOtherMemberInfoCommonResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".memberId").description("해당 유저의 ID"),
        fieldWithPath(prefix + ".nickName").description("해당 유저의 닉네임"),
        fieldWithPath(prefix + ".birthday").description("해당 유저의 생일").type(Date.class).optional(),
        fieldWithPath(prefix + ".checkFollowee").description(
            "상대방을 팔로우 했는지 확인(팔로우: true, 아니면: false)"),
        fieldWithPath(prefix + ".checkFollower").description(
            "상대방이 나를 팔로우 했는지 확인(팔로우: true, 아니면: false)"),
        fieldWithPath(prefix + ".generation").description("기수 (7월 이후는 N.5기)").optional(),
        fieldWithPath(prefix + ".thumbnailPath").description("해당 유저의 썸네일 이미지 조회 api path")
            .optional(),
        fieldWithPath(prefix + ".memberRank").description("회원 등급: null, 우수회원, 일반회원").optional(),
        fieldWithPath(prefix + ".memberType").description("회원 상태: null, 비회원, 정회원, 휴면회원, 졸업회원, 탈퇴")
            .optional(),
        fieldWithPath(prefix + ".memberJobs").description(
            "동아리 직책: null, ROLE_회장, ROLE_부회장, ROLE_대외부장, ROLE_학술부장, ROLE_전산관리자, ROLE_서기, ROLE_총무, ROLE_사서")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  // TODO : PostingDto -> PostingResponseDto로 바꾼 후, .._Legacy 메소드 삭제
  public List<FieldDescriptor> generatePostingResponseFields_Legacy(ResponseType type,
      String success,
      String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        // FIXME : PostingDto에 id와 writer가 없음
//        fieldWithPath(prefix + ".id").description("게시물 ID"),
        fieldWithPath(prefix + ".title").description("게시물 제목"),
        fieldWithPath(prefix + ".content").description("게시물 내용"),
//        fieldWithPath(prefix + ".writer").description("작성자  (비밀 게시글일 경우 익명)"),
        fieldWithPath(prefix + ".visitCount").description("조회 수"),
        fieldWithPath(prefix + ".likeCount").description("좋아요 수"),
        fieldWithPath(prefix + ".dislikeCount").description("싫어요 수"),
        fieldWithPath(prefix + ".commentCount").description("댓글 수"),
        fieldWithPath(prefix + ".registerTime").description("작성 시간"),
        fieldWithPath(prefix + ".updateTime").description("수정 시간"),
        fieldWithPath(prefix + ".ipAddress").description("IP 주소"),
        fieldWithPath(prefix + ".allowComment").description("댓글 허용?"),
        fieldWithPath(prefix + ".isNotice").description("공지글?"),
        fieldWithPath(prefix + ".isSecret").description("비밀글?"),
        fieldWithPath(prefix + ".isTemp").description("임시저장?"),
        fieldWithPath(prefix + ".password").description("비밀번호").optional(),
        fieldWithPath(prefix + ".memberId").description("작성자 아이디"),
        fieldWithPath(prefix + ".categoryId").description("카테고리 아이디"),
        fieldWithPath(prefix + ".category").description("카테고리 이름"),
        fieldWithPath(prefix + ".thumbnailId").description("게시글 썸네일 아이디")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  public List<FieldDescriptor> generatePostingResponseFields(ResponseType type, String success,
      String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".id").description("게시물 ID"),
        fieldWithPath(prefix + ".title").description("게시물 제목"),
        fieldWithPath(prefix + ".content").description("게시물 내용 (비밀 게시글일 경우 \"비밀 게시글입니다.\""),
        fieldWithPath(prefix + ".writer").description("작성자 (비밀 게시글일 경우 \"익명\")"),
        fieldWithPath(prefix + ".writerId").description("작성자 아이디 (비밀 게시글일 경우 -1)"),
        fieldWithPath(prefix + ".writerThumbnailPath").description(
            "작성자 썸네일 이미지 조회 api path (비밀 게시글일 경우 null)").type(String.class).optional(),
        fieldWithPath(prefix + ".size").description("조건에 따라 조회한 게시글의 총 개수"),
        fieldWithPath(prefix + ".visitCount").description("조회 수"),
        fieldWithPath(prefix + ".likeCount").description("좋아요 수"),
        fieldWithPath(prefix + ".dislikeCount").description("싫어요 수"),
        fieldWithPath(prefix + ".commentCount").description("댓글 수"),
        fieldWithPath(prefix + ".registerTime").description("작성 시간"),
        fieldWithPath(prefix + ".updateTime").description("수정 시간"),
        fieldWithPath(prefix + ".ipAddress").description("IP 주소"),
        fieldWithPath(prefix + ".allowComment").description("댓글 허용?"),
        fieldWithPath(prefix + ".isNotice").description("공지글?"),
        fieldWithPath(prefix + ".isSecret").description("비밀글?"),
        fieldWithPath(prefix + ".isTemp").description("임시저장?"),
        fieldWithPath(prefix + ".category").description("카테고리 이름"),
        fieldWithPath(prefix + ".categoryId").description("카테고리 ID"),
        fieldWithPath(prefix + ".thumbnailPath").description("게시글 썸네일 이미지 조회 api path")
            .type(String.class).optional(),
        subsectionWithPath(prefix + ".files").description("첨부파일").type(FileDto.class).optional()
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }

  public List<FieldDescriptor> generateCommonCommentResponse(ResponseType type, String docSuccess,
      String docCode, String docMsg, FieldDescriptor... descriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(docSuccess, docCode, docMsg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".id").description("댓글 id"),
        fieldWithPath(prefix + ".content").description("댓글 내용"),
        fieldWithPath(prefix + ".registerTime").description("댓글이 처음 등록된 시간"),
        fieldWithPath(prefix + ".updateTime").description("댓글이 수정된 시간"),
        fieldWithPath(prefix + ".ipAddress").description("댓글 작성자의 ip address"),
        fieldWithPath(prefix + ".likeCount").description("좋아요 개수"),
        fieldWithPath(prefix + ".dislikeCount").description("싫어요 개수"),
        fieldWithPath(prefix + ".parentId").description("대댓글인 경우, 부모 댓글의 id"),
        fieldWithPath(prefix + ".writer").optional()
            .description("작성자의 닉네임 (익명 게시판의 경우 \"익명\", 탈퇴한 작성자일 경우 null)"),
        fieldWithPath(prefix + ".writerId").optional()
            .description("작성자 id (익명 게시판의 경우 -1, 탈퇴한 작성자일 경우 null)"),
        fieldWithPath(prefix + ".writerThumbnailPath").optional().type(String.class)
            .description(
                "작성자의 썸네일 조회 api 경로 (익명 게시판의 경우 빈 문자열 \"\", 탈퇴했을 경우 / 썸네일을 등록하지 않았을 경우 null)")));
    if (descriptors.length > 0) {
      commonFields.addAll(Arrays.asList(descriptors));
    }
    return commonFields;
  }

  public List<FieldDescriptor> generateCommonFollowResponse(ResponseType type, String docSuccess,
      String docCode, String docMsg, FieldDescriptor... descriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(docSuccess, docCode, docMsg));
    commonFields.addAll(Arrays.asList(
        fieldWithPath(prefix + ".followerNumber").description("팔로워 숫자"),
        fieldWithPath(prefix + ".followeeNumber").description("팔로우 숫자")));
    if (descriptors.length > 0) {
      commonFields.addAll(Arrays.asList(descriptors));
    }
    return commonFields;
  }

  public List<ParameterDescriptor> generateCommonPagingParameters(String sizeDescription,
      ParameterDescriptor... descriptors) {
    List<ParameterDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(Arrays.asList(
        parameterWithName("page").optional().description("페이지 번호(default = 0)"),
        parameterWithName("size").optional().description(sizeDescription)
    ));
    if (descriptors.length > 0) {
      commonFields.addAll(Arrays.asList(descriptors));
    }
    return commonFields;
  }

  public List<FieldDescriptor> generateGetGenerationListResponseFields(ResponseType type,
      String success, String code, String msg, FieldDescriptor... addDescriptors) {
    String prefix = type.getReponseFieldPrefix();
    List<FieldDescriptor> commonFields = new ArrayList<>();
    commonFields.addAll(generateCommonResponseFields(success, code, msg));
    commonFields.addAll(List.of(
        fieldWithPath(prefix).description("기수 정보가 중복 없이 오름차순으로 넘겨집니다.")
    ));
    if (addDescriptors.length > 0) {
      commonFields.addAll(Arrays.asList(addDescriptors));
    }
    return commonFields;
  }
}