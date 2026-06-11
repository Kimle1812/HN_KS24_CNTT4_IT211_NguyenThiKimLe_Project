package org.example.course_management.service.impl;

import com.cloudinary.Cloudinary;
import org.example.course_management.service.CloudStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) {
        if(file.getSize() > 15 * 1024 * 1024) throw new RuntimeException("File size limit cap exceeded (15MB).");
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Cloud synchronization fault: " + e.getMessage());
        }
    }
}

