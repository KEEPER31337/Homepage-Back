package keeper.project.homepage.admin.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ItWorld1 {
    public static void main(String[] args) throws IOException {
        final String url = "https://www.itworld.co.kr/t/36/%EB%B3%B4%EC%95%88";
        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.getElementsByClass("flex-nowrap");

        Map<String, String> datas = new HashMap<>();
        for(Element element : elements){
            Elements elems = element.select("h5 > a");
            datas.put(elems.text(), url.substring(0,25) + elems.attr("href"));
        }

        datas.entrySet().forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
    }
}
