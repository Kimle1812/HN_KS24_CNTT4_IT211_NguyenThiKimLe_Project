package org.example.course_management.service.impl;

import com.cloudinary.Cloudinary;
import org.example.course_management.service.CloudStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {
        if(file.getSize() > 15 * 1024 * 1024) throw new RuntimeException("Kích thước tệp vượt quá giới hạn (15MB).");
        try {
            Map<String, Object> options = new HashMap<>();
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
                options.put("resource_type", "raw");
            }
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi đồng bộ Cloudinary: " + e.getMessage());
        }
    }
}

