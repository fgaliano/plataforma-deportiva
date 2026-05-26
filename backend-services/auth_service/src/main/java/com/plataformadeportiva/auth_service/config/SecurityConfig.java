package com.plataformadeportiva.auth_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 *  La clase SecurityConfig es una configuración de seguridad para la aplicación de autenticación. Esta clase se encarga de definir las reglas de seguridad para las rutas de la aplicación, así como de configurar el filtro personalizado JwtAuthenticationFilter para validar los tokens JWT en cada solicitud.
 *  En el método securityFilterChain, se configuran las reglas de seguridad para las rutas de la aplicación, permitiendo el acceso libre a las rutas de registro y login, y exigiendo autenticación para cualquier otra ruta. Además, se inyecta el filtro JwtAuthenticationFilter antes del filtro de autenticación por defecto de Spring para validar los tokens JWT en cada solicitud.
 *  En el método corsConfigurationSource, se define una configuración explícita de CORS para permitir las solicitudes desde el origen del servidor de desarrollo de React, lo que es necesario para que la aplicación frontend pueda comunicarse con la API de autenticación sin problemas de CORS.
 *  En resumen, la clase SecurityConfig es fundamental para asegurar que la aplicación de autenticación esté protegida adecuadamente, permitiendo el acceso a las rutas públicas de registro y login, y asegurando que solo los usuarios autenticados puedan acceder a los recursos protegidos del sistema a través de la validación de tokens JWT. Además, la configuración de CORS garantiza que la aplicación frontend pueda interactuar con la API de autenticación sin problemas de seguridad relacionados con el intercambio de recursos entre diferentes orígenes. 
 */
@Configuration // Indica que esta clase es una clase de configuración de Spring, lo que permite definir beans y otras configuraciones relacionadas con la seguridad de la aplicación
@EnableWebSecurity // Habilita la seguridad web en la aplicación, lo que permite configurar las reglas de seguridad para las rutas y los filtros de autenticación
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter; // Tu filtro personalizado para validar tokens

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Enlazamos la seguridad con el Bean de CORS que está abajo
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            
            // Desactivamos CSRF ya que usamos tokens JWT y no cookies de sesión
            .csrf(csrf -> csrf.disable()) 
            
            .authorizeHttpRequests(auth -> auth
                // 2. Paso libre absoluto a peticiones de tipo OPTIONS (Preflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // 3. Tus endpoints públicos (Registro y Login)
                .requestMatchers("/auth/register", "/auth/login").permitAll() 
                
                // 4. Cualquier otra petición (como /auth/listado-usuarios) exigirá Token JWT
                .anyRequest().authenticated()
            )
            // 5. Inyectamos tu filtro JWT antes del filtro de login por defecto de Spring
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 📌 Configuración explícita de CORS para Spring Security.
     * Esto intercepta las llamadas de React antes de que lleguen a los filtros de autenticación.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitimos explicitamente el origen de tu servidor de desarrollo en React
        //configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // Si quieres permitir cualquier origen (útil para desarrollo pero no recomendado para producción sin restricciones adicionales), usa AllowedOriginPatterns con "*"
        //configuration.setAllowedOriginPatterns(List.of("*")); // 👈 Con "Patterns" sí se permite el asterisco

        // Para ser más explícitos y seguros, vamos a listar los orígenes permitidos en lugar de usar un comodín
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", "http://localhost"));
        
        // Métodos HTTP permitidos para la API
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Cabeceras permitidas. Añadimos 'Authorization' para que acepte tu Bearer Token
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        
        // Permitir el intercambio de credenciales si fuera necesario
        configuration.setAllowCredentials(true);

        // Aplicamos esta configuración de CORS a todas las rutas de la aplicación (/**)
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Bean para el encriptador de contraseñas. Usamos BCrypt, que es un algoritmo de hashing seguro y recomendado para almacenar contraseñas.
     * Este bean se inyectará en el UsuarioService para encriptar las contraseñas antes de guardarlas en la base de datos, lo que mejora la seguridad de la aplicación al proteger las contraseñas de los usuarios contra accesos no autorizados. 
     * return Un nuevo BCryptPasswordEncoder, que es un encriptador de contraseñas basado en el algoritmo BCrypt, que se utiliza para encriptar las contraseñas de los usuarios antes de almacenarlas en la base de datos, lo que mejora la seguridad de la aplicación al proteger las contraseñas contra accesos no autorizados. 
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}