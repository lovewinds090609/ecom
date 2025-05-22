package com.ecommerce.project.config;


import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * App相關設定
 */
@Configuration
public class AppConfig {
    /**
     * 在web啟動時就先初始化一個ModelMapper
     * 讓各個service共用同一個ModelMapper,避免浪費記憶體
     * @return ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
