package HomePage.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
    private Status status;
    private T data;
    private String message;
    private int code;


    public static <T> CommonResponse<T> success(T data){
        return CommonResponse.<T>builder()
                .status(Status.SUCCESS)
                .data(data)
                .build();
    }
    public static <T> CommonResponse<T> success(T data, String message) {
        return CommonResponse.<T>builder()
                .status(Status.SUCCESS)
                .data(data)
                .message(message)
                .build();
    }
    public static <T> CommonResponse<T> error(String message) {
       return CommonResponse.<T>builder()
               .status(Status.ERROR)
               .message(message)
               .build();
    }
}
