package com.application.claimhereweb.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Service {

    void uploadFile(MultipartFile file, String nombreCarpeta);

    void uploadFileWithMetadata(MultipartFile file, String name, String typeDocument);

    List<String> getObjectFromS3();

    InputStream downloadFile(String nombreCarpeta, String key);
}
