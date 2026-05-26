package com.plataformadeportiva.auth_service.config;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.plataformadeportiva.auth_service.services.JwtService;

import java.io.IOException;
import java.util.Collections;

/**
 * El JwtAuthenticationFilter es un filtro personalizado que se ejecuta en cada solicitud HTTP para validar la presencia y validez de un token JWT en la cabecera Authorization.
 * Este filtro extiende OncePerRequestFilter, lo que garantiza que se ejecute una sola vez por solicitud. 
 * 
 * En el método doFilterInternal, el filtro verifica si la ruta de la solicitud es de autenticación (registro o login) y, si es así, 
 * permite que la solicitud continúe sin validar el token.  Esto se debe a que estas rutas deben ser accesibles sin autenticación para permitir a los usuarios registrarse o iniciar sesión.
 * 
 * Para otras rutas, el filtro extrae el token JWT de la cabecera Authorization, valida su formato y, si es válido, extrae el nombre de usuario del token y establece 
 * la autenticación en el contexto de seguridad de Spring, lo que permite que el usuario autenticado acceda a los recursos protegidos. 
 * 
 * Si el token no es válido o no está presente, el filtro simplemente permite que la solicitud continúe sin establecer la autenticación, 
 * lo que resultará en una respuesta de acceso denegado para los recursos protegidos. En resumen, el JwtAuthenticationFilter es fundamental para implementar 
 * la autenticación basada en tokens JWT en la plataforma deportiva, asegurando que solo los usuarios autenticados puedan acceder a los recursos protegidos del sistema. 
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService; // Inyección de dependencia del JwtService para validar los tokens JWT en cada solicitud, lo que permite asegurar la autenticación de los usuarios en el sistema basado en tokens JWT

    /**
     * Este método se ejecuta en cada solicitud HTTP y realiza las siguientes acciones:
     * 1. Verifica si la ruta de la solicitud es de autenticación (registro o login) y, si es así, permite que la solicitud continúe sin validar el token, ya que estas rutas deben ser accesibles sin autenticación para permitir a los usuarios registrarse o iniciar sesión.
     * 2. Para otras rutas, extrae el token JWT de la cabecera Authorization, valida su formato y, si es válido, extrae el nombre de usuario del token y establece la autenticación en el contexto de seguridad de Spring, lo que permite que el usuario autenticado acceda a los recursos protegidos.
     * 3. Si el token no es válido o no está presente, permite que la solicitud continúe sin establecer la autenticación, lo que resultará en una respuesta de acceso denegado para los recursos protegidos.
     * param request // El objeto HttpServletRequest que representa la solicitud HTTP entrante, que contiene información sobre la ruta, las cabeceras y otros detalles de la solicitud
     * param response // El objeto HttpServletResponse que representa la respuesta HTTP que se enviará al cliente, que puede ser modificada para incluir información sobre el resultado de la validación del token JWT
     * param filterChain // El objeto FilterChain que permite continuar con la cadena de filtros después de que este filtro haya realizado su trabajo, lo que es necesario para que la solicitud pueda llegar a los controladores y otros componentes de la aplicación después de la validación del token JWT
     * throws ServletException, IOException // Indica que este método puede lanzar excepciones de tipo ServletException o IOException, lo que es común en los filtros de servlets y permite manejar errores relacionados con la validación del token JWT o problemas en la cadena de filtros
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try{

                //  Si la ruta es de autenticación (registro o login), no validamos el token y dejamos pasar la solicitud sin autenticación, ya que estas rutas deben ser accesibles sin autenticación para permitir a los usuarios 
                if (request.getServletPath().contains("/auth/register") || request.getServletPath().contains("/auth/login")) {
                    filterChain.doFilter(request, response);
                    return;
                }
                
                // Para otras rutas, validamos el token JWT en la cabecera Authorization y, si es válido, establecemos la autenticación en el contexto de seguridad de Spring para permitir el acceso a los recursos protegidos
                final String authHeader = request.getHeader("Authorization");
                final String jwt;
                final String username;

                // Si no hay cabecera Authorization o no empieza por Bearer, ignoramos
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    filterChain.doFilter(request, response);
                    return;
                }

                jwt = authHeader.substring(7); // Quitamos la palabra "Bearer "
                username = jwtService.extractUsername(jwt);

                // Si hay usuario y no está ya autenticado en esta petición...
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtService.isTokenValid(jwt)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                username, null, Collections.emptyList()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // ¡AUTORIZADO!
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
                filterChain.doFilter(request, response);


    } catch (io.jsonwebtoken.security.SignatureException e) {
        // 🎯 CAPTURA DEL TOKEN FALSO o MANIPULADO
            armarRespuestaDeError(response, "El token proporcionado ha sido manipulado o tiene una firma invalida.");
        
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // ⏰ CAPTURA DEL TOKEN CADUCADO (Muy útil para más adelante)
            armarRespuestaDeError(response, "El token de acceso ha expirado. Vuelve a iniciar sesion.");
            
        } catch (Exception e) {
            // 🛡️ CAPTURA DE CUALQUIER OTRO ERROR CON EL JWT
            armarRespuestaDeError(response, "Error al procesar el token de autenticacion.");
        }

    }

    /**
 * Método auxiliar para escribir un JSON limpio de error directamente en la respuesta HTTP
 */
private void armarRespuestaDeError(HttpServletResponse response, String mensaje) throws IOException {

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Devuelve un código 401
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    
    // Construimos un JSON manual muy sencillo para que Postman lo lea bonito
    String jsonResponse = String.format("{\n  \"error\": \"Unauthorized\",\n  \"message\": \"%s\"\n}", mensaje);
    
    response.getWriter().write(jsonResponse);
}

}