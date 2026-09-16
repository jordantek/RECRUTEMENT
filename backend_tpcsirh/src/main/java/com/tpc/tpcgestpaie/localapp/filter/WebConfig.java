package com.tpc.tpcgestpaie.localapp.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Autoriser toutes les routes à recevoir des requêtes de React
        registry.addMapping("/**") // Toutes les routes
                .allowedOrigins("http://localhost:5173") // L'adresse de ton front React (par défaut en dev)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Les méthodes autorisées
                .allowedHeaders("*") // Autoriser tous les headers
                .allowCredentials(true); // Autoriser les cookies ou les sessions
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // On définit le chemin absolu vers /uploads
        Path uploadDir = Paths.get("uploads"); // le dossier dans ton projet ou serveur
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Mapping URL -> chemin physique
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }


}
