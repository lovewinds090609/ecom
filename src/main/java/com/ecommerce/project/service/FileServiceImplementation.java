package com.ecommerce.project.service;

import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import io.micrometer.observation.ObservationFilter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

/**
 * 實作檔案服務
 */
@Service
public class FileServiceImplementation implements FileService {
    /**
     * 上傳照片
     * @param path 照片放置的路徑
     * @param file 上傳的照片
     * @return 隨機的檔案名稱
     * @throws IOException 當檔案有錯誤時拋出exception
     */
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        //當前fileName
        String originalFileName = file.getOriginalFilename();
        //生成特定的fileName,避免檔名重複覆蓋
        String randomId = UUID.randomUUID().toString();
        // test.jpg and UUID = 1234 >> 1234.jpg
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf(".")));
        String filepath = path + File.separator + fileName;
        //檢查路徑是否存在或著創建
        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdirs();
        }
        //上傳到Server
        Files.copy(file.getInputStream(), Paths.get(filepath));
        return fileName;
    }
}
