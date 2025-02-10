package HomePage.controller;

import HomePage.config.auth.PrincipalDetails;
import HomePage.domain.model.entity.User;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationRestController {
   @GetMapping("/api/auth/status")
   public ResponseEntity<String> getAuthStatus(Authentication authentication) {
       boolean isAuthenticated = false;

       if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
           PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
           User user = principalDetails.getUser();
           if (user != null) {
               isAuthenticated = true;
           }
       }
       System.out.println("isAuthenticated" + isAuthenticated);

       return ResponseEntity.ok()
           .cacheControl(CacheControl.noCache())
           .body(isAuthenticated ? "authenticated" : "anonymous");
   }
}