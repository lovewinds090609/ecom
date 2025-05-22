package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 檔案服務
 */
public interface FileService {
    String uploadImage(String path, MultipartFile file) throws IOException;
}
