package HomePage.config.oauth;

import HomePage.config.auth.PrincipalDetails;
import HomePage.config.oauth.provider.FacebookUserInfo;
import HomePage.config.oauth.provider.GoogleUserInfo;
import HomePage.config.oauth.provider.NaverUserInfo;
import HomePage.config.oauth.provider.OAuth2UserInfo;
import HomePage.domain.model.User;
import HomePage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
    public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;



    // 구글로 부터 후처리되는 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("getClientRegistration : " + userRequest.getClientRegistration());
        System.out.println("getAccessToken : " + userRequest.getAccessToken().getTokenValue());


        // userRequest 정보 -> loadUser 함수 호출 -> 구글로 부터 회원 프로필을 받는다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")){
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }else{
            System.out.println("지원하지 않는 로그인");
        }

        System.out.println("getAttributes : " + oAuth2User.getAttributes());
        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId; // google_103432.....
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";

        Optional<User> userEntityOptional = userRepository.findByUsername(username);
        User userEntity;

        if (!userEntityOptional.isPresent()){
            System.out.println("oAuth2 로그인이 최초입니다.");
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();
            userRepository.save(userEntity);
        }else{
            userEntity =  userEntityOptional.get();
            System.out.println("oAuth2 로그인");
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
