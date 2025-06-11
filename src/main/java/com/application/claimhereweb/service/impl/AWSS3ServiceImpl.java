package com.application.claimhereweb.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.application.claimhereweb.service.AWSS3Service;

@Service
public class AWSS3ServiceImpl implements AWSS3Service {

    private static final Logger logger = LoggerFactory.getLogger(AWSS3ServiceImpl.class);

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    public void uploadFile(MultipartFile file, String nombreCarpeta) {

        File mainFile = new File(file.getOriginalFilename());
        try (FileOutputStream stream = new FileOutputStream(mainFile)) {
            stream.write(file.getBytes());

            String newFileName = System.currentTimeMillis() + "_" + mainFile.getName();
            // esto evitara que los archivos se sobre escriban

            String s3Key = nombreCarpeta + "/" + newFileName;
            // nombre de carpeta + el nombre del archivo

            logger.info("Subiendo archivo con el nombre ... " + s3Key);

            PutObjectRequest request = new PutObjectRequest(bucketName, s3Key, mainFile);
            amazonS3.putObject(request);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public List<String> getObjectFromS3() {
        ListObjectsV2Result result = amazonS3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        List<String> list = objects.stream().map(item -> {
            return item.getKey();
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public InputStream downloadFile(String nombreCarpeta, String key) {
        // Construir la clave con carpeta y nombre del archivo
        String fullKey = (nombreCarpeta.endsWith("/") ? nombreCarpeta : nombreCarpeta + "/") + key;

        S3Object object = amazonS3.getObject(bucketName, fullKey);
        return object.getObjectContent();
    }

    @Override
    public void uploadFileWithMetadata(MultipartFile file, String name, String typeDocument) {
        String tipoMime = file.getContentType();

        List<String> tiposPermitidos = List.of(
                "application/pdf",
                "image/png",
                "image/jpeg",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
                "text/csv");

        if (tipoMime == null || !tiposPermitidos.contains(tipoMime)) {
            throw new IllegalArgumentException("Tipo de archivo no permitido: " + tipoMime);
        }

        File mainFile = new File(file.getOriginalFilename());
        try (FileOutputStream stream = new FileOutputStream(mainFile)) {
            stream.write(file.getBytes());

            String newFileName = System.currentTimeMillis() + "_" + mainFile.getName();
            String s3Key = "documents/" + newFileName; // todo se sube a la carpeta 'documents/'

            logger.info("Subiendo archivo con nombre lógico: " + name);
            logger.info("Tipo declarado por el usuario: " + typeDocument);
            logger.info("MIME type detectado: " + tipoMime);
            logger.info("Subiendo a S3 con key: " + s3Key);

            PutObjectRequest request = new PutObjectRequest(bucketName, s3Key, mainFile);
            amazonS3.putObject(request);

        } catch (IOException e) {
            logger.error("Error al subir archivo: " + e.getMessage(), e);
            throw new RuntimeException("Fallo al subir el archivo a S3", e);
        }
    }
}
