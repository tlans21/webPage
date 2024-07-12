package HomePage.config;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class MyHttpServletResponseWrapper extends HttpServletResponseWrapper {
    String jwtToken;
    public MyHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }
    public void setJwtToken(String jwtToken) {
//        if ("Authorization".equals(name)) {
//           super.setHeader("Authorization", value);
//           super.setHeader("X-Authorization", value); // 추가 헤더 (옵션)
//        } else {
//           super.setHeader(name, value);
//        }
        this.jwtToken = jwtToken;
    }

    @Override
    public void addHeader(String name, String value) {
        if ("Authorization".equalsIgnoreCase(name)) {
           super.addHeader(name, "Bearer " + jwtToken); // 추가적인 로직을 수행할 수도 있습니다.
       } else {
           super.addHeader(name, value);
       }
    }
}
