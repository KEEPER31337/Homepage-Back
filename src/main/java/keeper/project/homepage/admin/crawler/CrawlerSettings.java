package keeper.project.homepage.admin.crawler;

import java.util.List;

public class CrawlerSettings {

  public static final List<String> TITLES = List.of("BoanNews", "ItWorld", "ContestKorea",
      "AllForYoung", "ItFind");
  public static final List<String> ELEMENT_QUERYS = List.of(
      "body > dl > dd > section > ul:not(:nth-child(1)) > li > a", "div.node-list > div h5 > a",
      "#frm > div > div.list_style_2 > ul > li",
      "#post_list > div > div.posts__wrapper > div", "tbody > tr > td > a");
  public static final List<String> TITLE_QUERYS = List.of("", "", "span.txt", "", "");
  public static final List<String> LINK_QUERYS = List.of("", "", "a", "a", "");
  public static final List<String> URLS = List.of(
      "https://m.boannews.com/html/", "https://www.itworld.co.kr/t/36/%EB%B3%B4%EC%95%88",
      "https://www.contestkorea.com/sub/list.php?int_gbn=1&Txt_bcode=030510001",
      "https://allforyoung.com/posts/category/1/?fields=14",
      "https://www.itfind.or.kr/data/seminar/list.do?pageSize=10&boardParam1=&searchTarget2=&pageIndex=0");
  public static final List<Integer> URL_ENDS = List.of(URLS.get(0).length(), 25, 33, 23, 24);
}
