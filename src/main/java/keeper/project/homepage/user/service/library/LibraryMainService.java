package keeper.project.homepage.user.service.library;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import keeper.project.homepage.entity.library.BookEntity;
import keeper.project.homepage.repository.library.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.Exceptions;

@Service
@RequiredArgsConstructor
public class LibraryMainService {

  private final BookRepository bookRepository;
  private ObjectMapper objectMapper;


  public List<BookEntity> displayTenBooks(Pageable pageable) {

    List<BookEntity> bookEntityPage = bookRepository.findAll(pageable).getContent();

    return bookEntityPage;
  }

  public List<BookEntity> searchBooks(String keyword, Pageable pageable) {
    List<BookEntity> bookEntitiesTitle = bookRepository.findByTitleContaining(keyword, pageable);
    List<BookEntity> bookEntitiesAuthor = bookRepository.findByAuthorContaining(keyword, pageable);
    List<BookEntity> bookEntitiesInformation = bookRepository.findByInformationContaining(keyword,
        pageable);

    Set<BookEntity> bookEntitySet = new HashSet<>(bookEntitiesTitle);
    bookEntitySet.addAll(bookEntitiesAuthor);
    bookEntitySet.addAll(bookEntitiesInformation);

    List<BookEntity> bookEntities = new ArrayList<>(bookEntitySet);

    return bookEntities;
  }

  public BookEntity selectedBook(String title, String author) {
    return bookRepository.findByTitleAndAuthor(title, author).get();
  }

  public void sendBorrowMessage(String title, String author, Long quantity)
      throws Exception {

    String apiKey = "0e34aed4f97818cc672325fe03160328";
    String code = getLoginCode();

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(
        new MediaType("application", "x-www-form-urlencoded", Charset.forName("UTF-8")));

    Map<String, Object> map = new HashMap<>();
    map.put("grant_type", "authorization_code");
    map.put("client_id", apiKey);
    map.put("redirect_uri", "https://localhost:8080/v1/selectedbook/borrow");
    map.put("code", code);

    String params = objectMapper.writeValueAsString(map);

    HttpEntity httpEntity = new HttpEntity<>(params, httpHeaders);

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> responseEntity = restTemplate.exchange(
        "https://kauth.kakao.com/oauth/authorize", HttpMethod.GET, httpEntity, String.class);

    System.out.println(responseEntity.getStatusCode());
    System.out.println(responseEntity.getBody());

  }

  private String getLoginCode() throws Exception{
      HttpClient httpClient = HttpClient.newBuilder().version(Version.HTTP_1_1).build();
      String result = httpClient.sendAsync(
          HttpRequest.newBuilder(
              new URI(
                  "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=0e34aed4f97818cc672325fe03160328&redirect_uri=https://localhost:8080/v1/selectedbook/borrow")
          ).GET().build(),
          HttpResponse.BodyHandlers.ofString()
      ).thenApply(HttpResponse::body).get();
    System.out.println(result);

    return result;
  }
}
