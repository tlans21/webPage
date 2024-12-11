package HomePage.config;

import java.util.Arrays;
import java.util.List;

public class CacheExcludePatterns {
    public static final List<String> PATTERNS = Arrays.asList(
        "/api/**",
        "/profile/**",
        "/notifications/**",
        "/env",
        "/hc"
    );
}
