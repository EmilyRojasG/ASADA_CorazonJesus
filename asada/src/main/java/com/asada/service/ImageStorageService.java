package com.asada.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/**
 * Abstrae el almacenamiento de imágenes (usuarios, abonados) para que el
 * resto de la aplicación no dependa de si el proveedor real es disco
 * local ({@link LocalStorageService}) o Firebase Storage
 * ({@link FirebaseStorageService}).
 *
 * El proveedor activo se elige con la propiedad
 * {@code asada.storage.provider} ("local" por defecto, o "firebase").
 */
public interface ImageStorageService {

    /**
     * Guarda la imagen recibida y devuelve la URL pública con la que se
     * puede mostrar en las vistas.
     *
     * @param file archivo recibido del formulario (input type="file").
     * @param folder subcarpeta lógica ("usuario", "abonado", etc.).
     * @param id identificador del registro dueño de la imagen, usado para
     * nombrar el archivo de forma única.
     */
    String uploadImage(MultipartFile file, String folder, Integer id) throws IOException;

    /**
     * Guarda un archivo de cualquier tipo (PDF, Word, Excel, imágenes,
     * etc.) bajo un nombre único generado automáticamente, y devuelve la
     * URL pública con la que se puede ver/descargar. A diferencia de
     * {@link #uploadImage}, aquí puede haber varios archivos para un
     * mismo registro (por ejemplo, varios adjuntos de una misma carta de
     * disponibilidad), así que el nombre no depende del id del dueño.
     *
     * @param file archivo recibido del formulario (input type="file").
     * @param folder subcarpeta lógica ("carta_disponibilidad/12", etc.).
     */
    String uploadFile(MultipartFile file, String folder) throws IOException;

}
