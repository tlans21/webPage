package HomePage.service.like;

import HomePage.domain.model.entity.Like;
import HomePage.domain.model.enumData.LikeTargetType;
import HomePage.mapper.LikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    LikeMapper likeMapper;

    @Override
    @Transactional
    public Map<String, Integer> toggleLike(Long reviewId, Long userId, boolean isLike) {
        String targetType = LikeTargetType.REVIEW.getCode();
        System.out.println("test1");

        // 현재 사용자의 좋아요/싫어요 상태 확인
        Like existingLike = likeMapper.findByTargetAndUser(targetType, reviewId, userId);
        System.out.println("test2");
        System.out.println("existingLike: " + existingLike);  // null인지 확인

        try {
            if (existingLike != null) {
                System.out.println("이미 좋아요/싫어요가 존재함");
                if (existingLike.isLike() == isLike) {
                    System.out.println("같은 타입 삭제 시도");
                    // 같은 타입(좋아요->좋아요 or 싫어요->싫어요)이면 삭제
                    int affectedRows = likeMapper.delete(targetType, reviewId, userId);
                    System.out.println("삭제된 행 수: " + affectedRows);
                    // 삭제 후 카운트 갱신
                    int affectedRowsToReviewCount = likeMapper.updateReviewCounts(existingLike);
                    if (affectedRowsToReviewCount == 0) {
                        throw new RuntimeException("좋아요 삭제에 대한 카운트 갱신을 실패했습니다");
                    }
                } else {
                    System.out.println("다른 타입 업데이트 시도");
                    // 다른 타입(좋아요->싫어요 or 싫어요->좋아요)이면 업데이트
                    existingLike.setLike(isLike);
                    int affectedRows = likeMapper.update(existingLike);

                    System.out.println("업데이트된 행 수: " + affectedRows);
                    if (affectedRows == 0) {
                        throw new RuntimeException("좋아요 업데이트에 실패했습니다.");
                    }

                    // 업데이트 후 카운트 갱신
                    int affectedRowsToReviewCount = likeMapper.updateReviewCounts(existingLike);
                    if (affectedRowsToReviewCount == 0) {
                        throw new RuntimeException("좋아요 업데이트에 대한 카운트 갱신을 실패했습니다");
                    }
                }
            } else {
                System.out.println("새로운 좋아요/싫어요 생성 시도");
                // 없으면 새로 생성
                Like like = Like.createLike(LikeTargetType.REVIEW, reviewId, userId, isLike);

                int affectedRows = likeMapper.insert(like);
                System.out.println("생성된 행 수: " + affectedRows);
                if (affectedRows == 0) {
                    throw new RuntimeException("좋아요 생성에 실패했씁니다.");
                }
                int affectedRowsToReviewCount = likeMapper.updateReviewCounts(like);
                if (affectedRowsToReviewCount == 0) {
                    throw new RuntimeException("좋아요 생성에 대한 카운트 갱신을 실패했습니다");
                }
            }

            // 카운트 조회
            int likeCount = likeMapper.countLikes(targetType, reviewId, true);
            int dislikeCount = likeMapper.countLikes(targetType, reviewId, false);
            System.out.println("최종 카운트 - 좋아요: " + likeCount + ", 싫어요: " + dislikeCount);

            Map<String, Integer> counts = new HashMap<>();
            counts.put("likeCount", likeCount);
            counts.put("dislikeCount", dislikeCount);

            return counts;
        } catch (Exception e) {
            System.out.println("에러 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;

        }
    }
}
