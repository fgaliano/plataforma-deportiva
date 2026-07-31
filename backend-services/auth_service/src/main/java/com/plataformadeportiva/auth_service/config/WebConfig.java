package com.plataformadeportiva.auth_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Obtenemos la ruta física de la carpeta de fotos
        String rutaUsuarios = Paths.get("uploads/fotos_usuarios").toAbsolutePath().toUri().toString();
        
        // Registramos el manejador para /uploads/**
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(rutaUsuarios);
    }
}