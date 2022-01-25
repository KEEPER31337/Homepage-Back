package keeper.project.homepage;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import keeper.project.homepage.repository.BookRepository;
import keeper.project.homepage.repository.CategoryRepository;
import keeper.project.homepage.repository.CommentRepository;
import keeper.project.homepage.repository.FileRepository;
import keeper.project.homepage.repository.OriginalImageRepository;
import keeper.project.homepage.repository.PostingRepository;
import keeper.project.homepage.repository.ThumbnailRepository;
import keeper.project.homepage.repository.member.MemberRepository;
import keeper.project.homepage.service.MemberHasCommentDislikeService;
import keeper.project.homepage.service.MemberHasCommentLikeService;
import keeper.project.homepage.service.sign.SignUpService;
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
  protected BookRepository bookRepository;

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
  protected OriginalImageRepository originalImageRepository;
  /********* Service Start ********/
  @Autowired
  protected SignUpService signUpService;

  @Autowired
  protected MemberHasCommentLikeService memberHasCommentLikeService;

  @Autowired
  protected MemberHasCommentDislikeService memberHasCommentDislikeService;

  /********* Others Start ********/
  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected PasswordEncoder passwordEncoder;

  @Autowired
  protected WebApplicationContext ctx;

  @Autowired
  protected MessageSource messageSource;

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
