package keeper.project.homepage;

import static keeper.project.homepage.user.service.posting.PostingService.INFO_CATEGORY_ID;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import keeper.project.homepage.user.dto.posting.PostingDto;
import keeper.project.homepage.user.service.posting.PostingService;
import keeper.project.homepage.util.service.ThumbnailService.DefaultThumbnailInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableScheduling
@SpringBootApplication
public class KeeperApplication {

  private static ApplicationContext applicationContext;

  public static void main(String[] args) {
    applicationContext = SpringApplication.run(KeeperApplication.class, args);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
  public void autoPosting() throws IOException {
    PostingService postingService = applicationContext.getBean(PostingService.class);

    String url = "https://www.contestkorea.com/sub/list.php?Txt_bcode=030220003";
    Document doc = Jsoup.connect(url).get();

    Elements onlyContent = doc.select("#frm > div > div.list_style_2 > ul > li");

    Map<String, String> datas = new HashMap<>();
    for (Element element : onlyContent) {
      String dDay = element.select("span.day").text();
      if (dDay.length() != 0) {
        datas.put(element.select("span.txt").text(),
            url.substring(0, 33) + element.select("a").attr("href"));
      }
    }

    LocalDateTime now = LocalDateTime.now();
    StringBuilder sb = new StringBuilder();

    Iterator<Entry<String, String>> iter = datas.entrySet().iterator();
    sb.append("ContestKorea\n");
    while (iter.hasNext()) {
      Entry<String, String> entry = iter.next();
      sb.append(entry.getKey());
      sb.append(" => ");
      sb.append(entry.getValue());
      sb.append("\n");
    }
    sb.append("\n");
    sb.append("Linkareer\n");

    Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/chromedriver.exe");
    System.setProperty("webdriver.chrome.driver", path.toString());

    ChromeOptions options = new ChromeOptions();
    options.addArguments("--start-maximized"); // 최대크기
    options.addArguments("--headless"); // Browser를 띄우지 않음
    options.addArguments("--disable-gpu"); // GPU를 사용하지 않음
    options.addArguments("--no-sandbox"); // Sandbox 프로세스를 사용하지 않음

    ChromeDriver driver = new ChromeDriver(options);

    url = "https://linkareer.com/list/activity?filterBy_interestIDs=13&filterType=INTEREST&orderBy_direction=DESC&orderBy_field=CREATED_AT&page=1";
    driver.get(url);

    String html = driver.getPageSource();
    driver.quit();

    doc = Jsoup.parse(html);
    Elements elements = doc.getElementsByClass("MuiGrid-item");

    datas = new HashMap<>();
    for (Element element : elements) {
      datas.put(element.select("h5").text(),
          url.substring(0, 21) + element.select("a").attr("href"));
    }

    iter = datas.entrySet().iterator();
    while (iter.hasNext()) {
      Entry<String, String> entry = iter.next();
      sb.append(entry.getKey());
      sb.append(" => ");
      sb.append(entry.getValue());
      sb.append("\n");
    }
    sb.append("\n");

    PostingDto postingDto = PostingDto.builder().categoryId(INFO_CATEGORY_ID).allowComment(1)
        .commentCount(0).dislikeCount(0).likeCount(0).ipAddress("127.0.0.1").isNotice(0).isSecret(0)
        .isTemp(0).visitCount(0).registerTime(now).updateTime(now)
        .thumbnailId(DefaultThumbnailInfo.ThumbPosting.getThumbnailId())
        .title(now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일자 정보")))
        .content(sb.toString()).build();

    postingService.autoSave(postingDto);
  }
}
