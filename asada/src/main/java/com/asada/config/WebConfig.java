package com.asada.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${asada.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/403")
                .setViewName("403");

    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Sirve las imágenes de usuarios/abonados guardadas en disco local.
        // En la Entrega 3 este mapeo dejará de ser necesario al migrar a
        // Firebase Storage (las imágenes se servirán directamente desde ahí).
        String location = "file:" + uploadDir.replace("\\", "/")
                + (uploadDir.endsWith("/") ? "" : "/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location);

    }

}
