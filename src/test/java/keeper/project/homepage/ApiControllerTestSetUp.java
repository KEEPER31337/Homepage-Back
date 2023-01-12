package keeper.project.homepage;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import keeper.project.homepage.config.security.JwtTokenProvider;
import keeper.project.homepage.about.repository.StaticWriteContentRepository;
import keeper.project.homepage.about.repository.StaticWriteSubtitleImageRepository;
import keeper.project.homepage.about.repository.StaticWriteTitleRepository;
import keeper.project.homepage.library.repository.BookBorrowRepository;
import keeper.project.homepage.attendance.repository.AttendanceRepository;
import keeper.project.homepage.library.repository.BookDepartmentRepository;
import keeper.project.homepage.library.repository.BookRepository;
import keeper.project.homepage.member.repository.FriendRepository;
import keeper.project.homepage.member.repository.MemberHasMemberJobRepository;
import keeper.project.homepage.member.repository.MemberJobRepository;
import keeper.project.homepage.member.repository.MemberRankRepository;
import keeper.project.homepage.member.repository.MemberTypeRepository;
import keeper.project.homepage.member.service.MemberFollowService;
import keeper.project.homepage.point.repository.PointLogRepository;
import keeper.project.homepage.posting.repository.CategoryRepository;
import keeper.project.homepage.posting.repository.CommentRepository;
import keeper.project.homepage.util.exception.ExceptionAdviceUtil;
import keeper.project.homepage.util.repository.FileRepository;
import keeper.project.homepage.posting.repository.PostingRepository;
import keeper.project.homepage.util.repository.ThumbnailRepository;
import keeper.project.homepage.member.repository.MemberRepository;
import keeper.project.homepage.library.service.BookManageService;
import keeper.project.homepage.study.repository.StudyHasMemberRepository;
import keeper.project.homepage.study.repository.StudyRepository;
import keeper.project.homepage.attendance.service.AttendanceService;
import keeper.project.homepage.member.service.MemberDeleteService;
import keeper.project.homepage.member.service.MemberHasCommentDislikeService;
import keeper.project.homepage.member.service.MemberHasCommentLikeService;
import keeper.project.homepage.posting.service.CommentService;
import keeper.project.homepage.sign.service.SignUpService;
import keeper.project.homepage.member.service.MemberService;
import keeper.project.homepage.posting.service.PostingService;
import keeper.project.homepage.study.service.StudyService;
import keeper.project.homepage.util.service.FileService;
import keeper.project.homepage.util.service.ThumbnailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@ExtendWith({RestDocumentationExtension.class})
@SpringBootTest
@AutoConfigureMockMvc
public abstract class ApiControllerTestSetUp {

  /********* Repository Start ********/
  @Autowired
  protected MemberRepository memberRepository;

  @Autowired
  protected MemberJobRepository memberJobRepository;

  @Autowired
  protected MemberHasMemberJobRepository memberHasMemberJobRepository;

  @Autowired
  protected BookRepository bookRepository;

  @Autowired
  protected BookBorrowRepository bookBorrowRepository;

  @Autowired
  protected BookDepartmentRepository bookDepartmentRepository;

  @Autowired
  protected CategoryRepository categoryRepository;

  @Autowired
  protected PostingRepository postingRepository;

  @Autowired
  protected CommentRepository commentRepository;

  @Autowired
  protected FileRepository fileRepository;

  @Autowired
  protected ThumbnailRepository thumbnailRepository;

  @Autowired
  protected FriendRepository friendRepository;

  @Autowired
  protected AttendanceRepository attendanceRepository;

  @Autowired
  protected MemberRankRepository memberRankRepository;

  @Autowired
  protected MemberTypeRepository memberTypeRepository;

  @Autowired
  protected StaticWriteTitleRepository staticWriteTitleRepository;

  @Autowired
  protected StaticWriteSubtitleImageRepository staticWriteSubtitleImageRepository;

  @Autowired
  protected StaticWriteContentRepository staticWriteContentRepository;

  @Autowired
  protected PointLogRepository pointLogRepository;

  @Autowired
  protected StudyRepository studyRepository;

  @Autowired
  protected StudyHasMemberRepository studyHasMemberRepository;

  /********* Service Start ********/
  @Autowired
  protected SignUpService signUpService;

  @Autowired
  protected AttendanceService attendanceService;

  @Autowired
  protected BookManageService bookManageService;

  @Autowired
  protected MemberHasCommentLikeService memberHasCommentLikeService;

  @Autowired
  protected MemberHasCommentDislikeService memberHasCommentDislikeService;

  @Autowired
  protected MemberService memberService;

  @Autowired
  protected MemberFollowService memberFollowService;

  @Autowired
  protected MemberDeleteService memberDeleteService;

  @Autowired
  protected CommentService commentService;

  @Autowired
  protected PostingService postingService;

  @Autowired
  protected StudyService studyService;

  @Autowired
  protected ThumbnailService thumbnailService;

  @Autowired
  protected FileService fileService;

  /********* Others Start ********/
  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected PasswordEncoder passwordEncoder;

  @Autowired
  protected WebApplicationContext ctx;

  @Autowired
  protected MessageSource messageSource;

  @Autowired
  protected ExceptionAdvice exceptionAdvice;

  @Autowired
  protected ExceptionAdviceUtil exceptionUtil;

  @Autowired
  protected JwtTokenProvider jwtTokenProvider;

  @BeforeEach
  public void setUpAll(RestDocumentationContextProvider restDocumentation) throws Exception {
    // mockMvc의 한글 사용을 위한 코드
    this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
        .apply(springSecurity())
        .apply(documentationConfiguration(restDocumentation)
            .operationPreprocessors()
            .withRequestDefaults(modifyUris().host("test.com").removePort(), prettyPrint())
            .withResponseDefaults(prettyPrint())
        )
        .build();
  }
}