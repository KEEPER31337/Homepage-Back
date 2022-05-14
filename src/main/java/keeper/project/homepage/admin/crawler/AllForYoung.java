package keeper.project.homepage.admin.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AllForYoung {
    public static void main(String[] args) throws IOException {
        final String url = "https://allforyoung.com/posts/category/1/?fields=14";
        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.select("#post_list > div > div.posts__wrapper > div");

        Map<String, String> datas = new HashMap<>();
        for(Element element : elements){
            Elements link = element.select("a");
            datas.put(element.select("p.info__name").text(), url.substring(0,23) + link.get(0).attr("href"));
        }

        datas.entrySet().forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
    }
}
