package HomePage.config.jwt;

import HomePage.config.auth.PrincipalDetails;
import HomePage.config.jwt.handler.UserLoginSuccessHandler;
import HomePage.config.jwt.provider.TokenProvider;
import HomePage.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;


public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private UserLoginSuccessHandler userLoginSuccessHandler;
    @Autowired
    TokenProvider tokenProvider;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, UserLoginSuccessHandler userLoginSuccessHandler){
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.userLoginSuccessHandler = userLoginSuccessHandler;
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthentication : 로그인 시도중");

        try {
            BufferedReader br = request.getReader();
            String input = null;
            String data = "";
            while((input = br.readLine()) != null) {
                data += input;
            }
            System.out.println(data);
            String parsedUsername = null;
            String parsedPassword = null;
            // parse
            String[] pairs = data.split("&");
            for (String pair : pairs){
                String[] keyValue = pair.split("=");
                if (keyValue[0].equals("username")) {
                    parsedUsername = keyValue[1];
                } else if (keyValue[0].equals("password")) {
                    parsedPassword = keyValue[1];
                }
            }
            System.out.println("Username: " + parsedUsername);
            System.out.println("Password: " + parsedPassword);

            // json 방식

//            ObjectMapper objectMapper = new ObjectMapper();
//            User user = objectMapper.readValue(request.getInputStream(), User.class);
//            System.out.println(user);

//            User user = userRepository.findByUsername(parsedUsername).get();
//
//            System.out.println(user.getUsername());
//            System.out.println(user.getRoles());
            // 토큰 생성

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(parsedUsername, parsedPassword);
            // 토큰을 이용하여 로그인 정보를 얻는다.
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            // authentication 객체가 session 영역에 저장되고, 즉 로그인이 되었다는 뜻임.

            System.out.println(principalDetails.getUser().getUsername());
            System.out.println(principalDetails.getUser().getPassword());
            System.out.println("================================================");
            return authentication;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 정상적으로 attemptAuthentication이 동작하면 successfulAuthentication이 동작
    // JWT 토큰을 만들어 응답을 해주면 됨.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication이 실행됨");
        userLoginSuccessHandler.onAuthenticationSuccess(request, response, authResult);
    }
}
