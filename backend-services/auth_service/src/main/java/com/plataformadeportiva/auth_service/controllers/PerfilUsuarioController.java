
package com.plataformadeportiva.auth_service.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.plataformadeportiva.auth_service.services.PerfilService;


@RestController
@RequestMapping("/perfil") // Define la ruta base para todos los endpoints de este controlador, lo que significa que todas las solicitudes a este controlador deben comenzar con "/auth"
public class PerfilUsuarioController {

    // Inyección de dependencia del PerfilService para acceder a la lógica de negocio relacionada con la gestión de perfiles de usuario, lo que permite listar los perfiles disponibles en la plataforma
    private final PerfilService perfilService;

    PerfilUsuarioController(PerfilService perfilService) {
        this.perfilService = perfilService;
    }// Inyección de dependencia del PerfilService para acceder a la lógica de negocio relacionada con la gestión de perfiles de usuario, lo que permite listar los perfiles disponibles en la plataforma

    // Endpoint para listar perfiles de usuario: POST http://localhost:8081/perfil/listarPerfiles
  @GetMapping("/listarPerfiles")
   public ResponseEntity<?> listadoUsuarios() {
        return ResponseEntity.ok(perfilService.listarPerfiles());
   }

}    