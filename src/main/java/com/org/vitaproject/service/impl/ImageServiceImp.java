package com.org.vitaproject.service.impl;

import com.org.vitaproject.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageServiceImp implements ImageService {
    /*
    private final ImageRepo imageRepo;

    public String uploadImage(MultipartFile file) throws IOException {
        ImageDataEntity imageData = imageRepo.save(ImageDataEntity.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes())).build());
        if (imageData != null) {
            return "file uploaded successfully : " + file.getOriginalFilename();
        }
        return null;
    }


    public byte[] downloadImage(String fileName) {
        Optional<ImageDataEntity> dbImageData = imageRepo.findByName(fileName);
        byte[] images = ImageUtils.decompressImage(dbImageData.get().getImageData());
        return images;
    }

     */
}
