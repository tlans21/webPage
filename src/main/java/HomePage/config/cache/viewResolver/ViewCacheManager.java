package HomePage.config.cache.viewResolver;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class ViewCacheManager {
    private final Cache<String, String> pageCache = Caffeine.newBuilder()
        .maximumSize(1000)                // 최대 캐시 크기
        .expireAfterWrite(30, TimeUnit.MINUTES)  // 30분 후 만료
        .recordStats()                    // 캐시 통계 기록
        .build();

    public Optional<String> getCache(String key) {
        return Optional.ofNullable(pageCache.getIfPresent(key));
    }

    public void putCache(String key, String content) {
        pageCache.put(key, content);
    }

    // 캐시 통계 확인을 위한 메서드
    public String getCacheStats() {
        return pageCache.stats().toString();
    }

    // 특정 캐시 삭제
    public void invalidateCache(String key) {
        pageCache.invalidate(key);
    }
}
