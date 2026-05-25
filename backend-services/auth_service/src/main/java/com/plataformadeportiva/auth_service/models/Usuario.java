package com.plataformadeportiva.auth_service.models;

/**
 * Clase que representa a un usuario en el sistema. Esta clase se mapeará a la tabla "gpdd_usuarios" en la base de datos.
 * Contiene los campos necesarios para almacenar la información de un usuario, como su nombre, apellidos, correo electrónico, nombre de usuario, contraseña y perfil.
 * Utiliza anotaciones de JPA para definir la estructura de la tabla y Lombok para generar automáticamente los métodos getters, setters, toString, etc.
 */
import jakarta.persistence.*; 

/**
 * Anotaciones de JPA:
 * @Entity: Indica que esta clase es una entidad que se mapeará a una tabla en la base de datos.
 * @Table(name = "gpdd_usuarios"): Especifica el nombre de la tabla en la base de datos a la que se mapeará esta entidad.
 * @Id: Indica que el campo "id" es la clave primaria de la tabla.
 * @GeneratedValue(strategy = GenerationType.IDENTITY): Especifica que el valor del campo "id" se generará automáticamente por la base de datos.
 * @Column: Define las propiedades de cada columna en la tabla, como si es única, si puede ser nula, etc.
 */
//import lombok.Data; // Para generar getters, setters, toString, etc. automáticamente

/**
 * 
 * La clase Usuario representa a un usuario en el sistema de autenticación. Contiene campos para almacenar la información del usuario, como su nombre, apellidos, 
 * correo electrónico, nombre de usuario, contraseña y perfil.
 * 
 * Esta clase se mapeará a la tabla "gpdd_usuarios" en la base de datos, lo que permitirá almacenar y recuperar la información de los usuarios de manera eficiente. 
 * La anotación @Data de Lombok se utiliza para generar automáticamente los métodos necesarios para acceder y modificar los campos
 * de la clase, lo que simplifica el código y mejora su legibilidad.
 * 
 * Es importante destacar que la contraseña se almacenará en formato cifrado para garantizar la seguridad de los datos del usuario. Además, el campo "perfil" se
 * utilizará para determinar el rol del usuario en el sistema, lo que permitirá implementar diferentes niveles de acceso y funcionalidades según el perfil asignado.
 * En resumen, la clase Usuario es una parte fundamental del sistema de autenticación, ya que representa a los usuarios que interactúan con la plataforma 
 * deportiva y permite gestionar su información de manera segura y eficiente.
 *
 * */
@Entity
@Table(name = "gpdd_usuarios")
public class Usuario {

    // Campos de la clase Usuario que se mapearán a las columnas de la tabla "gpdd_usuarios" en la base de datos
    @Id // Indica que este campo es la clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Especifica que el valor de este campo se generará automáticamente por la base de datos
    private Long id; // El ID del usuario, que se generará automáticamente

    @Column(nullable = false) // Indica que este campo no puede ser nulo en la base de datos
    private String nombre; // El nombre del usuario

    @Column(nullable = false) // Indica que este campo no puede ser nulo en la base de datos
    private String apellidos; // Los apellidos del usuario

    @Column(unique = true, nullable = false) // Indica que este campo debe ser único en la base de datos y no puede ser nulo
    private String mail; // El correo electrónico del usuario, que debe ser único para evitar duplicados

    @Column(unique = true, nullable = false) // Indica que este campo debe ser único en la base de datos y no puede ser nulo
    private String usuario; // El nombre de usuario, que debe ser único para evitar duplicados

    @Column(nullable = false) // Indica que este campo no puede ser nulo en la base de datos
    private String password; // La necesitaremos para que puedan iniciar sesión

    // Relación ManyToOne con PerfilUsuario, lo que significa que cada usuario tiene un perfil asociado
    // engancha de modo automático cel campo perfil_id con el campo id en la tabla "gpdd_usuarios" con el campo id de la tabla "gpdp_perfiles_usuarios"
    @ManyToOne(fetch = FetchType.EAGER) // Especifica que la relación se cargará de manera inmediata (EAGER), lo que significa que el perfil se cargará junto con el usuario
    @JoinColumn(name = "perfil_id", nullable = false) // Especifica el nombre de la columna en la tabla "gpdd_usuarios" que se utilizará para la relación con la tabla "gpdp_perfiles_usuarios" y que no puede ser nula
    private PerfilUsuario perfil; // El perfil asociado al usuario, que se mapeará a la tabla "gpdp_perfiles_usuarios" a través de la columna "perfil"

