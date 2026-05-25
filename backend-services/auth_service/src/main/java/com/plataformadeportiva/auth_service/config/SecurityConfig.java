package com.plataformadeportiva.auth_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactiva CSRF para que Postman pueda hacer POST sin problemas
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Abre todas las URLs al público (¡adiós al 401!)
            );
        return http.build();
    }

    /**
     *  Este método define un bean de tipo PasswordEncoder que utiliza BCryptPasswordEncoder para cifrar las contraseñas de los usuarios antes de almacenarlas en la base de datos.
     *  BCrypt es un algoritmo de hashing seguro que incluye un factor de costo para hacer que el proceso de hashing sea más lento y, por lo tanto, más resistente a ataques de fuerza bruta. Al definir este bean, Spring Security lo utilizará automáticamente para cifrar las contraseñas cuando se registren nuevos usuarios o se actualicen las contraseñas existentes, garantizando así la seguridad de las credenciales de los usuarios en la plataforma deportiva.
      * return Un objeto PasswordEncoder que utiliza BCrypt para cifrar las contraseñas.
      */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}