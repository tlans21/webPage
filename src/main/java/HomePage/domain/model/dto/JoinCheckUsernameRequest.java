package HomePage.domain.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinCheckUsernameRequest {
    @Schema(
            description = "확인하고자 하는 사용자명",
            example = "testUser",
            minLength = 3,
            maxLength = 13,
            required = true
    )
    @NotBlank(message = "사용자 명은 필수 입력 값입니다.")
    String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