    /**
     * Getters y Setters para los campos de la clase Usuario. Estos métodos permiten acceder y modificar los valores de los campos de manera controlada.
     */
    public Long getId() {
        return id;
    }

    /**
     *  Setters para el campo id. Aunque este campo se genera automáticamente, es importante tener un setter para permitir la asignación del valor generado por la base de datos.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *  Getters y Setters para el campo nombre. El getter permite obtener el nombre del usuario, mientras que el setter permite asignar un nombre específico al crear o modificar un usuario. 
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Setters para el campo nombre. El setter permite asignar un nombre específico al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos.
     * param nombre El nombre del usuario a asignar al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Getters y Setters para el campo apellidos. El getter permite obtener los apellidos del usuario, mientras que el setter permite asignar apellidos específicos al crear o modificar un usuario.
     * param apellidos Los apellidos del usuario a asignar al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos.
     * return Los apellidos del usuario.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     *  Setters para el campo apellidos. El setter permite asignar apellidos específicos al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos.
     * param apellidos Los apellidos del usuario a asignar al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos.  
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Getters y Setters para el campo mail. El getter permite obtener el correo electrónico del usuario, mientras que el setter permite asignar un correo electrónico específico al crear o modificar un usuario.
     * param mail El correo electrónico del usuario a asignar al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos.
     * return El correo electrónico del usuario.
     */
    public String getMail() {
        return mail;
    }

    /**
     * Setters para el campo mail. El setter permite asignar un correo electrónico específico al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos, además de garantizar que el correo electrónico sea único para evitar duplicados.
     * param mail El correo electrónico del usuario a asignar al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos.
     */
    public void setMail(String mail) {
        this.mail = mail;
    }

    /**
     * Getters y Setters para el campo usuario. El getter permite obtener el nombre de usuario, mientras que el setter permite asignar un nombre de usuario específico al crear o modificar un usuario.
     * param usuario El nombre de usuario a asignar al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos.
     * return El nombre de usuario.
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Setters para el campo usuario. El setter permite asignar un nombre de usuario específico al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos, además de garantizar que el nombre de usuario sea único para evitar duplicados.
     * param usuario El nombre de usuario a asignar al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos.
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * Getters y Setters para el campo password. El getter permite obtener la contraseña del usuario, mientras que el setter permite asignar una contraseña específica al crear o modificar un usuario. Es importante destacar que la contraseña se almacenará en formato cifrado para garantizar la seguridad de los datos del usuario.
     * param password La contraseña del usuario a asignar al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos.
     * return La contraseña del usuario.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setters para el campo password. El setter permite asignar una contraseña específica al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos. Es importante destacar que la contraseña se almacenará en formato cifrado para garantizar la seguridad de los datos del usuario.
     * param password La contraseña del usuario a asignar al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Getters y Setters para el campo perfil. El getter permite obtener el perfil asociado al usuario, mientras que el setter permite asignar un perfil específico al crear o modificar un usuario. El campo "perfil" se utilizará para determinar el rol del usuario en el sistema, lo que permitirá implementar diferentes niveles de acceso y funcionalidades según el perfil asignado.
     * param perfil El perfil a asignar al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos.
     * return El perfil del usuario.
     */
    public PerfilUsuario getPerfil() {
        return perfil;
    }

    /**
     * Setters para el campo perfil. El setter permite asignar un perfil específico al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos. El campo "perfil" se utilizará para determinar el rol del usuario en el sistema, lo que permitirá implementar diferentes niveles de acceso y funcionalidades según el perfil asignado.
     * param perfil El perfil a asignar al crear o modificar un usuario, lo que es esencial para mantener la información del usuario actualizada y precisa en la base de datos.
     */
    public void setPerfil(PerfilUsuario perfil) {
        this.perfil = perfil;
    }

}