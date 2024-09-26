package HomePage.domain.model;

import java.util.List;

public class UserActivityDTO {
    private List<CommunityBoardDTO> recentCommunityBoardDTOs;
    private List<CommunityCommentDTO> recentCommunityCommentDTOs; // DTO 사용 이유 : 댓글에 연관된 게시글을 불러오기 위함이다. 데이터베이스 컬럼과 1대1 매칭을 할 수 없고 유연한 데이터 통신을 위해 사용
    public UserActivityDTO(List<CommunityBoardDTO> recentCommunityBoardDTOs, List<CommunityCommentDTO> recentCommunityCommentDTOs) {
        this.recentCommunityBoardDTOs = recentCommunityBoardDTOs;
        this.recentCommunityCommentDTOs = recentCommunityCommentDTOs;
    }

    public List<CommunityBoardDTO> getRecentCommunityBoardDTOs() {
        return recentCommunityBoardDTOs;
    }

    public void setRecentCommunityBoardDTOs(List<CommunityBoardDTO> recentCommunityBoardDTOs) {
        this.recentCommunityBoardDTOs = recentCommunityBoardDTOs;
    }

    public List<CommunityCommentDTO> getRecentCommunityCommentDTOs() {
        return recentCommunityCommentDTOs;
    }

    public void setRecentCommunityCommentDTOs(List<CommunityCommentDTO> recentCommunityCommentDTOs) {
        this.recentCommunityCommentDTOs = recentCommunityCommentDTOs;
    }
}
