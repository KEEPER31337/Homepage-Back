package keeper.project.homepage.controller;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.sql.Date;
import javax.transaction.Transactional;
import keeper.project.homepage.entity.BookEntity;
import keeper.project.homepage.entity.MemberEntity;
import keeper.project.homepage.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
public class BookControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private BookRepository bookRepository;

  @Autowired
  private WebApplicationContext ctx;

  final private String bookTitle = "Do it! 점프 투 파이썬";
  final private String bookAuthor = "박응용";
  final private String bookPicture = "JumpToPython.png";
  final private String bookInformation = "파이썬의 기본이 잘 정리된 책이다.";
  final private Long bookQuantity = 4L;
  final private Long bookBorrow = 0L;
  final private Long bookEnable = bookQuantity;
  final private String bookRegisterDate = "2021-01-16";

  @BeforeEach
  public void setUp() throws Exception {
    // mockMvc의 한글 사용을 위한 코드
    this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
        .addFilters(new CharacterEncodingFilter("UTF-8", true))  // 필터 추가
        .alwaysDo(print())
        .build();

    bookRepository.save(
        BookEntity.builder()
            .title(bookTitle)
            .author(bookAuthor)
            .picture(bookPicture)
            .information(bookInformation)
            .total(bookQuantity)
            .borrow(bookBorrow)
            .enable(bookEnable)
            .registerDate(Date.valueOf(bookRegisterDate))
            .build());
  }

  @Test
  @DisplayName("책 등록 성공")
  public void addBook() throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("title", bookTitle);
    params.add("author", bookAuthor);
    params.add("picture", bookPicture);
    params.add("information", bookInformation);
    params.add("quantity", String.valueOf(bookQuantity));

    mockMvc.perform(post("/v1/addbook").params(params))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.code").value(0))
        .andExpect(jsonPath("$.msg").exists());
  }

}
