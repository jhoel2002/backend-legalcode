package com.application.claimhereweb.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.application.claimhereweb.service.AWSS3Service;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("api/s3")
public class AWSS3Controller {

    @Autowired
    private AWSS3Service awss3Service;

    @PostMapping(value = "/upload")
    public ResponseEntity<String> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam("nombreCarpeta") String nombreCarpeta) {

        awss3Service.uploadFile(file, nombreCarpeta);

        String response = "El archivo " + file.getOriginalFilename() +
                " fue cargado correctamente en la carpeta " + nombreCarpeta + " de S3";
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<String>> listFiles() {
        return new ResponseEntity<>(awss3Service.getObjectFromS3(), HttpStatus.OK);
    }

    @GetMapping(value = "/download")
    public ResponseEntity<Resource> download(
            @RequestParam("nombreCarpeta") String nombreCarpeta,
            @RequestParam("key") String key) {

        InputStreamResource resource = new InputStreamResource(
                awss3Service.downloadFile(nombreCarpeta, key));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                .body(resource);
    }

    @PostMapping(value = "/upload-with-metadata")
    public ResponseEntity<String> uploadFileWithMetadata(
            @RequestPart("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("type_document") String typeDocument) {

        try {
            awss3Service.uploadFileWithMetadata(file, name, typeDocument);
            String response = "El archivo " + file.getOriginalFilename() +
                    " fue cargado correctamente con el nombre lógico '" + name +
                    "' y tipo de documento '" + typeDocument + "'";
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Error al subir el archivo: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}