package HomePage.filter;


import jakarta.servlet.*;

import java.io.IOException;

public class Filter2 implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request, response);
    }
}

