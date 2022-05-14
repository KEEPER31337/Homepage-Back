package keeper.project.homepage.admin.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class BoanNews {
    public static void main(String[] args) throws IOException {
        final String url = "https://m.boannews.com/html/";
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select("body > dl > dd > section > ul:not(:nth-child(1)) > li");

        Map<String, String> datas = new HashMap<>();
        for(Element element : elements){
            Elements link = element.select("a");
            datas.put(element.text(), url + link.get(0).attr("href"));
        }

        datas.entrySet().forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
    }
}
