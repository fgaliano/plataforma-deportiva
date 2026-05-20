***************************************************************************************************************************************************************************************************************
***************************************************************************************************************************************************************************************************************
***************************************************************************************************************************************************************************************************************
																			INFORMACIÓN TIPOS DE APLICACIÓN
***************************************************************************************************************************************************************************************************************
***************************************************************************************************************************************************************************************************************
***************************************************************************************************************************************************************************************************************

Monorepo/Multirepo

Trabajar con un solo proyecto de Git que contenga todos tus microservicios y frontends dentro es la opción ganadora por tres motivos fundamentales:

	1. Sincronización perfecta de cambios: Imagina que modificas el backend de Login para que devuelva un nuevo dato del usuario. 
	  Con un Monorepo, modificas el Java del backend, modificas el React del frontend en la misma ventana, 
	  haces un único git commit ("Añadido campo perfil al login") y subes todo junto a GitHub. No pierdes el hilo de lo que estás haciendo.
	  
	2. Orquestación centralizada (Docker Compose): Al tener todas las carpetas juntas, creas un único archivo docker-compose.yml en la raíz. 
	   Con escribir docker compose up -d desde la raíz, Docker lee todas las subcarpetas, construye las imágenes y levanta tus 3 backends, tus 3 fronts y la base de datos de golpe. 
	   Es una maravilla para trabajar en local.  
	
	3. Refactorización masiva sencilla: Si mañana decides cambiar una librería común o el formato de las respuestas de tus APIs, puedes buscar y reemplazar texto en todo tu ecosistema a la vez desde VS Code.
	
Cuándo se usa el Multirepo

	1. Se usa cuando hay cientos de desarrolladores divididos en equipos independientes.
	2. El "Equipo de Rutas" tiene su propio repositorio de Git y sube cambios a producción sin enterarse de lo que hace el "Equipo de Login" en su propio repositorio. Así evitan molestarse unos a otros con los commits.
	3. El inconveniente: Coordinar cambios que afectan a varios repositorios a la vez se convierte en una pesadilla burocrática y de gestión de terminales.
	
El estándar moderno

	Lo más inteligente, moderno y lo que mejor va a lucir en tu LinkedIn para demostrar que sabes gestionar un proyecto completo (Full Stack / DevOps) es montar el Monorepo. 
	Demuestra que eres capaz de estructurar una arquitectura compleja de manera limpia y organizada.
	
	Tu idea de empezar de cero con la estructura unificada es el camino idóneo. Te va a permitir entender al 100% cómo se acoplan las piezas sin volverte loco abriendo y cerrando ventanas de comandos.	
	
Escalar aplicación

	La respuesta corta es: No, no escalas la aplicación entera. Escalas únicamente el microservicio que lo necesita. Y esa es la magia absoluta de este sistema.
	Para entenderlo de forma muy clara, imagínate el día en que se abren las inscripciones para una maratón muy importante en tu aplicación. Ese día, miles de usuarios van a entrar a la vez a hacer clic en "Inscribirme".
	
	El enfoque antiguo (Monolito): 
		Escalar TODO
	
	El enfoque moderno (Microservicios): Escalamiento Horizontal Selectivo
		Como tienes el sistema separado en el Monorepo (recuerda: carpetas juntas para programar, pero contenedores totalmente independientes en Docker), puedes hacer lo que se conoce como Escalamiento Horizontal.
		El día de la avalancha de inscripciones, dejas el servicio de Login (auth-service) con un solo contenedor (porque la gente se loguea una vez y ya está) y dejas el de rutas con un solo contenedor. 
		Sin embargo, a través de tu orquestador, le dices a Docker: "Quiero 5 réplicas del servicio de inscripciones ahora mismo".
		
			- Docker Compose (o herramientas más avanzadas en producción como Kubernetes o Docker Swarm) levantará de forma instantánea 5 clones idénticos de ese microservicio específico:
				inscripciones-service-1 (Puerto interno 8083)
				inscripciones-service-2 (Puerto interno 8084)
				inscripciones-service-3 (Puerto interno 8085)		
				
	Para que esto funcione, se coloca una pieza delante llamada Balanceador de Carga (como Nginx o Spring Cloud Gateway). Cuando los usuarios envían sus solicitudes de inscripción, 
	el balanceador las recibe y las va repartiendo como un crupier de cartas: la primera petición va al clon 1, la segunda al clon 2, la tercera al clon 3		
	Ambos se pueden "instalar" (contenerizar) dentro de tu entorno de Docker tal y como hiciste con la base de datos PostgreSQL.	
	
