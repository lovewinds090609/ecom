package com.ecommerce.project.security.services;

public interface JwtBlackListService {
    void addJwtToBlackList(String jwt);
    boolean isJwtInBlackList(String jwt);
}
