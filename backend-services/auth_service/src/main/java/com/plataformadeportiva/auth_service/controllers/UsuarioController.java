package com.plataformadeportiva.auth_service.controllers;

import com.plataformadeportiva.auth_service.models.Usuario;
import com.plataformadeportiva.auth_service.services.UsuarioService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * El UsuarioController es una clase de controlador que maneja las solicitudes HTTP relacionadas con la gestión de usuarios en el sistema de autenticación.
 * Esta clase define un endpoint para registrar nuevos usuarios en la plataforma deportiva, utilizando el método POST
 * 
 * El método registrar recibe la información del nuevo usuario a través del cuerpo de la solicitud (RequestBody) y el nombre del perfil a asignar a través de un 
 * parámetro de consulta (RequestParam).
 * 
 * Luego, llama al método registrarUsuario del UsuarioService para realizar el proceso de registro, que incluye la validación de duplicados, 
 * la asignación del perfil y la persistencia en la base de datos.
 * 
 * Si el registro es exitoso, devuelve una respuesta HTTP 200 OK con la información del usuario registrado. Si ocurre algún error durante el proceso
 * (como duplicados o perfil no encontrado), devuelve una respuesta HTTP 400 Bad Request con el mensaje de error correspondiente.
 * 
 * En resumen, el UsuarioController es fundamental para manejar las solicitudes de registro de usuarios en la
 * plataforma deportiva, permitiendo a los clientes interactuar con el sistema de autenticación de manera eficiente y segura.
 *
 **/
@RestController
@RequestMapping("/auth") // Define la ruta base para todos los endpoints de este controlador, lo que significa que todas las solicitudes a este controlador deben comenzar con "/auth"
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService; // Inyección de dependencia del UsuarioService para acceder a la lógica de negocio relacionada con la gestión de usuarios, lo que permite registrar nuevos usuarios a través del servicio

    // Endpoint para registrar usuarios: POST http://localhost:8081/auth/register

    /**
     * Este método maneja las solicitudes POST a la ruta "/auth/register" para registrar un nuevo usuario en el sistema. Realiza las siguientes acciones:
     * 1. Recibe la información del nuevo usuario a través del cuerpo de la solicitud
     * 2. Recibe el nombre del perfil a asignar al nuevo usuario a través de un parámetro de consulta
     * 3. Llama al método registrarUsuario del UsuarioService para realizar el proceso de
     *    registro, que incluye la validación de duplicados, la asignación del perfil y la persistencia en la base de datos
     * 4. Devuelve una respuesta HTTP 200 OK con la información del usuario registrado
     * 5. Si ocurre algún error durante el proceso (como duplicados o perfil no encontrado), devuelve una respuesta HTTP 400 
     *    Bad Request con el mensaje de error correspondiente
     * param usuario
     * param perfil
     * return ResponseEntity con la información del usuario registrado o un mensaje de error
     */
    @PostMapping("/register") // Define que este método manejará las solicitudes POST a la ruta "/auth/register", lo que significa que los clientes deben enviar una solicitud POST a esta ruta para registrar un nuevo usuario
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            // Llamamos al servicio para registrar el nuevo usuario, pasando la información del usuario y el nombre del perfil a asignar
            Usuario usuarioCreado = usuarioService.registrarUsuario(usuario);
            // Si el registro es exitoso, devolvemos una respuesta HTTP 200 OK con la información del usuario registrado
            return ResponseEntity.ok(usuarioCreado);
        } catch (RuntimeException e) {
            // Si el servicio lanza un error (duplicados, etc.), devolvemos un 400 Bad Request con el mensaje
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Este método maneja las solicitudes POST a la ruta "/auth/login" para autenticar a un usuario existente en el sistema. Realiza las siguientes acciones:
     * 1. Recibe la información del usuario a través del cuerpo de la solicitud
     * 2. Llama al método login del UsuarioService para realizar el proceso de autenticación, que incluye la validación de las credenciales del usuario
     * 3. Si la autenticación es exitosa, devuelve una respuesta HTTP 200 OK con la información del usuario autenticado
     * 4. Si ocurre algún error durante el proceso (como credenciales incorrectas), devuelve una respuesta HTTP 400 Bad Request con el mensaje de error correspondiente
     * param usuario La información del usuario que se intenta autenticar, incluyendo el correo electrónico y la contraseña   
     * return ResponseEntity con la información del usuario autenticado o un mensaje de error
     */
    @PostMapping("/login") // Define que este método manejará las solicitudes POST a la ruta "/auth/login", lo que significa que los clientes deben enviar una solicitud POST a esta ruta para iniciar sesión con un usuario existente 
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {

        try {
            // Llamamos al servicio para autenticar al usuario, pasando la información del usuario.
            String token = usuarioService.login(usuario);
            // Si la autenticación es exitosa, devolvemos una respuesta HTTP 200 OK con la información del usuario autenticado
            return ResponseEntity.ok(token);

        } catch (RuntimeException e) {
            //  CAPTURAMOS EL ERROR Y DEVOLVEMOS UN JSON
            // Creamos un mapa que Spring convertirá automáticamente a {"message": "El nombre de..."}
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
        
            // Devolvemos un 401 (No autorizado) o 404 (No encontrado) con el JSON dentro   
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }


    }

    @GetMapping("/listado_usuarios") 
    public ResponseEntity<?> listadoUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }
    
}