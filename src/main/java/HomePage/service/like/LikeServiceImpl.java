package HomePage.service.like;

import HomePage.domain.model.entity.Like;
import HomePage.domain.model.enumData.LikeTargetType;
import HomePage.exception.AlreadyExistsException;
import HomePage.exception.InvalidDataException;
import HomePage.mapper.LikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;

@Service
public class LikeServiceImpl implements LikeService{
    @Autowired
    private LikeMapper likeMapper;

    @Override
    public Like save(Like like) {
        try{
            boolean isExist = likeMapper.exists(like.getTargetType(), like.getTargetId(), like.getUserId());
            if (isExist){
                throw new AlreadyExistsException("이미 좋아요를 누르셨습니다.");
            }
            Like savedLike = likeMapper.insert(like);
            return savedLike;
        } catch (DataAccessException e){
            throw new InvalidDataException("잘못된 대상이나 사용자입니다.");
        }
    }

    @Override
    public void addLikeToBoard(Long boardId, Long userId) {
        if (boardId == null || userId == null) {
            throw new InvalidParameterException("게시글 ID와 사용자 ID는 필수값입니다.");
        }

        Like like = Like.createLike(LikeTargetType.BOARD, boardId, userId);
        save(like);
    }

    @Override
    public void addLikeToComment(Long commentId, Long userId) {
        if (commentId == null || userId == null) {
            throw new InvalidParameterException("코멘트 ID와 사용자 ID는 필수값입니다.");
        }
        Like like = Like.createLike(LikeTargetType.COMMENT, commentId, userId);
        save(like);
    }

    @Override
    public void addLikeToRestaurant(Long restaurantId, Long userId) {
        if (restaurantId == null || userId == null) {
            throw new InvalidParameterException("식당 ID와 사용자 ID는 필수값입니다.");
        }
        Like like = Like.createLike(LikeTargetType.RESTAURANT, restaurantId, userId);
        save(like);
    }

    @Override
    public void addLikeToReview(Long reviewId, Long userId) {
        if (reviewId == null || userId == null) {
            throw new InvalidParameterException("리뷰 ID와 사용자 ID는 필수값입니다.");
        }
        Like like = Like.createLike(LikeTargetType.REVIEW, reviewId, userId);
        save(like);
    }
}
