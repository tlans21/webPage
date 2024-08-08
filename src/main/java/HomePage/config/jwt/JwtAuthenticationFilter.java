package HomePage.config.jwt;

import HomePage.config.auth.PrincipalDetails;
import HomePage.config.jwt.handler.UserLoginSuccessHandler;
import HomePage.config.jwt.provider.TokenProvider;
import HomePage.exception.CustomBadCredentialsException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
        setFilterProcessesUrl("/api/login"); // 로그인 처리 URL
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        System.out.println("JwtAuthentication : 로그인 시도중");
        try {
//            BufferedReader br = request.getReader();
//            String input = null;
//            String data = "";
//            while ((input = br.readLine()) != null) {
//                data += input;
//            }
//            System.out.println(data);
//            String parsedUsername = null;
//            String parsedPassword = null;
//            // parse
//            String[] pairs = data.split("&");
//            for (String pair : pairs) {
//                String[] keyValue = pair.split("=");
//                if (keyValue[0].equals("username")) {
//                    parsedUsername = keyValue[1];
//                } else if (keyValue[0].equals("password")) {
//                    parsedPassword = keyValue[1];
//                }
//            }
//            System.out.println("Username: " + parsedUsername);
//            System.out.println("Password: " + parsedPassword);
            String parsedUsername = request.getParameter("username");
            String parsedPassword = request.getParameter("password");
            System.out.println(parsedUsername);
            System.out.println(parsedPassword);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(parsedUsername, parsedPassword);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            System.out.println(principalDetails.getUser().getUsername());
            System.out.println(principalDetails.getUser().getPassword());
            System.out.println("================================================");
            return authentication;
        }catch (AuthenticationException authenticationException) {
            // IOException 처리
            try{
                unsuccessfulAuthentication(request, response, new CustomBadCredentialsException());
                System.out.println("next");
            }catch (IOException e2){
                System.out.println("next1");
            }catch (ServletException e3){
                System.out.println("next2");
            }
        }
        return null;
    }

    // 정상적으로 attemptAuthentication이 동작하면 successfulAuthentication이 동작
    // JWT 토큰을 만들어 응답을 해주면 됨.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication이 실행됨");
        userLoginSuccessHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("unsuccessfulAuthentication");
        SecurityContextHolder.clearContext();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Authentication Failed: " + failed.getMessage());
        logger.warn("Authentication failed for request: " + request.getRequestURI(), failed);
    }
}
