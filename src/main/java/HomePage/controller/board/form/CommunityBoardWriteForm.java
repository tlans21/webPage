package HomePage.controller.board.form;

import jakarta.validation.constraints.NotBlank;

import java.util.List;


public class CommunityBoardWriteForm {
    @NotBlank(message = "제목은 필수 입력 값 입니다.")
    private String title;
    @NotBlank(message = "내용은 필수 입력 값 입니다.")
    private String content;

    private List<String> tag;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }
}
