package keeper.project.homepage.admin.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Linkareer {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/chromedriver.exe");
        System.setProperty("webdriver.chrome.driver", path.toString());

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");          // 최대크기
        options.addArguments("--headless");                 // Browser를 띄우지 않음
        options.addArguments("--disable-gpu");              // GPU를 사용하지 않음, Linux에서 headless를 사용하는 경우 필요함.
        options.addArguments("--no-sandbox");               // Sandbox 프로세스를 사용하지 않음, Linux에서 headless를 사용하는 경우 필요함.

        ChromeDriver driver = new ChromeDriver( options );

        final String url = "https://linkareer.com/list/activity?filterBy_interestIDs=13&filterType=INTEREST&orderBy_direction=DESC&orderBy_field=CREATED_AT&page=1";
        driver.get(url);

        String html = driver.getPageSource();
        driver.quit();

        Document doc = Jsoup.parse(html);
        Elements contents = doc.select("#__next > div.jss5 > div.MuiContainer-root.jss13.jss3 > div.MuiBox-root.jss110.jss1 > div > div.MuiBox-root.jss123.jss117 > div.MuiGrid-root.MuiGrid-container > div > div");

        Map<String, String> datas = new HashMap<>();
        for(Element element : contents) {
            datas.put(element.select("h5").text(), url.substring(0, 21) + element.select("a").attr("href"));
        }

        datas.entrySet().forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
    }
}
