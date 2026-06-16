package com.plataformadeportiva.auth_service.services;

import java.util.List;

import org.springframework.stereotype.Service;
import com.plataformadeportiva.auth_service.models.PerfilUsuario;
import com.plataformadeportiva.auth_service.repositories.PerfilUsuarioRepository;


// Anotación que indica que esta clase es un servicio de Spring, lo que permite su inyección en otros componentes de la aplicación, 
// como controladores o servicios relacionados con la autenticación y gestión de usuarios en la plataforma deportiva.
@Service 
public class PerfilService {
    
    // Inyección de dependencia del PerfilUsuarioRepository para acceder a la base de datos y realizar operaciones CRUD relacionadas con los perfiles de usuario
    private final PerfilUsuarioRepository perfilUsuarioRepository;

    // Constructor que recibe el PerfilUsuarioRepository como parámetro, lo que permite la inyección de dependencias y facilita la prueba unitaria de esta clase
    PerfilService(PerfilUsuarioRepository perfilUsuarioRepository) {
        this.perfilUsuarioRepository = perfilUsuarioRepository;
    } 

    // Método que devuelve una lista de todos los perfiles de usuario disponibles en la base de datos, lo que permite a los clientes obtener información sobre los perfiles existentes en la plataforma deportiva
    public List<PerfilUsuario> listarPerfiles() {
        return perfilUsuarioRepository.findAll();
    }

}
