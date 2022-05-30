package keeper.project.homepage.admin.crawler;

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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < urls.size(); i++) {
      Document doc;
      try {
        doc = Jsoup.connect(urls.get(i)).get();
      } catch (IOException e) {
        return String.format("Error : url %s is invalid", urls.get(i));
      }

      Elements elements = doc.select(elementQueries.get(i));
      sb.append("==" + titles.get(i) + "==\n\n");
      for (Element element : elements) {
        sb.append(titleQueries.get(i).equals("") ? element.text()
            : element.select(titleQueries.get(i)).text());
        sb.append(" => ");
        sb.append(urls.get(i), 0, urlEnds.get(i));
        sb.append(linkQueries.get(i).equals("") ? element.attr("href")
            : element.select(linkQueries.get(i)).get(0).attr("href"));
        sb.append("\n");
      }
      sb.append("\n\n");
    }

    return sb.toString();
  }
}