***************************************************************************************************************************************************************************************************************
***************************************************************************************************************************************************************************************************************
***************************************************************************************************************************************************************************************************************
																			PASOS PARA CREAR EL PROYECTO
***************************************************************************************************************************************************************************************************************
***************************************************************************************************************************************************************************************************************
***************************************************************************************************************************************************************************************************************	
	
Paso 1: Creación del proyecto:

	1.2 Crear las carpetas e iniciar el repositorio Git
			plataforma-deportiva/
			├── backend-services/  (Vacía por ahora)
			└── frontend-apps/     (Vacía por ahora)
		
	1.3 Inicializar el repositorio de GIT
			git init

	1.4 El toque maestro (Crear el archivo .gitignore)
		Abre la carpeta raíz plataforma-deportiva en VS Code.
		Crea un archivo nuevo en la raíz (al mismo nivel que backend-services) y llámalo exactamente: .gitignore
		Pega este contenido básico dentro para proteger tu repositorio:
			# Carpetas de compilación de Java
			**/target/
			**.jar
			**.war

			# Carpetas de dependencias de JavaScript/React
			**/node_modules/
			**/dist/

			# Archivos de configuración del sistema y de VS Code
			.workspace/
			.vscode/
			.DS_Store
			Thumbs.db
			
	
Paso 2:	Generar el nuevo auth-service		
	Ahora que tienes tu mapa del Monorepo creado y tu "guía de consulta" abierta a un lado, vamos a meter el primer habitante en la carpeta backend-service
		
	2.1 Ve a tu navegador y abre start.spring.io. (para crear automaticamente el pom.xml)	
	
		Configura el proyecto con estos datos básicos:
			- Project: Maven
			- Language: Java
			- Spring Boot: Selecciona la versión estable que no tenga texto al lado (por ejemplo, la 3.3.x o 3.4.x, evita las que digan Snapshot o Milestone).
			- Java: Elige la versión que tengas instalada en tu PC (comúnmente la 17 o 21).
			- Group: Escribe com.plataformadeportiva
			- Artifact: Escribe auth-service
			- Name: Se cambiará solo a auth-service al escribir el artifact. Si no, escríbelo tú.
			- Description: Puedes poner algo como: Microservicio de autenticacion y perfiles para la plataforma deportiva
			- Package name: Se generará automáticamente como com.plataformadeportiva.authservice
			- Packaging: Déjalo en Jar (tal y como está seleccionado).
			- Java: Tienes marcado 21, que está genial si es la versión que tienes configurada en tu VS Code.
			
	2.2 Siguiente paso: Las dependencias
		
		Mira arriba a la derecha de la pantalla de Spring Initializr. Verás un botón que dice "Add Dependencies" (o un buscador). Añade estas 5 que comentamos antes:

			- Spring Web (para crear los controladores de la API).
			- Spring Security (para gestionar el login y los permisos).
			- Spring Data JPA (para interactuar con la base de datos mediante objetos Java).
			- PostgreSQL Driver (para que Spring sepa hablar el idioma de tu base de datos de Docker).
			- Lombok (para ahorrarte escribir los Getters, Setters y constructores a mano).
	
			plataforma-deportiva/
			├── .gitignore
			├── backend-services/
			│   └── auth-service/        👈 ¡Aquí dentro estará tu nuevo proyecto limpio!
			│       ├── pom.xml
			│       └── src/
			└── frontend-apps/
			
		Para que tu Monorepo funcione de forma centralizada y no haya reglas chocando entre sí, haz clic derecho sobre el .gitignore que está DENTRO de auth-service y bórralo (Delete). 
		Así toda la configuración de Git dependerá exclusivamente de nuestro .gitignore principal, el de la raíz.	
		
	2.3 Siguiente paso: Configurar el pom.xml y entender qué tenemos	
		Abre el archivo pom.xml que tienes ahí en pantalla. Vamos a hacer un repaso rápido de lo que Spring ha metido en tu proyecto basándose en lo que elegimos en la web.
		Busca la sección <dependencies> y comprueba que tienes estas piezas clave. Esto es lo que significa cada una:
		
		El primer obstáculo (Por qué no va a arrancar todavía):
		Si intentáramos arrancar la aplicación de Java ahora mismo, daría un error y se apagaría de inmediato.
		¿Por qué? Porque le hemos dicho a Spring: "Oye, vas a usar JPA y PostgreSQL", 
		pero todavía no le hemos dicho en qué dirección IP de Docker está la base de datos, ni cómo se llama, ni cuál es la contraseña. Spring, al no encontrar esos datos, entra en pánico y se para.
		
		Para solucionar esto, vamos a crear el archivo de configuración. Abre la siguiente ruta en las carpetas:
			backend-services ➡️ auth-service ➡️ src ➡️ main ➡️ resources
			
		Ahí dentro verás un archivo vacío llamado application.properties. Ábrelo. ¿Lo tienes localizado en tu pantalla? Dime y le metemos las credenciales de la base de datos repasando qué significa cada línea.	
		El archivo application.properties es donde le damos las órdenes iniciales a nuestro servicio. Aquí es donde conectamos los cables con el mundo exterior (en este caso, con la base de datos PostgreSQL que levantaremos en Docker).
		Vamos a escribir la configuración desde cero. Copia y pega las siguientes líneas dentro de tu archivo application.properties:
		
		
			# 1. Identificación del microservicio
			spring.application.name=auth-service
			server.port=8081

			# 2. Configuración de la conexión a la Base de Datos (JDBC)
			spring.datasource.url=jdbc:postgresql://localhost:5432/bbdd-plataforma
			spring.datasource.username=usuario_postgres
			spring.datasource.password=clave_secreta
			spring.datasource.driver-class-name=org.postgresql.Driver

			# 3. Estrategia de JPA / Hibernate
			spring.jpa.hibernate.ddl-auto=update
			spring.jpa.show-sql=true
			spring.jpa.properties.hibernate.format_sql=true
			

