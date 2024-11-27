//package HomePage.service.redis;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.concurrent.TimeUnit;
//
//@Service
//@RequiredArgsConstructor
//public class RedisService {
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    // 값 저장
//    public void setValue(String key, String value) {
//        redisTemplate.opsForValue().set(key, value);
//    }
//
//    // 값 저장 (만료시간 설정)
//    public void setValueWithExpiration(String key, String value, long timeout, TimeUnit unit) {
//        redisTemplate.opsForValue().set(key, value, timeout, unit);
//    }
//
//    // 값 조회
//    public String getValue(String key) {
//        return (String) redisTemplate.opsForValue().get(key);
//    }
//
//    // 값 삭제
//    public void deleteValue(String key) {
//        redisTemplate.delete(key);
//    }
//
//    // 해시 값 저장
//    public void setHash(String key, String hashKey, String value) {
//        redisTemplate.opsForHash().put(key, hashKey, value);
//    }
//
//    // 해시 값 조회
//    public String getHash(String key, String hashKey) {
//        return (String) redisTemplate.opsForHash().get(key, hashKey);
//    }
//}를
