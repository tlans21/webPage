package HomePage.service.naverApi;

import HomePage.config.naver.NaverApiMapConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class NaverApiMapService {
    @Autowired
    private NaverApiMapConfig naverApiMapConfig;
    @Autowired
    private HttpRequestService httpRequestService;
    public String searchLocal(String query, String region){
        String text;
        try {
            String combinedQuery = region + ' ' + query;
            text = URLEncoder.encode(combinedQuery, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        }

        String apiURL = "https://openapi.naver.com/v1/search/local.json?query=" + text + "&display=5&start=1&sort=random";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", naverApiMapConfig.getClientId());
        requestHeaders.put("X-Naver-Client-Secret", naverApiMapConfig.getClientSecret());

        return httpRequestService.get(apiURL, requestHeaders);
    }
    public String searchLocalImage(String title, String address){
        String text;
        try {
            String combinedText = removeHtmlTags(title + ' ' + "음식점");
            System.out.println(combinedText);
            text = URLEncoder.encode(combinedText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패", e);
        }
        String apiURL = "https://openapi.naver.com/v1/search/image?query=" + text + "&display=1&start=1&sort=sim&filter=small";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", naverApiMapConfig.getClientId());
        requestHeaders.put("X-Naver-Client-Secret", naverApiMapConfig.getClientSecret());

        return httpRequestService.get(apiURL, requestHeaders);
    }
    private String removeHtmlTags(String input) {
       return Pattern.compile("<[^>]*>").matcher(input).replaceAll("");
    }
}
