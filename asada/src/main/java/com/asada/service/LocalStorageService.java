package com.asada.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio de almacenamiento de imágenes en disco local.
 *
 * Durante la Entrega 1 y 2 las imágenes de usuarios y abonados se guardan
 * en una carpeta local del servidor. En la Entrega 3 este servicio será
 * reemplazado por uno equivalente que suba las imágenes a Firebase
 * Storage, manteniendo la misma firma de método para minimizar el
 * impacto en los controladores y servicios que lo utilizan.
 */
@Service
public class LocalStorageService {

    @Value("${asada.upload.dir:uploads}")
    private String uploadDir;

    private Path uploadRoot;

    @PostConstruct
    public void init() {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear la carpeta de imágenes: " + uploadRoot, e);
        }
    }

    /**
     * Guarda la imagen recibida en {@code uploads/<folder>/img<id><extensión>}
     * y devuelve la ruta pública (relativa) con la que se puede mostrar en
     * las vistas, por ejemplo {@code /uploads/abonado/img12.jpg}.
     */
    public String uploadImage(MultipartFile file, String folder, Integer id) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }

        String fileName = "img" + id + extension;

        Path folderPath = uploadRoot.resolve(folder);
        Files.createDirectories(folderPath);

        Path target = folderPath.resolve(fileName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + folder + "/" + fileName;
    }

    public Path getUploadRoot() {
        return uploadRoot;
    }

}
