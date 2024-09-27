package HomePage.service.user;

import HomePage.domain.model.*;
import HomePage.repository.BoardRepository;
import HomePage.repository.CommentRepository;
import HomePage.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserMyPageService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository<CommunityBoard> boardRepository;
    @Autowired
    private CommentRepository<CommunityComment> commentRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public UserMyPageService(UserRepository userRepository, BoardRepository<CommunityBoard> boardRepository) {
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    public ProfileDTO getUserProfile(Long userId){
        User foundUser = userRepository.findById(userId).get();
        ProfileDTO profile = new ProfileDTO(foundUser.getId(), foundUser.getUsername(), foundUser.getNickname(), foundUser.getEmail(), foundUser.getCreateDate(), foundUser.getRoles());
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

        // entity -> dto 변환
        List<CommunityBoardDTO> recentCommunityBoardDTOs = recentCommunityBoards.stream()
            .map(recentCommunityBoard -> modelMapper.map(recentCommunityBoard, CommunityBoardDTO.class))
            .collect(Collectors.toList());

        // entity -> dto 변환 + 댓글 하나당 연관된 게시글 찾기
        List<CommunityCommentDTO> recentCommunityCommentDTOs = recentCommunityComments.stream()
                .map(recentCommunityComment -> {
                    CommunityCommentDTO communityCommentDTO = modelMapper.map(recentCommunityComment, CommunityCommentDTO.class);
                    CommunityBoard relatedBoard = boardRepository.selectById(recentCommunityComment.getBoard_id()).get();
                    communityCommentDTO.setRelatedBoard(modelMapper.map(relatedBoard, CommunityCommentDTO.BoardInfo.class));
                    return communityCommentDTO;
                })
                .collect(Collectors.toList());

        UserActivityDTO userActivity = new UserActivityDTO(recentCommunityBoardDTOs, recentCommunityCommentDTOs);

        return userActivity;
    }

    public Object getUserStats(Long id) {
        return null;
    }
}
