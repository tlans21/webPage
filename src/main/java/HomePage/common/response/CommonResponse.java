package HomePage.common.response;

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
    private Status status;
    private T data;
    private String message;
    private int code;    // HTTP 상태 코드

    public static <T> CommonResponse<T> success(T data) {
       return CommonResponse.<T>builder()
               .status(Status.SUCCESS)
               .data(data)
               .code(HttpStatus.OK.value())
               .build();
    }

    public static <T> CommonResponse<T> success(T data, String message) {
       return CommonResponse.<T>builder()
               .status(Status.SUCCESS)
               .data(data)
               .message(message)
               .code(HttpStatus.OK.value())
               .build();
    }

    public static <T> CommonResponse<T> error(String message, HttpStatus status) {
       return CommonResponse.<T>builder()
               .status(Status.ERROR)
               .message(message)
               .code(status.value())
               .build();
    }
}
