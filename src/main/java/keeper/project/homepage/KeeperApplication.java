package keeper.project.homepage;

import static keeper.project.homepage.admin.crawler.CrawlerSettings.ELEMENT_QUERYS;
import static keeper.project.homepage.admin.crawler.CrawlerSettings.LINK_QUERYS;
import static keeper.project.homepage.admin.crawler.CrawlerSettings.TITLES;
import static keeper.project.homepage.admin.crawler.CrawlerSettings.TITLE_QUERYS;
import static keeper.project.homepage.admin.crawler.CrawlerSettings.URLS;
import static keeper.project.homepage.admin.crawler.CrawlerSettings.URL_ENDS;
import static keeper.project.homepage.user.service.posting.PostingService.INFO_CATEGORY_ID;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import keeper.project.homepage.admin.crawler.KeeperCrawler;
import keeper.project.homepage.user.dto.posting.PostingDto;
import keeper.project.homepage.user.service.posting.PostingService;
import keeper.project.homepage.util.service.ThumbnailService.DefaultThumbnailInfo;
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

  // 3일마다 한 번씩 실행
  @Scheduled(cron = "0 0 */3 * * *", zone = "Asia/Seoul")
  public void autoPosting() {
    PostingService postingService = applicationContext.getBean(PostingService.class);

    LocalDateTime now = LocalDateTime.now();
    KeeperCrawler keeperCrawler = new KeeperCrawler(TITLES, ELEMENT_QUERYS, TITLE_QUERYS,
        LINK_QUERYS, URLS, URL_ENDS);

    PostingDto postingDto = PostingDto.builder().categoryId(INFO_CATEGORY_ID).allowComment(1)
        .commentCount(0).dislikeCount(0).likeCount(0).ipAddress("127.0.0.1").isNotice(0).isSecret(0)
        .isTemp(0).visitCount(0).registerTime(now).updateTime(now)
        .thumbnailId(DefaultThumbnailInfo.ThumbPosting.getThumbnailId())
        .title(now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일자 정보")))
        .content(keeperCrawler.toString()).build();

    postingService.autoSave(postingDto);
  }
}
