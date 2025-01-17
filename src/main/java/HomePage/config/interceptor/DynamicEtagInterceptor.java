package HomePage.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class DynamicEtagInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // If-None-Match 확인 및 ETag 검증
        if (handler instanceof HandlerMethod) {
           HandlerMethod handlerMethod = (HandlerMethod) handler;
           DynamicETag annotation = handlerMethod.getMethodAnnotation(DynamicETag.class);

           if (annotation != null) {
               String ifNoneMatch = request.getHeader("If-None-Match");

               if (ifNoneMatch != null) {
                   // 현재 인증 상태 확인
                   Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                   boolean isAuthenticated = (authentication != null &&
                                           authentication.isAuthenticated() &&
                                           !(authentication.getPrincipal() instanceof String));

                   // 현재 상태로 ETag 생성
                   StringBuilder builder = new StringBuilder();
                   builder.append("auth:").append(isAuthenticated);

                   String currentETag = "";
                   try {
                       MessageDigest md = MessageDigest.getInstance("MD5");
                       byte[] hashBytes = md.digest(builder.toString().getBytes());
                       StringBuilder hexString = new StringBuilder();
                       for (byte hashByte : hashBytes) {
                           String hex = Integer.toHexString(0xff & hashByte);
                           if (hex.length() == 1) hexString.append('0');
                           hexString.append(hex);
                       }
                       currentETag = "\"" + hexString.toString() + "\"";
                   } catch (NoSuchAlgorithmException e) {
                       currentETag = "\"" + builder.toString() + "\"";
                   }

                   // If-None-Match와 현재 생성한 ETag 비교
                   if (ifNoneMatch.equals(currentETag)) {
                       response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                       return false;  // 컨트롤러 실행하지 않음
                   }
               }
           }
       }
       return true;  // 계속 진행
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            DynamicETag annotation = handlerMethod.getMethodAnnotation(DynamicETag.class);

            if (annotation != null && modelAndView != null) {
                String etag = generateETag(modelAndView.getModel(), annotation.properties());
                response.setHeader("ETag", etag);
            }
        }
    }

    private String generateETag(Map<String, Object> model, String[] properties) {
       StringBuilder builder = new StringBuilder();

       Object authStatus = model.get("authentication");
       if (authStatus instanceof Boolean) {
           builder.append("auth:").append(authStatus);
       }

       // properties가 지정되어 있으면 해당 속성만 사용
       if (properties.length > 0) {
           for (String property : properties) {
               Object value = findPropertyValue(model, property);
               if (value != null) {
                   builder.append(property).append(":").append(value).append(";");
               }
           }
       } else {
           // properties가 지정되지 않았으면 모든 모델 속성 사용
           for (Map.Entry<String, Object> entry : model.entrySet()) {
               builder.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
           }
       }
       // MD5 해시 생성
       return DigestUtils.md5Hex(builder.toString());
    }
    private Object findPropertyValue(Map<String, Object> model, String property) {
        // property가 중첩된 경우 (예: post.likeCount)
        String[] parts = property.split("\\.");
        Object current = model;

        for (String part : parts) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(part);
            } else if (current != null) {
                try {
                    // 객체의 getter 메소드를 통해 속성값 획득
                    Method getter = current.getClass().getMethod("get" + StringUtils.capitalize(part));
                    current = getter.invoke(current);
                } catch (Exception e) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return current;
    }
}
