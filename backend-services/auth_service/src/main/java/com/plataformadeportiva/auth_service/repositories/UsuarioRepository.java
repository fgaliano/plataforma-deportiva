package com.plataformadeportiva.auth_service.repositories;

/**
 * El UsuarioRepository es una interfaz que extiende JpaRepository, lo que le proporciona métodos CRUD (Create, Read, Update, Delete) 
 * para gestionar los usuarios en la base de datos.
 * 
 * Además de los métodos heredados de JpaRepository, esta interfaz define dos métodos personalizados existsBy
 * usuario y existsByMail que permiten verificar si ya existe un usuario con el mismo nombre de usuario o 
 * correo electrónico antes de registrar un nuevo usuario, lo que ayuda a evitar duplicados en la base de datos.
 * 
 * La anotación @Repository indica que esta interfaz es un componente de Spring que se encargará de la capa de acceso a datos, 
 * lo que facilita la inyección de dependencias y el manejo de excepciones específicas de la capa de persistencia.
 * 
 * En resumen, el UsuarioRepository es fundamental para gestionar los usuarios en la plataforma deportiva, permitiendo realizar operaciones de
 * persistencia de manera eficiente y segura, así como verificar la existencia de usuarios por su nombre de usuario o correo electrónico para garantizar
 * la integridad de los datos y evitar duplicados en el sistema. 
 */
import com.plataformadeportiva.auth_service.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Para comprobar duplicados antes de registrar
    boolean existsByUsuario(String usuario); // Permite verificar si ya existe un usuario con el mismo nombre de usuario en la base de datos, lo que ayuda a evitar duplicados al registrar un nuevo usuario
    boolean existsByMail(String mail); // Permite verificar si ya existe un usuario con el mismo correo electrónico en la base de datos, lo que ayuda a evitar duplicados al registrar un nuevo usuario
    Optional<Usuario> findByUsuario(String usuario); // Permite buscar un usuario por su nombre de usuario, lo que es útil para el proceso de autenticación y otras operaciones relacionadas con la gestión de usuarios en la plataforma deportiva
    List<Usuario> findAll(); // Permite obtener una lista de todos los usuarios registrados en la base de datos, lo que es útil para mostrar la información de los usuarios en la plataforma deportiva o realizar operaciones de administración de usuarios
}
    
