package HomePage.service;

import HomePage.domain.model.dto.RestaurantReviewCommentDTO;
import HomePage.domain.model.entity.RestaurantReviewComment;
import HomePage.domain.model.entity.User;
import HomePage.mapper.RestaurantReviewCommentMapper;
import HomePage.repository.UserRepository;
import HomePage.service.restaurant.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
@Service
public class RestaurantReviewServiceImpl implements RestaurantReviewService{

    private final Map<Long, AtomicInteger> restaurantAnonymousCounterMap = new ConcurrentHashMap<>();

    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private RestaurantReviewCommentMapper reviewCommentMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    // 리뷰  전용 메서드
    private Long saveReview(RestaurantReviewCommentDTO commentDTO) {
        RestaurantReviewComment reviewComment = objectMapper.convertValue(commentDTO, RestaurantReviewComment.class);
        reviewComment.setRegisterDate(new Timestamp(System.currentTimeMillis()));
        reviewComment.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        reviewCommentMapper.save(reviewComment);
        return reviewComment.getId();

   }

    // 식당 통계 전용 메서드
    private void updateRestaurantStatistics(Long restaurantId) {
        int reviewCnt = countRestaurantReviewByRestaurantId(restaurantId);
//        updateRatingByRestaurantId(restaurantId, rating); // restaurantId에 해당하는 리뷰의 평점을 업데이트한다.


        // 총 평점 계산
        List<RestaurantReviewCommentDTO> reviewDTOs = findByRestaurantIdWithoutJoin(restaurantId);
        double totalRating = reviewDTOs.stream()
                .mapToDouble(RestaurantReviewCommentDTO::getRating)
                .sum();
        // 평균 평점 계산
        double averageRating = reviewCnt > 0 ? totalRating/ reviewCnt : 0.0;

        restaurantService.updateCountRestaurantReview(restaurantId, reviewCnt); // restaurantId를 통해서 식당 서비스를 이용하여 식당 리뷰 갯수를 업데이트한다.
        restaurantService.updateAverageRating(restaurantId, averageRating); // restaurantId에 해당하는 식당에 평균 평점을 업데이트한다.
    }


    @Override
    @Transactional
    public Long save(RestaurantReviewCommentDTO commentDTO) {
        Long savedId = saveReview(commentDTO);
        updateRestaurantStatistics(commentDTO.getRestaurantId());
        return savedId;
    }

    @Override
    public RestaurantReviewCommentDTO findById(Long id) {
        RestaurantReviewComment comment = reviewCommentMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return objectMapper.convertValue(comment, RestaurantReviewCommentDTO.class);
    }

    public RestaurantReviewCommentDTO createReview(Long restaurantId, Long userId, String content, double rating){
        RestaurantReviewCommentDTO restaurantReviewCommentDTO = new RestaurantReviewCommentDTO();
        restaurantReviewCommentDTO.setRestaurantId(restaurantId);
        restaurantReviewCommentDTO.setContent(content);
        restaurantReviewCommentDTO.setUserId(userId);
        restaurantReviewCommentDTO.setRating(rating);
        Long savedReviewId = save(restaurantReviewCommentDTO);
        System.out.println("저장된 리뷰 아이디 : " + savedReviewId);
        if (savedReviewId == null) {
            throw new RuntimeException("Failed to save review");
        }

        return findById(savedReviewId);
    }

    @Override
    @Transactional
    public List<RestaurantReviewCommentDTO> findByRestaurantIdWithoutJoin(Long restaurantId) {
        List<RestaurantReviewComment> comments = reviewCommentMapper.findByRestaurantId(restaurantId);

        return comments.stream()
                .map(comment -> {
                    RestaurantReviewCommentDTO dto = objectMapper.convertValue(comment, RestaurantReviewCommentDTO.class);
                    Long userId = dto.getUserId();
                    Optional<User> optionalUser = userRepository.findById(userId);
                    if (optionalUser.isPresent()) {
                        String userNickName = optionalUser.get().getNickname();
                        if (userNickName != null && !userNickName.isEmpty()){ // 닉네임을 설정했다면
                            dto.setWriter(userNickName);
                        } else {
                            String anonymousUserNickName = "익명 사용자";
                            dto.setWriter(anonymousUserNickName);
                        }
                    } else {
                        // 존재하지않음.
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<RestaurantReviewCommentDTO> findByRestaurantIdWithJoin(Long restaurantId, Long userId) {
        List<RestaurantReviewCommentDTO> restaurantReviewCommentDTOS = reviewCommentMapper.findByRestaurantIdWithJoin(restaurantId, userId);
        return restaurantReviewCommentDTOS;
    }

    private String getOrCreateAnonymousNickname(Long userId, Map<Long, String> anonymousNicknameMap, AtomicInteger counter) {
        return anonymousNicknameMap.computeIfAbsent(userId, k -> "익명" + counter.incrementAndGet());
    }

    @Override
    @Transactional
    public void update(RestaurantReviewCommentDTO commentDTO) {
        RestaurantReviewComment comment = reviewCommentMapper.findById(commentDTO.getId())
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setContent(commentDTO.getContent());
        comment.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        reviewCommentMapper.save(comment);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        RestaurantReviewCommentDTO reviewDto = findById(id);// 리뷰 조회하기
        Long restaurantId = reviewDto.getRestaurantId();
        reviewCommentMapper.deleteById(id); // 리뷰 삭제
        int reviewCnt = countRestaurantReviewByRestaurantId(restaurantId); // 삭제후 리뷰 갯수 세기.
        restaurantService.updateCountRestaurantReview(restaurantId, reviewCnt); // 리뷰 갯수 업데이트
    }

    // 비즈니스 로직

//    public Boolean updateRatingByRestaurantId(Long restaurantId, double rating){
//        return reviewCommentMapper.updateRatingByRestaurantId(restaurantId, rating);
//    }



    private int countRestaurantReviewByRestaurantId(Long restaurantId){
        return reviewCommentMapper.countByRestaurantId(restaurantId);
    }


}
