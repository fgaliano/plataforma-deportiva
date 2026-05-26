package com.plataformadeportiva.auth_service.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;

/**
 * El JwtService es una clase de servicio que se encarga de generar, validar y extraer información de los tokens JWT (JSON Web Tokens) 
 * utilizados para la autenticación en la plataforma deportiva. Esta clase utiliza la biblioteca jjwt para manejar la creación y validación de los tokens JWT, 
 * lo que permite asegurar la autenticación de los usuarios en el sistema. 
 * El JwtService define una clave secreta para firmar los tokens, y proporciona métodos para generar un token a partir del nombre de usuario, 
 * extraer el nombre de usuario de un token y validar si un token es correcto. 
 * En resumen, el JwtService es fundamental para implementar la autenticación basada en tokens JWT en la plataforma deportiva, 
 * permitiendo a los usuarios autenticarse de manera segura y eficiente en el sistema. 
 */
@Service // Anotación que indica que esta clase es un servicio de Spring, lo que permite su inyección en otros componentes de la aplicación, como controladores o servicios relacionados con la autenticación y gestión de usuarios en la plataforma deportiva.
public class JwtService {

    //  Clave secreta para firmar los tokens (en un entorno real, esta clave debería ser más segura y almacenada de forma segura)
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 1. GENERAR UN TOKEN A PARTIR DEL NOMBRE DE USUARIO
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Expira en 1 hora
                .signWith(KEY)
                .compact();
    }

    // 2. EXTRAER EL NOMBRE DEL USUARIO DEL TOKEN
    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 3. VALIDAR SI EL TOKEN ES CORRECTO
    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}