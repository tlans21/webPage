//package HomePage.service.like;
//
//import HomePage.domain.model.dto.LikeRes;
//import HomePage.domain.model.entity.Like;
//import HomePage.domain.model.enumData.LikeTargetType;
//import HomePage.mapper.LikeMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class LikeServiceImpl implements LikeService{
//    @Autowired
//    private LikeMapper likeMapper;
//
//    @Override
//    public LikeRes toggleLike(LikeTargetType targetType, Long targetId, Long userId) {
//        boolean exists = likeMapper.exists(targetType.getCode(), targetId, userId);
//        if (!exists) {
//            Like like = Like.createLike(targetType, targetId, userId);
//            likeMapper.insert(like);
//            updateLikeCount(targetType, targetId, 1);
//        } else {
//            likeMapper.delete(targetType.getCode(), targetId, userId);
//            updateLikeCount(targetType, targetId, -1);
//        }
//
//        int likeCount = getLikeCount(targetType, targetId);
//          return new LikeRes(!exists, likeCount);
//    }
//    private void updateLikeCount(LikeTargetType targetType, Long targetId, int delta) {
//        String key = getLikeCountKey(targetType, targetId);
//        redisTemplate.opsForValue().increment(key, delta);
//    }
//
//    @Override
//    public int getLikeCount(LikeTargetType targetType, Long targetId) {
//        return 0;
//    }
//
//    private String getLikeCountKey(LikeTargetType targetType, Long targetId) {
//        return String.format("like:%s:%d", targetType.getCode(), targetId);
//    }
//}
