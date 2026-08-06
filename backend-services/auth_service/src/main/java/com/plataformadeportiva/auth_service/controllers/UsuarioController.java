package com.plataformadeportiva.auth_service.controllers;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.plataformadeportiva.auth_service.models.Usuario;
import com.plataformadeportiva.auth_service.repositories.UsuarioRepository;
import com.plataformadeportiva.auth_service.services.UsuarioService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    private final UsuarioService usuarioService; // Inyección de dependencia del UsuarioService para acceder a la lógica de negocio relacionada con la gestión de usuarios, lo que permite registrar nuevos usuarios a través del servicio
    private final UsuarioRepository usuarioRepository; // Inyección de dependencia del UsuarioRepository para acceder a la base de datos y realizar operaciones CRUD relacionadas con los usuarios, lo que permite buscar usuarios por su nombre de usuario durante el proceso de autenticación

    UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    } // Inyección de dependencia del UsuarioService para acceder a la lógica de negocio relacionada con la gestión de usuarios, lo que permite registrar nuevos usuarios a través del servicio

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
    public ResponseEntity<?> registrar(
                                        @RequestPart("usuario") Usuario usuario,
                                        @RequestPart(value = "foto", required = false) MultipartFile foto) { // Recibe la información del nuevo usuario a través del cuerpo de la solicitud (RequestBody) y el nombre del perfil a asignar a través de un parámetro de consulta (RequestParam)
        try {

            // Llamamos al servicio para registrar el nuevo usuario, pasando la información del usuario y el nombre del perfil a asignar
            usuarioService.registrarUsuario(usuario);
            Usuario usuarioCreadoDb = usuarioRepository.findByUsuario(usuario.getUsuario()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Si el registro es exitoso, devolvemos una respuesta HTTP 200 OK con la información del usuario registrado

            // 1. Creamos la carpeta física en el servidor si no existe
            String rutaCarpeta = "uploads/fotos_usuarios/"; // Ruta relativa donde se guardarán las fotos de los usuarios
            File carpeta = new File(rutaCarpeta);
            if (!carpeta.exists()) {
                carpeta.mkdirs(); 
            } 
            
            // 2. Si el usuario subió una foto desde el formulario, la guardamos
            if (foto != null && !foto.isEmpty()) {
                String nombreOriginal = foto.getOriginalFilename();
                String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));

                // El nombre del archivo físico será el nombre de usuario (ej: ngalgom.png)
                String nombreArchivoFoto = usuario.getUsuario() + extension;

                // Guardamos el archivo en el disco duro del servidor
                Path rutaCompleta = Paths.get(rutaCarpeta + nombreArchivoFoto);
                Files.write(rutaCompleta, foto.getBytes());
                
                // Guardamos el nombre de la foto en el objeto que va a la base de datos
                usuarioCreadoDb.setRutaFoto(nombreArchivoFoto); 

                // Guardamos el usuario con la ruta de la foto actualizada en la base de datos
                usuarioRepository.save(usuarioCreadoDb);

            } 

            return ResponseEntity.ok(usuarioCreadoDb);
            
        } catch (RuntimeException e) {
            // Si el servicio lanza un error (duplicados, etc.), devolvemos un 400 Bad Request con el mensaje
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar la foto: " + e.getMessage());
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
    public ResponseEntity<?> login(@RequestPart("usuario") Usuario usuario) {

        try {
            // Llamamos al servicio para autenticar al usuario, pasando la información del usuario.
            String token = usuarioService.login(usuario);

            Optional<Usuario> userDbUsuarioOpt = usuarioRepository.findByUsuario(usuario.getUsuario());
            Usuario userDbUsuario = userDbUsuarioOpt.orElseThrow(() -> new RuntimeException("El nombre de usuario no está registrado."));

            // 🚀 En lugar de enviar solo el texto, metemos el token en un mapa estructurado
            Map<String, String> respuestaJson = new HashMap<>();
            respuestaJson.put("token", token);
            respuestaJson.put("usuario", userDbUsuario.getUsuario()); // O 'usuario.getUsername()', como lo tengas mapeado
            respuestaJson.put("usuarioPerfil", userDbUsuario.getPerfil().getPerfil()); // O 'usuario.getPerfil()', como lo tengas mapeado
            respuestaJson.put("rutaFoto", userDbUsuario.getRutaFoto()); // O 'usuario.getRutaFoto()', como lo tengas mapeado

            // Si la autenticación es exitosa, devolvemos el JSON estructurado
            return ResponseEntity.ok(respuestaJson);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }


    }

    /**
     * 
     * Este método maneja las solicitudes GET a la ruta "/auth/foto/{nombreFoto}" para obtener la foto de perfil de un usuario. Realiza las siguientes acciones:
     * 1. Construye la ruta absoluta hacia el archivo en la carpeta uploads     
     * 2. Verifica que el archivo existe y es legible
     * 3. Detecta el tipo de contenido (PNG, JPG, etc.) o
     *   por defecto octet-stream
     * 4. Retorna la foto protegida con respuesta 200 OK
     * 5. Si ocurre algún error durante el proceso (como archivo no encontrado), devuelve una respuesta HTTP 404 Not Found o 500 Internal Server Error según corresponda   
     * param nombreFoto El nombre del archivo de la foto de perfil que se desea obtener
     * return ResponseEntity con la foto de perfil o un mensaje de error
     *  
     */
    @GetMapping("/foto/{nombreFoto}")
    public ResponseEntity<Resource> obtenerFotoPerfil(@PathVariable String nombreFoto) {
        try {
            // 1. Construimos la ruta absoluta hacia el archivo en la carpeta uploads
            Path rutaArchivo = Paths.get("uploads/fotos_usuarios").resolve(nombreFoto).normalize();
            Resource recurso = new UrlResource(rutaArchivo.toUri());

            // 2. Verificamos que el archivo existe y es legible
            if (!recurso.exists() || !recurso.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // 3. Detectamos el tipo de contenido (PNG, JPG, etc.) o por defecto octet-stream
            String contentType = "image/png"; 
            if (nombreFoto.endsWith(".jpg") || nombreFoto.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            }

            // 4. Retornamos la foto protegida con respuesta 200 OK
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + recurso.getFilename() + "\"")
                    .body(recurso);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }    

    @GetMapping("/listado_usuarios") 
    public ResponseEntity<?> listadoUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }
    
}