package HomePage.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class Filter3 implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        System.out.println(req.getMethod());
        if (req.getMethod().equals("GET")){
            System.out.println("POST 요청됨");
            String headerAuth = req.getHeader("Authorization");
            System.out.println(headerAuth);
            if (headerAuth.equals("cos")){
                chain.doFilter(req, res);
            } else {
              PrintWriter out = res.getWriter();
              out.println("인증 안됨");
            }
        }
        chain.doFilter(req, res);
    }
}
