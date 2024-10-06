package HomePage.domain.model.dto;

import java.sql.Timestamp;

public class CommunityCommentDTO {
    private Long id;
    private Long board_id;
    private String writer;
    private String content;
    private Timestamp registerDate;
    private Timestamp updateDate;
    private Timestamp deleteDate;

    private BoardInfo relatedBoard;

    public static class BoardInfo{
        private Long id;
        private String title;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBoard_id() {
        return board_id;
    }

    public void setBoard_id(Long board_id) {
        this.board_id = board_id;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Timestamp registerDate) {
        this.registerDate = registerDate;
    }

    public Timestamp getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Timestamp updateDate) {
        this.updateDate = updateDate;
    }

    public Timestamp getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Timestamp deleteDate) {
        this.deleteDate = deleteDate;
    }

    public BoardInfo getRelatedBoard() {
        return relatedBoard;
    }

    public void setRelatedBoard(BoardInfo relatedBoard) {
        this.relatedBoard = relatedBoard;
    }
}
