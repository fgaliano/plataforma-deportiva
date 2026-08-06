package com.plataformadeportiva.auth_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

/**
 * La clase WebConfig es una configuración de Spring MVC que se encarga de definir cómo se manejan los recursos estáticos en la aplicación. 
 * En este caso, se utiliza para exponer la carpeta "uploads/fotos_usuarios" como un recurso accesible públicamente a través de la URL "/uploads/**". 
 * Esto permite que las fotos de los usuarios almacenadas en el servidor puedan ser accedidas desde el frontend de la aplicación.   
 *  
 * WebConfig
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Obtenemos la ruta física de la carpeta de fotos
        String rutaUsuarios = Paths.get(uploadDir).toAbsolutePath().toUri().toString();
        
        // Registramos el manejador para /uploads/**
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(rutaUsuarios);
    }
}