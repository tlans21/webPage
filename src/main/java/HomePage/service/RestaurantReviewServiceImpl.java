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


    @Override
    @Transactional
    public Long save(RestaurantReviewCommentDTO commentDTO) {
        RestaurantReviewComment comment = objectMapper.convertValue(commentDTO, RestaurantReviewComment.class);
        comment.setRegisterDate(new Timestamp(System.currentTimeMillis()));
        comment.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        Long savedId = reviewCommentMapper.save(comment); // 댓글 저장
        RestaurantReviewCommentDTO foundReview = findById(savedId); // 저장된 review id값을 통해 reviewDto객체 불러옴

        int reviewCnt = countRestaurantReviewByRestaurantId(foundReview.getRestaurantId()); // 식당 id값과 관련된 리뷰 개수 불러옴
        restaurantService.updateCountRestaurantReview(foundReview.getRestaurantId(), reviewCnt);
        return savedId;
    }

    @Override
    public RestaurantReviewCommentDTO findById(Long id) {
        RestaurantReviewComment comment = reviewCommentMapper.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return objectMapper.convertValue(comment, RestaurantReviewCommentDTO.class);
    }

    @Override
    @Transactional
    public List<RestaurantReviewCommentDTO> findByRestaurantId(Long restaurantId) {
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

    public RestaurantReviewCommentDTO createReview(Long restaurantId, Long userId, String content){
        RestaurantReviewCommentDTO restaurantReviewCommentDTO = new RestaurantReviewCommentDTO();
        restaurantReviewCommentDTO.setRestaurantId(restaurantId);
        restaurantReviewCommentDTO.setContent(content);
        restaurantReviewCommentDTO.setUserId(userId);
        Long savedReviewId = save(restaurantReviewCommentDTO);
        if (savedReviewId == null) {
            throw new RuntimeException("Failed to save review");
        }

        return findById(savedReviewId);
    }
    private int countRestaurantReviewByRestaurantId(Long restaurantId){
        return reviewCommentMapper.countByRestaurantId(restaurantId);
    }
}
