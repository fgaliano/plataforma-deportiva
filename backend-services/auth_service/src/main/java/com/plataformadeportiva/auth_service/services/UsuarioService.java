package com.plataformadeportiva.auth_service.services;

import com.plataformadeportiva.auth_service.models.PerfilUsuario;
import com.plataformadeportiva.auth_service.models.Usuario;
import com.plataformadeportiva.auth_service.repositories.PerfilUsuarioRepository;
import com.plataformadeportiva.auth_service.repositories.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * El UsuarioService es una clase de servicio que contiene la lógica de negocio relacionada con la gestión de usuarios en el sistema de autenticación.
 * Esta clase se encarga de validar la información del usuario, asignar el perfil correspondiente y
 * guardar la información del usuario en la base de datos a través del UsuarioRepository.
 * 
 * El método registrarUsuario es el encargado de realizar todo el proceso de registro de un nuevo usuario, incluyendo la validación de duplicados, 
 * la asignación del perfil y la persistencia en la base de datos.
 *  
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository; // Inyección de dependencia del UsuarioRepository para acceder a la base de datos y realizar operaciones CRUD relacionadas con los usuarios
    private final PerfilUsuarioRepository perfilUsuarioRepository; // Inyección de dependencia del PerfilUsuarioRepository para acceder a la base de datos y realizar operaciones CRUD relacionadas con los perfiles de usuario
    private final JwtService jwtService; // Inyección de dependencia del JwtService para generar tokens JWT para los usuarios autenticados, lo que permite implementar la autenticación basada en tokens JWT en la plataforma deportiva
    private final PasswordEncoder passwordEncoder;

    UsuarioService(UsuarioRepository usuarioRepository, PerfilUsuarioRepository perfilUsuarioRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.perfilUsuarioRepository = perfilUsuarioRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    } // <-- Inyectamos el encriptador que creamos en SecurityConfig
    /**
     * Este método se encarga de registrar un nuevo usuario en el sistema. Realiza las siguientes acciones:
     * 1. Valida si el nombre de usuario ya existe en la base de datos
     * 2. Valida si el correo electrónico ya existe en la base de datos
     * 3. Busca el perfil correspondiente en la tabla paramétrica de perfiles de usuario
     * 4. Asigna el perfil encontrado al nuevo usuario
     * 5. Guarda la información del nuevo usuario en la tabla de usuarios de la base de datos
     * param nuevoUsuario // El objeto Usuario que contiene la información del nuevo usuario a registrar, incluyendo su nombre, apellidos, correo electrónico, nombre de usuario y contraseña
     * param nombrePerfil // El nombre del perfil que se desea asignar al nuevo usuario (ej: "DEPORTISTA"), que se utilizará para buscar el perfil correspondiente en la tabla paramétrica de perfiles de usuario
     * return // El objeto Usuario que ha sido registrado exitosamente en la base de datos, incluyendo su ID generado automáticamente y el perfil asignado
     */
    public Usuario registrarUsuario(Usuario nuevoUsuario) {

        String passwordPlano = "";
        String passwordEncriptada = "";
        
        // Validaciones de campos obligatorios y formato de nombre y apellidos
        if (nuevoUsuario.getNombre() == null || nuevoUsuario.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio.");
        }
        
        // Validamos que el nombre no contenga números ni caracteres especiales usando Expresiones Regulares (Regex)
        if (!nuevoUsuario.getNombre().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new RuntimeException("El nombre no puede contener números ni caracteres especiales.");
        }

        // Validaciones de campos obligatorios y formato de apellidos
        if (nuevoUsuario.getApellidos() == null || nuevoUsuario.getApellidos().trim().isEmpty()) {
            throw new RuntimeException("Los apellidos son obligatorios.");
        }

        // Validamos que los apellidos no contengan números ni caracteres especiales usando Expresiones Regulares (Regex)
        if (!nuevoUsuario.getApellidos().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new RuntimeException("Los apellidos no pueden contener números ni caracteres especiales.");
        }        
        
        // 1. Validar si el usuario ya existe
        if (usuarioRepository.existsByUsuario(nuevoUsuario.getUsuario())) {
            throw new RuntimeException("El nombre de usuario ya está cogido.");
        }

        // 2. Validar si el correo ya existe
        if (usuarioRepository.existsByMail(nuevoUsuario.getMail())) {
            throw new RuntimeException("El correo electrónico ya está registrado.");
        }

        // Validamos la longitud mínima de 12 caracteres
        if (passwordPlano == null || passwordPlano.length() < 12) {
            throw new RuntimeException("La contraseña debe tener al menos 12 caracteres de longitud.");
        }
        
        // Validamos que contenga al menos un número usando Expresiones Regulares (Regex)
        // .*\\d.* significa: "busca si hay cualquier carácter, luego un dígito, luego cualquier otra cosa"
        if (!passwordPlano.matches(".*\\d.*")) {
            throw new RuntimeException("La contraseña debe contener al menos un número.");
        }
        
        // Validamos que contenga al menos un carácter especial
        // [!@#$%^&*(),.?\":{}|<>] define la lista de símbolos permitidos que buscamos
        if (!passwordPlano.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new RuntimeException("La contraseña debe contener al menos un carácter especial (ej: !, @, #, $, %).");
        }        

        // 3. Encriptar la contraseña antes de guardar el usuario
        passwordPlano = nuevoUsuario.getPassword();  
        passwordEncriptada = passwordEncoder.encode(passwordPlano);
        nuevoUsuario.setPassword(passwordEncriptada);

        // 4. Sacamos el ID que ya vemos que viene con valor (Long@126)
        Long perfilId = nuevoUsuario.getPerfil().getId_perfil();

        // 5. Validamos que el ID del perfil exista en la base de datos
        PerfilUsuario perfilDb = perfilUsuarioRepository.findById(perfilId).orElseThrow(() -> new RuntimeException("El perfil con ID " + perfilId + " no existe en la base de datos."));

        // 6. Asignar el perfil encontrado al usuario
        nuevoUsuario.setPerfil(perfilDb);

        // 7. Guardar en la tabla gpdd_usuarios
        return usuarioRepository.save(nuevoUsuario);
    }

    /**
     * Este método se encarga de autenticar a un usuario existente en el sistema. Realiza las siguientes acciones:
     * 1. Busca el usuario en la base de datos por su nombre de usuario. Si no lo encuentra, lanza una excepción indicando que el nombre de usuario no está registrado.
     * 2. Valida la contraseña en texto plano que viene en la solicitud con la contraseña encriptada que tenemos en la base de datos utilizando el método matches del PasswordEncoder. Si las contraseñas coinciden, genera un token JWT para el usuario autenticado. 
     * Si las contraseñas no coinciden, lanza una excepción indicando que la contraseña es incorrecta.
     * param usuario    // El objeto Usuario que contiene la información del usuario que se intenta autenticar, incluyendo su nombre de usuario y contraseña en texto plano  
     * return // El objeto Usuario autenticado, incluyendo su información y un token JWT
     */
    public String login(Usuario usuario) {


        // 1. Buscamos el usuario en la base de datos por su nombre de usuario. Si no lo encontramos, lanzamos una excepción indicando que el nombre de usuario no está registrado.
        Usuario user = Optional.ofNullable(usuarioRepository.findByUsuario(usuario.getUsuario())).orElseThrow(() -> new RuntimeException("El nombre de usuario no está registrado."));

        // 2. Validar la contraseña en texto plano que viene en la solicitud con la contraseña encriptada que tenemos en la base de datos utilizando el método matches del PasswordEncoder. Si las contraseñas coinciden, generamos un token JWT para el usuario autenticado. Si las contraseñas no coinciden, lanzamos una excepción indicando que la contraseña es incorrecta.
        if (!passwordEncoder.matches(usuario.getPassword(), user.getPassword())) {
            throw new RuntimeException("La contraseña es incorrecta.");
        }

        
        String token = jwtService.generateToken(user.getUsuario());
        
        return token; // Devolvemos el token JWT generado

    }

    /**
     * Este método se encarga de listar todos los usuarios registrados en el sistema.
     * 1. Consulta la base de datos para obtener todos los usuarios.
     * 2. Devuelve la lista de usuarios encontrados.
     * return // La lista de usuarios registrados en el sistema
     */
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

}