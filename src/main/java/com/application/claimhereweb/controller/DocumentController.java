package com.application.claimhereweb.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;

import org.springframework.http.HttpStatus;

@RestController
@CrossOrigin(origins = "http://localhost:4200", originPatterns = "*")
@RequestMapping("api/document")
public class DocumentController {
    /*
     * @Autowired
     * private final AmazonS3 amazonS3;
     * 
     * @Value("${cloud.aws.s3.bucket}")
     * private String bucketName;
     * 
     * public DocumentController(AmazonS3 amazonS3) {
     * this.amazonS3 = amazonS3;
     * }
     * 
     * @PostMapping(value = "/upload", consumes =
     * MediaType.MULTIPART_FORM_DATA_VALUE)
     * public ResponseEntity<?> uploadFile(
     * 
     * @RequestParam("file") MultipartFile file,
     * 
     * @RequestParam("name") String name,
     * 
     * @RequestParam("type_document") String typeDocument) {
     * 
     * // Validación de archivo vacío
     * if (file.isEmpty()) {
     * return ResponseEntity.badRequest().body("El archivo no puede estar vacío.");
     * }
     * 
     * // Validación de tipo MIME
     * String tipoMime = file.getContentType();
     * List<String> tiposPermitidos = List.of(
     * "application/pdf",
     * "image/png",
     * "image/jpeg",
     * "application/vnd.openxmlformats-officedocument.wordprocessingml.document", //
     * .docx
     * "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
     * "text/csv");
     * 
     * if (tipoMime == null || !tiposPermitidos.contains(tipoMime)) {
     * return ResponseEntity.badRequest().body("Tipo de archivo no permitido: " +
     * tipoMime);
     * }
     * 
     * try {
     * File mainFile = new File(file.getOriginalFilename());
     * try (FileOutputStream stream = new FileOutputStream(mainFile)) {
     * stream.write(file.getBytes());
     * }
     * 
     * String newFileName = System.currentTimeMillis() + "_" + mainFile.getName();
     * amazonS3.putObject(new PutObjectRequest(bucketName, newFileName, mainFile));
     * 
     * // URL opcional:
     * String fileUrl = amazonS3.getUrl(bucketName, newFileName).toString();
     * 
     * return ResponseEntity.ok(Map.of(
     * "message", "Archivo subido correctamente",
     * "name", name,
     * "type_document", typeDocument,
     * "filename", newFileName,
     * "original_filename", file.getOriginalFilename(),
     * "size", file.getSize(),
     * "url", fileUrl));
     * 
     * } catch (IOException e) {
     * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
     * .body("Error al subir el archivo: " + e.getMessage());
     * }
     * }
     */
}