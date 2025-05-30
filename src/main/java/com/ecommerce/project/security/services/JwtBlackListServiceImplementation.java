package com.ecommerce.project.security.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class JwtBlackListServiceImplementation implements JwtBlackListService {
    @Value("${spring.app.jwtExpirationMs}")
    private int jwtExpiration;

    private final RedisTemplate<String, String> redisTemplate;

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:"; // 黑名單前綴

    /** 將jwt加入黑名單 */
    public void addJwtToBlackList(String jwt) {
        String key = BLACKLIST_PREFIX + jwt;
        Duration expirationDuration  = Duration.ofMillis(jwtExpiration); // 設置存活時間
        // 將jwt加入黑名單並設置存活時間，當過了存活時間就會自動被Redis自動清除
        redisTemplate.opsForValue().set(key, "true", expirationDuration);
    }

    /** 檢查jwt是否在黑名單中 */
    public boolean isJwtInBlackList(String jwt) {
        String key = BLACKLIST_PREFIX + jwt;
        return redisTemplate.opsForValue().get(key) != null;
    }
}
