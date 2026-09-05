package com.asada.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio de almacenamiento de imágenes en disco local.
 *
 * Es el proveedor de imágenes por defecto (desarrollo local, o cuando no
 * se configura explícitamente Firebase). Se activa cuando
 * {@code asada.storage.provider} es "local" o no está definida.
 *
 * ADVERTENCIA para Render: el sistema de archivos de un servicio web ahí
 * es efímero -- lo guardado aquí se pierde en cada reinicio o despliegue.
 * Para persistir imágenes en producción, usa {@link FirebaseStorageService}
 * configurando {@code asada.storage.provider=firebase}.
 */
@Service
@ConditionalOnProperty(name = "asada.storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements ImageStorageService {

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
    @Override
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

    /**
     * Guarda un archivo de cualquier tipo bajo un nombre único (evita
     * choques cuando varios archivos se suben para el mismo registro).
     */
    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }

        String fileName = java.util.UUID.randomUUID().toString().substring(0, 8) + extension;

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
