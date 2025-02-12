package HomePage.service;

import HomePage.domain.model.dto.CommunityCommentDTO;
import HomePage.domain.model.entity.CommunityBoard;
import HomePage.domain.model.entity.CommunityComment;
import HomePage.mapper.CommunityCommentMapper;
import HomePage.repository.BoardRepository;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@NoArgsConstructor
public class CommunityCommentService implements CommentService<CommunityComment> {

    @Autowired
    private CommunityCommentMapper commentMapper;
    @Autowired
    private BoardRepository<CommunityBoard> boardRepository;



    public int getCommentCntById(Long boardId){
        return commentMapper.countByBoardId(boardId);
    }

    @Override
    @Transactional
    public void saveCommentAndIncrementCommentCnt(CommunityComment comment) {
        saveComment(comment);
        int commentCnt = countByBoardId(comment.getBoardId());
        incrementCommentCnt(comment.getBoardId(), commentCnt);
    }

    @Override
    public void saveComment(CommunityComment comment){
        comment.setRegisterDate(new Timestamp(System.currentTimeMillis()));
        commentMapper.save(comment);
    }
    @Override
    public int countByBoardId(Long BoardId){
        return commentMapper.countByBoardId(BoardId);
    }
    @Override
    public boolean incrementCommentCnt(Long id, int commentCnt){
        return boardRepository.updateCommentCnt(id, commentCnt);
    }




    @Override
    @Transactional
    public void updateComment(CommunityComment comment) {
        if (commentMapper.update(comment) <= 0) {
            throw new RuntimeException("Failed to update board with id: " + comment.getId());
        }
    }

    @Override
    @Transactional
    public CommunityComment getCommentByCommentId(Long commentId) {
        return commentMapper.findCommentById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentByCommentId(Long id) {
        CommunityComment comment = getCommentByCommentId(id);
        if (commentMapper.deleteByCommentId(comment.getId()) <= 0) {
            throw new RuntimeException("Failed to delete board with comment_id: " + id);
        }
    }

    @Override
    @Transactional
    public void deleteCommentsByBoardId(Long id) {
        if(commentMapper.deleteByBoardId(id) <= 0){
            throw new RuntimeException("Failed to delete board with board_id: " + id);
        }
    }

    @Override
    @Transactional
    public void deleteCommentByWriter(String writer) {
        if (commentMapper.deleteByWriter(writer) <= 0){
            throw new RuntimeException("Failed to delete board with writer: " + writer);
        }
    }

    @Override
    public List<CommunityCommentDTO> getCommentByBoardId(Long boardId) {
        List<CommunityCommentDTO> communityCommentDTOS = commentMapper.selectByBoardId(boardId);
        return communityCommentDTOS;
    }

    @Override
    public List<CommunityCommentDTO> getCommentByBoardId(Long boardId, Long userId) {
        List<CommunityCommentDTO> communityCommentDTOS = commentMapper.selectByBoardId(boardId, userId);
        return communityCommentDTOS;
    }

    @Override
    public List<CommunityCommentDTO> getAllComments() {
        return commentMapper.selectAll();
    }
}
