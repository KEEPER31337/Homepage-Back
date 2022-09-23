package keeper.project.homepage.crawler;

import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@AllArgsConstructor
public class KeeperCrawler {

  private final List<String> titles;
  private final List<String> elementQueries;
  private final List<String> titleQueries;
  private final List<String> linkQueries;
  private final List<String> urls;
  private final List<Integer> urlEnds;
  private final List<String> existUrls;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < urls.size(); i++) {
      Document doc;
      try {
        doc = Jsoup.connect(urls.get(i)).get();
      } catch (IOException e) {
        sb.append(String.format("Error : url %s is invalid", urls.get(i)));
        sb.append("- - -\n");
        continue;
      }

      Elements elements = doc.select(elementQueries.get(i));
      sb.append("### " + titles.get(i) + "\n\n");
      boolean hasNew = false;
      for (Element element : elements) {
        String title = titleQueries.get(i).equals("") ? element.text()
            : element.select(titleQueries.get(i)).text();
        String link = urls.get(i).substring(0, urlEnds.get(i));
        link += linkQueries.get(i).equals("") ? element.attr("href")
            : element.select(linkQueries.get(i)).get(0).attr("href");
        if (existUrls.contains(link)) {
          continue;
        }
        hasNew = true;
        sb.append("**");
        sb.append(title);
        sb.append("**");
        sb.append(" -> [링크](");
        sb.append(link);
        sb.append(")\n");
      }
      if (!hasNew) {
        sb.append("전의 정보와 비교해 새로운 정보가 없습니다.\n");
      }
      sb.append("- - -\n");
    }

    return sb.toString();
  }
}
