package com.plataformadeportiva.auth_service.repositories; // <-- Con el guion bajo

import com.plataformadeportiva.auth_service.models.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * El PerfilUsuarioRepository es una interfaz que extiende JpaRepository, lo que le proporciona métodos CRUD para interactuar con la base de datos. Esta interfaz se utiliza para realizar operaciones relacionadas con los perfiles de usuario, como buscar un perfil por su nombre.
 * El método findByPerfil permite buscar un perfil específico en la tabla "gpdp_perfiles
 * _usuarios" utilizando el nombre del perfil (ej: "ADMIN", "DEPORTISTA"). Este método devuelve un Optional<PerfilUsuario>, lo que significa que puede devolver un perfil si se encuentra o un valor vacío si no se encuentra ningún perfil con el nombre especificado.
 * La anotación @Repository indica que esta interfaz es un componente de Spring que se encargará de la interacción con la base de datos, lo que permite a Spring manejar las excepciones relacionadas con la base de datos de manera adecuada.
 * En resumen, el PerfilUsuarioRepository es fundamental para gestionar los perfiles de usuario en la plataforma deportiva, permitiendo realizar operaciones de búsqueda y gestión de perfiles de manera eficiente a través de la base de datos.
  */
@Repository
public interface PerfilUsuarioRepository extends JpaRepository<PerfilUsuario, Long> {
    
    // Método para buscar un perfil por su nombre (ej: "ADMIN", "DEPORTISTA")
    Optional<PerfilUsuario> findByPerfil(String perfil);

    // Método para buscar un perfil por su ID (Long id_perfil)
    Optional<PerfilUsuario> findById(Long id_perfil);
}