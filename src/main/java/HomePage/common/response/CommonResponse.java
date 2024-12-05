package HomePage.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
    @Schema(description = "상태 코드", example = "200")
    private int statusCode;

    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다")
    private String message;

    @Schema(description = "응답 데이터")
    private T payload;

    public static <T> CommonResponse<T> success(T payload, String message, HttpStatus status) {
        return new CommonResponse<>(status.value(), message, payload);
    }

    public static <T> CommonResponse<T> success(){
        return new CommonResponse<>(Result.OK.getCode(), Result.OK.getMessage(), null);
    }

    public static <T> CommonResponse<T> error(T payload, String message, HttpStatus status) {
        return new CommonResponse<>(status.value(), message, payload);
    }
    public static <T> CommonResponse<T> error(String message, HttpStatus status) {
        return new CommonResponse<>(status.value(), message, null);
    }
}
