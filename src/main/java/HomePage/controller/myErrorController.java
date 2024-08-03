package HomePage.controller;


import HomePage.exception.JoinException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class myErrorController implements ErrorController{

    public String getErrorPath() {
        return "/error";
    }
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // 오류 처리 로직
        return "error"; // 오류 페이지 뷰 이름
    }

    @GetMapping("/api/test1")
    public RedirectView test(){
        throw new JoinException("11");
    }
}
