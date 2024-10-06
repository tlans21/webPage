package HomePage.config.naver;

import HomePage.domain.model.dto.SearchImageReq;
import HomePage.domain.model.dto.SearchImageRes;
import HomePage.domain.model.dto.SearchLocalReq;
import HomePage.domain.model.dto.SearchLocalRes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class NaverClient {
    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverSecret;

    @Value("${naver.url.search.local}")
    private String naverLocalSearchUrl;

    @Value("${naver.url.search.image}")
    private String naverImageSearchUrl;
    public SearchLocalRes searchLocal(SearchLocalReq searchLocalReq) {
        var uri = UriComponentsBuilder
                .fromUriString(naverLocalSearchUrl)
                .queryParams(searchLocalReq.toMultiValueMap())
                .build()
                .encode()
                .toUri();

        var headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", naverClientId);
        headers.set("X-Naver-Client-Secret", naverSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        var httpEntity = new HttpEntity<>(headers);
        var responseType = new ParameterizedTypeReference<SearchLocalRes>(){};


        var responseEntity = new RestTemplate()
            .exchange(
                    uri,
                    HttpMethod.GET,
                    httpEntity,
                    responseType
            );

        return responseEntity.getBody();
    }
    public SearchImageRes searchImage(SearchImageReq searchImageReq) {
        var uri = UriComponentsBuilder
                    .fromUriString(naverImageSearchUrl)
                    .queryParams(searchImageReq.toMultiValueMap())
                    .build()
                    .encode()
                    .toUri();
        var headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", naverClientId);
        headers.set("X-Naver-Client-Secret", naverSecret);
        headers.setContentType(MediaType.APPLICATION_JSON);

        var httpEntity = new HttpEntity<>(headers);

        // 응답 타입 
        var responseType = new ParameterizedTypeReference<SearchImageRes>(){};

        // RestTemplate()로 네이버 API JSON 응답을 searchLocalRes에 자동 매핑을 한다.
        var responseEntity = new RestTemplate()
                .exchange(
                        uri,
                        HttpMethod.GET,
                        httpEntity,
                        responseType
                );

        return responseEntity.getBody();
    }
}
