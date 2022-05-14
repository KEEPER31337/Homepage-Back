package keeper.project.homepage.admin.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ItFind {
    public static void main(String[] args) throws IOException {
        final String url = "https://www.itfind.or.kr/data/seminar/list.do?pageSize=10&boardParam1=&searchTarget2=&pageIndex=0";
        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.select("tbody > tr > td > a");

        Map<String, String> datas = new HashMap<>();
        for(Element element : elements){
            datas.put(element.text(), url.substring(0,24) + element.attr("href"));
        }

        datas.entrySet().forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
    }
}
