package com.plataformadeportiva.auth_service.models;

/**
 * La clase PerfilUsuario representa los diferentes perfiles o roles que un usuario puede tener en el sistema de autenticación. 
 * Cada perfil define un conjunto de permisos y funcionalidades a las que el usuario tiene acceso.
 * 
 * Esta clase se mapeará a la tabla "gpdp_perfiles_usuarios" en la base de datos, lo que permitirá almacenar y gestionar los perfiles de manera eficiente.
 * Cada perfil tendrá un nombre único (por ejemplo, "ADMIN", "DEPORTISTA") y una descripción opcional que explique qué permisos o funcionalidades están asociados 
 * con ese perfil.
 * 
 * La anotación @Data de Lombok se utiliza para generar automáticamente los métodos necesarios para acceder
 * y modificar los campos de la clase, lo que simplifica el código y mejora su legibilidad. 
 * 
 * En resumen, la clase PerfilUsuario es fundamental para implementar un sistema de roles y permisos en la plataforma deportiva, permitiendo gestionar 
 * el acceso de los usuarios a diferentes funcionalidades según su perfil asignado.
 *  
 */
import jakarta.persistence.*;

/**
 * Anotaciones de JPA:
 * Entity: Indica que esta clase es una entidad que se mapeará a una tabla en la base de datos.
 * Table(name = "gpdp_perfiles_usuarios"): Especifica el nombre de la tabla en la base de datos a la que se mapeará esta entidad.
 * Id: Indica que el campo "id" es la clave primaria de la tabla.
 * GeneratedValue(strategy = GenerationType.IDENTITY): Especifica que el valor del campo "id" se generará automáticamente por la base de datos.
 * Column: Define las propiedades de cada columna en la tabla, como si es única, si puede ser nula, etc
 */
//import lombok.Data;

@Entity
@Table(name = "gpdp_perfiles_usuarios")

public class PerfilUsuario {

    @Id // Indica que este campo es la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Especifica que el valor de este campo se generará automáticamente por la base de datos
    @com.fasterxml.jackson.annotation.JsonProperty("id_perfil") // Anotación para mapear el nombre del campo en JSON a "id_perfil" en lugar de "idPerfil"
    private Long id_perfil; // El ID del perfil, que se generará automáticamente

    // Nombre del perfil (ej: "ADMIN", "DEPORTISTA")
    @Column(nullable = false, unique = true) // Indica que este campo no puede ser nulo y debe ser único en la base de datos
    private String perfil; // El nombre del perfil, que debe ser único para evitar duplicados

    // Descripción opcional de qué puede hacer este perfil
    private String descripcion; // La descripción del perfil, que es opcional y puede ser nula en la base de datos

    /**
     * Getters y Setters para los campos de la clase PerfilUsuario. Estos métodos permiten acceder y modificar los valores de los campos de manera controlada.
     * return El ID del perfil, el nombre del perfil y la descripción del perfil.
     */
    public Long getId_perfil() {
        return id_perfil;
    }

    /**
     *  Setters para el campo id_perfil. Aunque este campo se genera automáticamente, es importante tener un setter para permitir la asignación del valor generado por la base de datos.
     *  param id_perfil El ID del perfil a asignar, que se generará automáticamente por la base de datos, lo que es esencial para mantener la integridad de los datos y garantizar que cada perfil tenga un identificador único en la base de datos.
     */
    public void setId_perfil(Long id_perfil) {
        this.id_perfil = id_perfil;
    }

    /**
     *  Getters y Setters para el campo perfil. El getter permite obtener el nombre del perfil, mientras que el setter permite asignar un nombre de perfil específico al crear o modificar un perfil de usuario.
     *  return El nombre del perfil.
     */
    public String getPerfil() {
        return perfil;
    }

    /**
     * Setters para el campo perfil. El setter permite asignar un nombre de perfil específico al crear o modificar un perfil de usuario, lo que es esencial para mantener la información del perfil actualizada y precisa en la base de datos, además de garantizar que el nombre del perfil sea único para evitar duplicados.
     * param perfil El nombre del perfil a asignar, que debe ser único para evitar duplicados.
     */
    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    /**
     * Getters y Setters para el campo descripcion. El getter permite obtener la descripción del perfil, mientras que el setter permite asignar una descripción específica al crear o modificar un perfil de usuario. La descripción es opcional y puede ser nula en la base de datos, pero es útil para proporcionar información adicional sobre los permisos o funcionalidades asociados con ese perfil.
     * return La descripción del perfil.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Setters para el campo descripcion. El setter permite asignar una descripción específica al crear o modificar un perfil de usuario. La descripción es opcional y puede ser nula en la base de datos, pero es útil para proporcionar información adicional sobre los permisos o funcionalidades asociados con ese perfil.
     * param descripcion La descripción del perfil a asignar, que es opcional y puede ser nula en la base de datos.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }



}