package com.asada.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio de almacenamiento de imágenes en Firebase Storage (que por
 * debajo es un bucket de Google Cloud Storage). Se activa configurando:
 *
 *   asada.storage.provider=firebase
 *   asada.firebase.bucket-name=(tu-bucket).appspot.com
 *
 * y, para las credenciales de la cuenta de servicio, UNA de estas dos
 * opciones:
 *
 *   asada.firebase.credentials-json=(contenido completo del .json)
 *   asada.firebase.credentials-path=(ruta a un archivo .json montado)
 *
 * (la primera es más simple en Render: se pega el contenido del JSON de
 * la cuenta de servicio directo como variable de entorno; la segunda es
 * útil si prefieres usar un "Secret File" de Render).
 *
 * Mientras esta propiedad no esté en "firebase", el proveedor activo
 * sigue siendo LocalStorageService (disco local), así que este servicio
 * no interfiere con el desarrollo local hasta que se active a propósito.
 */
@Service
@ConditionalOnProperty(name = "asada.storage.provider", havingValue = "firebase")
public class FirebaseStorageService implements ImageStorageService {

    @Value("${asada.firebase.bucket-name:}")
    private String bucketName;

    @Value("${asada.firebase.credentials-json:}")
    private String credentialsJson;

    @Value("${asada.firebase.credentials-path:}")
    private String credentialsPath;

    private Storage storage;

    @PostConstruct
    public void init() throws IOException {
        if (bucketName == null || bucketName.isBlank()) {
            throw new IllegalStateException(
                    "asada.storage.provider=firebase pero no se configuró asada.firebase.bucket-name.");
        }

        GoogleCredentials credentials;
        try (InputStream credentialsStream = abrirCredenciales()) {
            credentials = GoogleCredentials.fromStream(credentialsStream);
        }

        this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    private InputStream abrirCredenciales() throws IOException {
        if (credentialsJson != null && !credentialsJson.isBlank()) {
            return new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));
        }
        if (credentialsPath != null && !credentialsPath.isBlank()) {
            return new FileInputStream(credentialsPath);
        }
        throw new IllegalStateException(
                "asada.storage.provider=firebase pero no se configuró asada.firebase.credentials-json "
                + "ni asada.firebase.credentials-path (credenciales de la cuenta de servicio de Firebase).");
    }

    /**
     * Sube la imagen al bucket configurado, la hace pública, y devuelve
     * su URL pública de Google Cloud Storage.
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

        // Se agrega un sufijo aleatorio corto para evitar que el navegador
        // muestre una versión vieja en caché al reemplazar una foto.
        String nombreArchivo = "img" + id + "-" + UUID.randomUUID().toString().substring(0, 8) + extension;
        String objectName = folder + "/" + nombreArchivo;

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());
        storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, objectName);
    }

    /**
     * Sube un archivo de cualquier tipo bajo un nombre único, lo hace
     * público, y devuelve su URL. Conserva el nombre original en el
     * nombre del objeto (después del UUID) solo para facilitar la
     * inspección manual del bucket; el nombre que se muestra al usuario
     * en la aplicación siempre es el guardado en la base de datos.
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

        String nombreArchivo = UUID.randomUUID().toString().substring(0, 8) + extension;
        String objectName = folder + "/" + nombreArchivo;

        BlobId blobId = BlobId.of(bucketName, objectName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();

        storage.create(blobInfo, file.getBytes());
        storage.createAcl(blobId, Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

        return String.format("https://storage.googleapis.com/%s/%s", bucketName, objectName);
    }

}
