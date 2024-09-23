package HomePage.domain.model;

import java.util.List;

public class UserActivityDTO {
    private List<CommunityBoard> recentCommunityBoards;
    private List<CommunityCommentDTO> recentCommunityComments; // DTO 사용 이유 : 댓글에 연관된 게시글을 불러오기 위함이다. 데이터베이스 컬럼과 1대1 매칭을 할 수 없고 유연한 데이터 통신을 위해 사용
    public UserActivityDTO(List<CommunityBoard> recentCommunityBoards, List<CommunityCommentDTO> recentCommunityComments) {
        this.recentCommunityBoards = recentCommunityBoards;
        this.recentCommunityComments = recentCommunityComments;
    }

    public List<CommunityBoard> getRecentCommunityBoards() {
        return recentCommunityBoards;
    }

    public void setRecentCommunityBoards(List<CommunityBoard> recentCommunityBoards) {
        this.recentCommunityBoards = recentCommunityBoards;
    }

    public List<CommunityCommentDTO> getRecentCommunityComments() {
        return recentCommunityComments;
    }

    public void setRecentCommunityComments(List<CommunityCommentDTO> recentCommunityComments) {
        this.recentCommunityComments = recentCommunityComments;
    }
}
