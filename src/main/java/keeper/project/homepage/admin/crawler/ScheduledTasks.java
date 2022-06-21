package keeper.project.homepage.admin.crawler;

import static keeper.project.homepage.admin.crawler.CrawlerSettings.ELEMENT_QUERIES;
import static keeper.project.homepage.admin.crawler.CrawlerSettings.LINK_QUERIES;
import static keeper.project.homepage.admin.crawler.CrawlerSettings.TITLES;
import static keeper.project.homepage.admin.crawler.CrawlerSettings.TITLE_QUERIES;
import static keeper.project.homepage.admin.crawler.CrawlerSettings.URLS;
import static keeper.project.homepage.admin.crawler.CrawlerSettings.URL_ENDS;
import static keeper.project.homepage.user.service.posting.PostingService.INFO_CATEGORY_ID;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import keeper.project.homepage.user.dto.posting.PostingDto;
import keeper.project.homepage.user.service.posting.PostingService;
import keeper.project.homepage.util.service.ThumbnailService.DefaultThumbnailInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class ScheduledTasks {

  private final PostingService postingService;

  // 3일마다 한 번씩 실행
  @Scheduled(cron = "0 0 */3 * * *", zone = "Asia/Seoul")
  public void autoPosting() {

    LocalDateTime now = LocalDateTime.now();
    KeeperCrawler keeperCrawler = new KeeperCrawler(TITLES, ELEMENT_QUERIES, TITLE_QUERIES,
        LINK_QUERIES, URLS, URL_ENDS);

    PostingDto postingDto = PostingDto.builder().categoryId(INFO_CATEGORY_ID).allowComment(1)
        .commentCount(0).dislikeCount(0).likeCount(0).ipAddress("127.0.0.1").isNotice(0).isSecret(0)
        .isTemp(0).visitCount(0).registerTime(now).updateTime(now)
        .thumbnailId(DefaultThumbnailInfo.ThumbPosting.getThumbnailId())
        .title(now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일자 정보")))
        .content(keeperCrawler.toString()).build();

    postingService.autoSave(postingDto);
  }
}
