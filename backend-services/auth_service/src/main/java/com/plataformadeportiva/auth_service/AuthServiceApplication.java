package com.plataformadeportiva.auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * La clase AuthServiceApplication es la clase principal de la aplicación de autenticación. Esta clase se anota con @SpringBootApplication, lo que indica que es una aplicación Spring Boot y habilita la configuración automática, el escaneo de componentes y otras características de Spring Boot.
 * El método main es el punto de entrada de la aplicación, donde se llama a SpringApplication.run para iniciar la aplicación de autenticación. Esto arranca el servidor web integrado de Spring Boot y
 * 	carga el contexto de la aplicación, lo que permite que los controladores, servicios y repositorios definidos en la aplicación estén disponibles para manejar las solicitudes HTTP y realizar las operaciones necesarias para la gestión de usuarios y perfiles en la plataforma deportiva.
 * En resumen, la clase AuthServiceApplication es fundamental para iniciar y configurar la aplicación de autenticación, permitiendo que la plataforma deportiva pueda gestionar de manera eficiente y segura el acceso de los usuarios
 * a través de la autenticación y autorización basada en perfiles.
 */
@SpringBootApplication
public class AuthServiceApplication {

	/**
	 * El método main es el punto de entrada de la aplicación, donde se llama a SpringApplication.run para iniciar la aplicación de autenticación. Esto arranca el servidor web integrado de Spring Boot y carga el contexto de la aplicación, lo que permite que los controladores, servicios y repositorios definidos en la aplicación estén disponibles para manejar las solicitudes HTTP y realizar las operaciones necesarias para la gestión de usuarios y perfiles en la plataforma deportiva.	
	 * param args Los argumentos de línea de comandos que se pueden pasar al iniciar la aplicación, aunque en este caso no se utilizan, pero se incluyen como parte de la firma del método main para cumplir con la convención estándar de Java para el punto de entrada de una aplicación.
	 * 
	 * */
	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
