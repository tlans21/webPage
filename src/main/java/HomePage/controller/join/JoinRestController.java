package HomePage.controller.join;

import HomePage.common.response.CommonResponse;
import HomePage.domain.model.dto.JoinCheckUsernameRequest;
import HomePage.domain.model.entity.User;
import HomePage.exception.JoinRequestProcessingTimeoutException;
import HomePage.exception.UsernameAlreadyExistsException;
import HomePage.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@RestController
@RequestMapping("/api")
public class JoinRestController implements JoinRestApiDocs{
    @Autowired
    UserService userService;
    @Override
    @PostMapping("/check-username")
    public CommonResponse<Map<String, Boolean>> checkUsername(
            @RequestBody @Valid JoinCheckUsernameRequest request){
        try{
            String username = request.getUsername(); // 요청으로부터 Username 얻어오기
            Optional<User> OptionalUser = userService.findByUsername(username);
            if (OptionalUser.isPresent()){
                throw new UsernameAlreadyExistsException("이미 사용중인 Username입니다.");
            }
            else {
                Map<String, Boolean> successResponse = new HashMap<>();
                successResponse.put("success", true);
                return CommonResponse.success(successResponse, "사용 가능한 Username입니다", HttpStatus.OK);
            }
        } catch (UsernameAlreadyExistsException e){
            Map<String, Boolean> failResponse = new HashMap<>();
            failResponse.put("success", false);
            return CommonResponse.error(failResponse, e.getMessage(), HttpStatus.CONFLICT); // 409 에러, 중복이 발생함
        }catch(JoinRequestProcessingTimeoutException e){
            Map<String, Boolean> errorResponse = new HashMap<>();
            errorResponse.put("success", false);

            return CommonResponse.error(errorResponse, "요청 처리 시간이 초과되었습니다.", HttpStatus.REQUEST_TIMEOUT); // 408 충돌 에러
        } catch (Exception e){
            Map<String, Boolean> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            return CommonResponse.error(errorResponse, "서버로 부터 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
        }
   }
}
