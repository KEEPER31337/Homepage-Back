package keeper.project.homepage.crawler;

import static keeper.project.homepage.crawler.CrawlerSettings.ELEMENT_QUERIES;
import static keeper.project.homepage.crawler.CrawlerSettings.LINK_QUERIES;
import static keeper.project.homepage.crawler.CrawlerSettings.TITLES;
import static keeper.project.homepage.crawler.CrawlerSettings.TITLE_QUERIES;
import static keeper.project.homepage.crawler.CrawlerSettings.URLS;
import static keeper.project.homepage.crawler.CrawlerSettings.URL_ENDS;
import static keeper.project.homepage.posting.service.PostingService.INFO_CATEGORY_ID;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import keeper.project.homepage.posting.entity.PostingEntity;
import keeper.project.homepage.posting.dto.PostingDto;
import keeper.project.homepage.posting.service.PostingService;
import keeper.project.homepage.util.service.ThumbnailService.DefaultThumbnailInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class ScheduledTasks {

  private final PostingService postingService;

  // 3일마다 한 번씩 실행
  @Scheduled(cron = "0 0 0 */3 * *", zone = "Asia/Seoul")
  public void autoPosting() {

    LocalDateTime now = LocalDateTime.now();
    PostingEntity postingEntity = postingService.getRecentPostingByCategoryId(INFO_CATEGORY_ID);
    List<String> existUrls = List.of(postingEntity.getContent().split("\n"));
    existUrls = existUrls.stream().filter(elem -> elem.contains("https"))
        .map(elem -> elem.substring(elem.lastIndexOf("(") + 1, elem.lastIndexOf(")")))
        .collect(Collectors.toList());
    KeeperCrawler keeperCrawler = new KeeperCrawler(TITLES, ELEMENT_QUERIES, TITLE_QUERIES,
        LINK_QUERIES, URLS, URL_ENDS, existUrls);

    PostingDto postingDto = PostingDto.builder().categoryId(INFO_CATEGORY_ID).allowComment(1)
        .commentCount(0).dislikeCount(0).likeCount(0).ipAddress("127.0.0.1").isNotice(0).isSecret(0)
        .isTemp(0).visitCount(0).registerTime(now).updateTime(now)
        .thumbnailId(DefaultThumbnailInfo.ThumbPosting.getThumbnailId())
        .title(now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일자 정보")
            .withLocale(Locale.forLanguageTag("ko"))))
        .content(keeperCrawler.toString()).build();

    postingService.autoSave(postingDto);
  }
}
