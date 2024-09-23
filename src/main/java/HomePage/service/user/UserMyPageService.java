package HomePage.service.user;

import HomePage.domain.model.*;
import HomePage.repository.BoardRepository;
import HomePage.repository.CommentRepository;
import HomePage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserMyPageService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository<CommunityBoard> boardRepository;
    @Autowired
    private CommentRepository<CommunityComment> commentRepository;


    @Autowired
    public UserMyPageService(UserRepository userRepository, BoardRepository<CommunityBoard> boardRepository) {
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;

    }

    public ProfileDTO getUserProfile(Long userId){
        User foundUser = userRepository.findById(userId).get();
        ProfileDTO profile = new ProfileDTO(foundUser.getId(), foundUser.getUsername(), foundUser.getEmail(), foundUser.getCreateDate(), foundUser.getRoles());
        return profile;
    }

    public UserActivityDTO getUserActivities(Long userId) {
        //게시판 활동
        User foundUser = userRepository.findById(userId).get();
        int limit = 5; // 최근 보여줄 갯수를 5개로 설정
        // username으로 게시판들 찾기
        List<CommunityBoard> recentCommunityBoards = boardRepository.selectRecentByWriter(foundUser.getUsername(), limit);
        // 쓴 댓글 조회
        List<CommunityComment> recentCommunityComments = commentRepository.selectRecentByWriter(foundUser.getUsername(), limit);
        List<CommunityCommentDTO> recentCommunityCommentDTOs = new ArrayList<>();
        for (CommunityComment comment : recentCommunityComments){
            CommunityBoard communityBoard = boardRepository.selectById(comment.getBoard_id()).get();
            CommunityCommentDTO communityCommentDTO = new CommunityCommentDTO();

            // entity -> DTO 변환, +  관련 게시글 주입
            communityCommentDTO.setId(comment.getId());
            communityCommentDTO.setContent(comment.getContent());
            communityCommentDTO.setRegisterDate(comment.getRegisterDate());
            communityCommentDTO.setWriter(comment.getWriter());
            communityCommentDTO.setBoard_id(comment.getBoard_id());
            communityCommentDTO.setRelatedCommunityBoard(communityBoard);
            communityCommentDTO.setDeleteDate(comment.getRegisterDate());
            communityCommentDTO.setUpdateDate(comment.getUpdateDate());

            recentCommunityCommentDTOs.add(communityCommentDTO);

            // 이제 communityCommentDTO에 담아야함.
        }
        UserActivityDTO userActivity = new UserActivityDTO(recentCommunityBoards, recentCommunityCommentDTOs);

        return userActivity;
    }

    public Object getUserStats(Long id) {
        return null;
    }
}
