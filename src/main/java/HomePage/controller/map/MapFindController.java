package HomePage.controller.map;

import HomePage.service.naverApi.NaverApiMapService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class MapFindController {
    @Autowired
    private NaverApiMapService naverApiMapService;
    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping("/map")
    public String showMap(){
        return "map/map";
    }
    
    @GetMapping("/map/search")
    public String searchLocal(@RequestParam String query){

        List<String> regions = Arrays.asList("서울", "경기", "인천", "강원", "충청", "대전", "세종", "전라", "광주", "경상", "대구", "부산", "제주");

        for (String region : regions){
            System.out.println("지역 :" + region);
            String result = naverApiMapService.searchLocal(query, region);

            try {
               // ObjectMapper 설정
               objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

               // JSON 파싱 및 예쁘게 출력
               Object json = objectMapper.readValue(result, Object.class);
               String prettyJson = objectMapper.writeValueAsString(json);
               System.out.println(prettyJson);
           } catch (Exception e) {
               e.printStackTrace();
           }
        }

        return "map/map";
    }
}