Paso 3: Crear el archivo docker-compose.yml
	
	Ahora mismo, si intentáramos arrancar la aplicación, seguiría fallando porque la base de datos bbdd-plataforma todavía no existe en tu ordenador.
	El siguiente paso lógico es crear el archivo docker-compose.yml en la raíz de nuestro Monorepo para levantar esa base de datos en un segundo.
	El archivo docker-compose.yml es el plano de construcción de todo tu ecosistema. Como estamos en un Monorepo, este archivo tiene que vivir en la raíz absoluta del proyecto, 
	justo al mismo nivel que tu carpeta backend-services y tu archivo .gitignore principal.	
	
	Paso 3.1: Crear el archivo
		- Haz clic derecho en una zona vacía del explorador de archivos de VS Code (asegúrate de no estar metido dentro de auth-service).
		- Crea un archivo nuevo y llámalo exactamente: docker-compose.yml
		- Copia y pega el siguiente contenido, diseñado a medida para tu nueva plataforma deportiva:
		
			version: '3.8'

			services:
			  # El motor de nuestra Base de Datos
			  bbdd-plataforma:
				image: postgres:15-alpine
				container_name: postgres_deportivo
				restart: always
				environment:
				  POSTGRES_DB: bbdd-plataforma
				  POSTGRES_USER: usuario_postgres
				  POSTGRES_PASSWORD: clave_secreta
				ports:
				  - "5432:5432"
				volumes:
				  - data-deportiva:/var/lib/postgresql/data
				networks:
				  - red-deportiva

			# Volúmenes para que los datos no se borren al apagar el contenedor
			volumes:
			  data-deportiva:

			# Red interna para que los microservicios se hablen entre sí en el futuro
			networks:
			  red-deportiva:
				driver: bridge
				
		Repaso técnico: ¿Qué significan los puntos clave?
			
			- image: postgres:15-alpine: Usamos la versión 15 de PostgreSQL. La coletilla -alpine significa que es una versión ultra ligera que consume poquísima RAM y espacio en tu disco, ideal para desarrollo.
			- environment: Aquí defines las tres variables que lee Postgres al nacer para configurarse solo. 
						   Si te fijas, coinciden exactamente con lo que acabamos de escribir en el application.properties de Java. Tienen que ser almas gemelas para que conecten.
			- ports: - "5432:5432": El primer número es el puerto de tu ordenador real (Windows) y el segundo es el del contenedor de Docker. 
									Al poner esto, permites que tu Spring Boot (que se ejecuta en tu local) pueda "entrar" al contenedor a través del puerto 5432.				
			- volumes: Esto es vital. Si no pones un volumen, el día que apagues el ordenador o reinicies el contenedor, todos los usuarios registrados y los datos de las pruebas deportivas se borrarán para siempre. 
					   El volumen crea un "túnel" que guarda los datos en tu disco duro real a salvo.								
			