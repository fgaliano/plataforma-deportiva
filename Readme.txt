*******************************
PASAR UN TXT a HTML
*******************************

	1. Abre tu terminal e instala la librería con este comando:
		pip install markdown

	2. 	

*******************************
 PROBAR BACK Y FRONT
*******************************

Para probarlo todo a la vez desde VS Code, sí, lo ideal es abrir dos terminales independientes

	1. Back: El Backend en Vivo (Spring Boot), mejor buscar archivo AuthServiceApplication y ejecutar desde el menu Run de vscode
	2. Front: añadir esto por cada front en launch.json QUE ESTA DENTRO DE .vscode

			{
		"configurations": [
			{
			"type": "java",
			"name": "Spring Boot-AuthServiceApplication<auth_service>",
			"request": "launch",
			"cwd": "${workspaceFolder}",
			"mainClass": "com.plataformadeportiva.auth_service.AuthServiceApplication",
			"projectName": "auth_service",
			"args": "",
			"envFile": "${workspaceFolder}/.env"
			},
			{
			"type": "chrome",
			"request": "launch",
			"name": "Depurar Frontend en Chrome",
			"url": "http://127.0.0.1:5500/frontend-apps/auth_front_app/index.html",
			"webRoot": "${workspaceFolder}/frontend-apps/auth_front_app"
			}
		]
		}

	

*******************************
COMANDOS GIT
*******************************

Subirlo a tu GitHub de internet

	1. Abre tu navegador de internet y entra en tu cuenta de GitHub.
	2. Arriba a la derecha, haz clic en el botón + y selecciona New repository (Nuevo repositorio).
	3. Dale el nombre oficial a tu proyecto: plataforma-deportiva.
	4. Déjalo como Public o Private (lo que prefieras), pero MUY IMPORTANTE: no marques ninguna casilla de Add a README, Add .gitignore ni Choose a license (ya los tenemos creados en local y si los marcas dará conflicto).
	5. Haz clic en el botón verde Create repository abajo del todo.

Al crearlo, GitHub te mostrará una pantalla con unos comandos. Copia y ejecuta en tu terminal de VS Code las tres líneas finales 
(las que sirven para emparejar tu PC con la web), que se verán exactamente así (cambiando tu nombre de usuario):

	# 1. Renombrar la rama principal a 'main'
	git branch -M main

	# 2. Conectar tu Git local con el servidor de GitHub (Cambia 'tu-usuario' por el tuyo real)
	git remote add origin https://github.com/tu-usuario/plataforma-deportiva.git

	# 3. Subir el código por primera vez
	git push -u origin main

Comandos:
	0. Primero, vamos a comprobar qué archivos va a guardar Git. Escribe: git status
	1. Para decirle a Git que queremos guardar absolutamente todo lo nuevo, ejecuta: git add .
	2. Ahora guardamos este estado en tu historial local con un mensaje descriptivo: 
		git commit -m "Feat: Inicializacion del Monorepo, Docker Compose con Postgres y Entidad Usuario"

Comandos crear rama, subir y volver a main sin merge:
	0. git checkout -b rama_esqueleto_limpio
	1. git add .
	2. git commit -m "Feat: Estructura base del Monorepo y paquetes del microservicio de autenticacion"
	3. git push -u origin rama_esqueleto_limpio
	4. git checkout main

Comandos para guardar cambios:
	0. git status (se ven los cambios en rojo)
	1. git add . ((Ese punto . significa "añade absolutamente todo lo que haya cambiado o sea nuevo"). Si vuelves a hacer git status, verás que todo se habrá puesto en verde.)

Comando para fusionar una rama con main y hacer otra nueva:
	1. Nos movemos a main
	git checkout main

	2. Absorber los cambios de tu rama de Mayo
	git merge develop_01_Mayo_2026

	3. Subir el main actualizado a tu nube (GitHub/GitLab)
	git push origin main


*******************************
ENTREGAS A PRODUCCION DOCKER
*******************************

Trabajar con monorrepositorios y muchos microservicios tiene su truco, pero una vez que organizas la estructura mental y el flujo de Git, 
es una delicia porque lo tienes todo bajo el mismo techo.

Vamos a estructurarlo en dos partes: cómo organizar las ramas en Git para producción y cómo mapearlo en tu Docker Compose (tu stack).

1. Organización de Ramas en un Monorepo
	En un monorepo, la regla de oro es que las ramas son globales, afectan a todo el repositorio, pero tus commits especifican qué servicio cambia. 
	Para simular una entrega a producción de forma profesional, lo estándar es usar el flujo Git Flow simplificado:

		1.1 main (o master): Es tu línea sagrada. Lo que está aquí es lo que está corriendo en producción (estable y sin fallos).
		1.2 develop (donde estás ahora): Es tu línea de batalla. Aquí vas metiendo las funciones del día a día, el registro, el login, etc.
		1.3 Vamos a congelar lo que tenemos ahora, asegurar que está en develop y crear la rama main para hacer el despliegue.
		1.4 Asegura tus cambios actuales en develop.
			cd d:\repos_GIT\plataforma-deportiva
			git checkout -b develop_01_Mayo_2026 --> crea la rama develop y se mueve a ella
			git add . --> añade todos los cambios.
			git commit -m "Build: Preparando release para producción" --> se hace commit.
			git push origin develop_01_Mayo_2026 --> y se guardan en la rama develop.
			git push -u origin develop_02_Junio_2026
			
		1.5 Crea la rama de producción (main) a partir de tus cambios:
			git checkout -b main --> crea la rama main a partir de estos ultimos cambios y se posiciona en ella.
		1.6 Dockerfile: Ve a tu editor de archivos en VS Code y, dentro de la carpeta backend-services/auth_service, crea un archivo nuevo llamado Dockerfile 
		    (sin ninguna extensión, solo ese nombre).	

			# --- ETAPA 1: Compilación (Cambiado a Java 21) ---
			FROM maven:3.9.6-eclipse-temurin-21 AS builder
			WORKDIR /app

			# Copiamos el pom y el código fuente para compilar
			COPY pom.xml .
			COPY src ./src

			# Compilamos el jar empaquetado saltándonos los tests
			RUN mvn clean package -DskipTests

			# --- ETAPA 2: Ejecución en Producción (Cambiado a JRE 21) ---
			FROM eclipse-temurin:21-jre-jammy
			WORKDIR /app

			# Nos traemos el archivo .jar generado en la etapa anterior
			COPY --from=builder /app/target/*.jar app.jar

			# Exponemos el puerto del microservicio
			EXPOSE 8081

			# Comando para arrancar tu aplicación de Spring Boot
			ENTRYPOINT ["java", "-jar", "app.jar"]

		1.7 Modifica tu docker-compose.yml (se añaden los servicios nuevos al que habia solo la bbdd)
			Sustituye por completo el contenido de tu archivo por este. He fusionado tu base de datos actual con el nuevo contenedor del backend cuidando 
			perfectamente la indentación (los espacios):

			services:
			# ################################################
			# 1. El motor de nuestra Base de Datos
			# ################################################
			bbdd-plataforma:
				image: postgres:15-alpine
				container_name: postgres_deportivo
				restart: always
				environment:
				POSTGRES_DB: bbdd-plataforma
				POSTGRES_USER: admin
				POSTGRES_PASSWORD: 123456789
				ports:
				- "5432:5432"
				volumes:
				- data-deportiva:/var/lib/postgresql/data
				networks:
				- red-deportiva

			# ################################################
			# 2. Tu Microservicio de Autenticación (¡NUEVO!)
			# ################################################
			auth-service:
				container_name: auth_service_container
				restart: always
				build:
				context: ./backend-services/auth_service
				dockerfile: Dockerfile
				ports:
				- "8081:8081"
				environment:
				# Apuntamos la URL al nombre del contenedor de la BBDD 'bbdd-plataforma'
				- SPRING_DATASOURCE_URL=jdbc:postgresql://bbdd-plataforma:5432/bbdd-plataforma
				- SPRING_DATASOURCE_USERNAME=admin
				- SPRING_DATASOURCE_PASSWORD=123456789
				depends_on:
				- bbdd-plataforma
				networks:
				- red-deportiva

			# Volúmenes para que los datos no se borren al apagar el contenedor
			volumes:
			data-deportiva:

			# Red interna para que los microservicios se hablen entre sí en el futuro
			networks:
			red-deportiva:
				driver: bridge

		1.8 Lanzar el Stack: Guarda el archivo, abre tu terminal en la raíz (plataforma-deportiva) y dale caña al comando de despliegue.
			docker compose down
			docker compose up --force-recreate --build -d

		1.9 Si da error: no main manifest attribute, in app.jar
			añadir esto al pom.xml
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>	

*******************************
PASO A PRODUCCION DOCKER
*******************************
0. git status (se ven los cambios en rojo)
1. git add .
2. git commit -m "Feat: Estructura base del Monorepo y paquetes del microservicio de autenticacion"
3. git push -u origin nombre_rama
4. git checkout main
5. git merge nombre_rama
6. docker compose down
7. docker compose up --force-recreate --build -d

*******************************
GENERAR JAVADOC
*******************************

	1. mvn clean javadoc:javadoc -Dadditionalparam="-Xdoclint:none" -DfailOnError=false

	Primero: "Genérame los archivos HTML estrictos de mi código Java (los apidocs)".
	Segundo: "Monta la web del sitio e integra los apidocs dentro".

*******************************
INFORMACIÓN TIPOS DE APLICACIÓN
*******************************

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
	

*******************************
PASOS PARA CREAR EL PROYECTO
*******************************
	
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

	Paso 4.1 Levantar la base de datos
		
			- Abre la terminal integrada de VS Code (Terminal -> New Terminal). Asegúrate de que la ruta de la terminal sea la raíz (.../plataforma-deportiva).
			- Ejecuta el comando mágico de Docker: 
				docker compose up -d 
				(El -d sirve para el "modo de detached", que hace que el contenedor se levante en segundo plano y te deje la terminal libre)
			- Docker empezará a descargar la imagen de Postgres y en unos segundos verás un mensaje verde que dice Started.
			- Si ahora entras en tu navegador a Portainer (http://localhost:9443), verás que ha aparecido de la nada un nuevo contenedor llamado postgres_deportivo funcionando impecable.


Paso 5:
	El objetivo de hoy es hacer que nuestro backend de autenticación funcione de verdad. Para ello, vamos a conectar las capas 
	de Java de nuestro auth-service y a probar los flujos de Registro y Login directamente en la base de datos de Docker.

	El objetivo de hoy es hacer que nuestro backend de autenticación funcione de verdad. Para ello, vamos a conectar las capas de Java de nuestro auth-service y 
	a probar los flujos de Registro y Login directamente en la base de datos de Docker.	

	Las 4 Capas del Microservicio:

		1. models (Modelos / Entidades): 
			Aquí se guardan los "planos" de tus datos. Son clases Java normales, pero decoradas con @Entity para decirle a Spring: "Oye, quiero que crees una tabla en PostgreSQL idéntica a esta clase"

		2. repositories (Repositorios)
			Es el "cable de datos" o el traductor. Aquí creamos interfaces que se conectan con Spring Data JPA. Gracias a esta capa, podremos hacer cosas como buscar un usuario en la base de datos con una sola línea de código, 
			sin necesidad de escribir sentencias SQL a mano.

		3. services (Servicios / Lógica de Negocio)
			Es el "cerebro" de la aplicación. Aquí se programa la lógica inteligente y las reglas de tu plataforma. Por ejemplo: comprobar si el email que introduce un usuario ya está registrado, 
			o pasar la contraseña por un algoritmo de encriptación para que nadie pueda verla en la base de datos si nos hackean.

		4. controllers (Controladores)
			Es la "puerta de entrada" desde el exterior (la API REST). Aquí definimos las URLs (endpoints) a las que llamaremos más tarde. 
			Por ejemplo, cuando creemos el endpoint /auth/login, esta capa recibirá el usuario y contraseña que envíe el Frontend (o Postman), se los pasará al Service para que los verifique y devolverá una respuesta adecuada (como un "OK" o un "Error de contraseña").	

		5. Estructura básica de carpetas
			
			plataforma-deportiva/
			├── .gitignore
			├── docker-compose.yml
			├── backend-services/
			│   └── auth-service/
			│       ├── pom.xml
			│       └── src/
			│           └── main/
			│               ├── java/
			│               │   └── com/
			│               │       └── plataformadeportiva/
			│               │           └── authservice/
			│               │               ├── AuthServiceApplication.java
			│               │               ├── controllers/
			│               │               ├── models/
			│               │               │   └── Usuario.java
			│               │               ├── repositories/
			│               │               │   └── UsuarioRepository.java
			│               │               └── services/
			│               └── resources/
			│                   └── application.properties
			└── frontend-apps/		


			======================================================================
			FLUJO DE DATOS Y ARQUITECTURA EN CAPAS (API REST)
			======================================================================

			Cuando un cliente (Postman / Frontend React) realiza una petición al microservicio, 
			la información viaja de forma unidireccional a través de las siguientes capas:

			[ CLIENTE ] -- (Envía JSON: username/password) --> [ CONTROLLERS ]
																	│
															(Valida HTTP y mapea)
																	▼
																[ SERVICES ]
																	│
															(Aplica Lógica de Negocio)
															(Cifra claves / Verifica)
																	▼
															[ REPOSITORIES ]
																	│
															(Traducción a SQL)
																	▼
															[ POSTGRESQL (Docker) ]

			----------------------------------------------------------------------
			RESPONSABILIDAD DE CADA CAPA:
			----------------------------------------------------------------------

			1. CLIENTE (Postman / Frontend):
			- Envía peticiones HTTP (POST, GET, etc.) portando datos en formato JSON.
			- Recibe la respuesta (JSON + Código de estado HTTP como 200 OK o 400 Bad Request).

			2. CONTROLADOR (Capas 'controllers'):
			- Es la puerta de entrada de la API. Expone los endpoints (URLs como /auth/login).
			- Recibe el JSON y lo transforma automáticamente en un objeto Java (DTO / Modelo).
			- Delega el trabajo pesado al Service y devuelve la respuesta HTTP al cliente.

			3. SERVICIO (Capa 'services'):
			- Contiene el "cerebro" y las reglas de negocio del sistema.
			- Aquí se valida si un usuario ya existe, si las contraseñas coinciden y se 
				aplica la encriptación de seguridad.

			4. REPOSITORIO (Capa 'repositories'):
			- Actúa como puente intermedio (ORM) mediante Spring Data JPA.
			- Traduce los objetos Java a consultas SQL automáticas sin necesidad de picar 
				código de base de datos a mano.

			5. BASE DE DATOS (PostgreSQL en Docker):
			- Almacena de forma permanente y segura las tablas y registros del sistema.					   


Paso 6: Nomenclatura de tablas.

	gpdd_: Tablas maestras o de datos normales (ej. gpdd_usuarios).
	gpdp_: Tablas paramétricas (diccionarios, tipos de deporte, roles).
	gpdr_: Tablas relacionales (tablas intermedias de muchos a muchos, ej: inscripciones).

Paso 7: Crear la Entidad de Usuario y perfiles

	Paso 7.1 Abre tu VS Code.
	Paso 7.2 Ve a la carpeta models (dentro de backend-services/auth-service/src/main/java/com/plataformadeportiva/authservice/models).
	Paso 7.3 Crea un archivo llamado Usuario.java.
	Paso 7.4 Crea un archivo llamado PerfilUsuario.java.
	Paso 7.5 Se relacionan ambas de este modo:
	
			// Aquí ocurre la magia de la relación relacional
			@ManyToOne(fetch = FetchType.EAGER)
			@JoinColumn(name = "perfil_id", nullable = false)
			private PerfilUsuario perfil;

	Esto crea un nuevo campo en Usuario.java llamado perfil_id que almacenará el id del perfil de cada usuario, esto lo hace automaticamente
	ya que busca en el modelo PerfilUsuario el campo con la notacion @id y lo relaciona. Los metodos get y set no hace escribirlos porque
	se ha incluido la notacion import lombok.Data; La respuesta a este misterio está en una pequeña palabra que has puesto justo encima de la definición de tus clases: @Data.

	Esa anotación @Data pertenece a una librería de Java utilísima llamada Lombok.
	En el Java tradicional de hace unos años, los programadores odiábamos tener que escribir (o generar con el IDE) cientos de líneas de código repetitivo solo para los getters, setters, el método toString(), el equals() y el hashCode(). El archivo se volvía gigante y aburrido.
	Al poner @Data, le estás diciendo a Spring Boot:
	"Oye, cuando compiles este proyecto para ejecutarlo, genera tú solo en memoria todos los métodos get y set de cada una de las variables que he escrito, sin que yo tenga que verlos aquí en sucio".		 

	NOTA: Para que tu VS Code no se vuelva loco pintándote líneas rojas de error cuando empecemos a escribir usuario.getNombre() en las otras capas hay
	que instslar la extension Lombok.

Paso 8 Crear los Repositorios (repositories)

	Paso 8.1 Es el "cable de datos" o el traductor. Aquí creamos interfaces que se conectan con Spring Data JPA.
			 Gracias a esta capa, podremos hacer cosas como buscar un usuario en la base de datos con una sola línea de código, 
			 sin necesidad de escribir sentencias SQL a mano.

	Paso 8.2 Crea un archivo llamado PerfilUsuarioRepository.java y UsuarioRepository

Paso 9 Crear el Servicio (services)

	Paso 9.1 Ahora que tenemos los cables de la base de datos, creamos el "cerebro" que gestionará las reglas de negocio 
			(que no se repitan correos ni usuarios y que se asigne bien el perfil).

	Paso 9.2 Crea el archivo UsuarioService.java

Paso 10 Crear el Controlador (controllers)	
	Por último, creamos la puerta de entrada de la API. Este archivo escuchará las peticiones HTTP que vengan desde Postman o desde tu Frontend.

		Paso 10.1 Ve a la carpeta controllers.
		Paso 10.2 Crea un archivo llamado UsuarioController.java

Paso 11: Arquitectura y flujo de datos

		=======================================================================
		FLUJO DE ARQUITECTURA - MICROSERVICIO: auth_service
		=======================================================================

			[ Cliente / Postman ]  (POST: /auth/register)
						│
						▼
		1. CAPA CONTROLADOR (UsuarioController.java)
			- Recibe la petición HTTP y los datos (JSON).
			- Captura errores y devuelve las respuestas (200 OK / 400 Bad Request).
						│
						▼
		2. CAPA SERVICIO (UsuarioService.java)  <-- [El Cerebro]
			- Aplica la lógica de negocio.
			- Valida que no se repitan usuarios o emails.
			- Enlaza el perfil correspondiente al usuario.
						│
						├───► ¿Falta el Perfil? ──► Consulta la tabla paramétrica
						│                                    │
						▼                                    ▼
		3. CAPA REPOSITORIOS (JPA Data)                    │
			├─ UsuarioRepository.java                       │
			└─ PerfilUsuarioRepository.java ◄───────────────┘
						│
						▼
		4. BASE DE DATOS (PostgreSQL)
			├─ gpdd_usuarios          (Tabla Maestra)
			└─ gpdp_perfiles_usuarios (Tabla Paramétrica)

		=======================================================================


		=======================================================================
		FLUJO DE ARQUITECTURA - MICROSERVICIO: auth_service
		=======================================================================

			[ Cliente / Postman ]  (POST: /auth/register)
						│
						▼
		1. CAPA CONTROLADOR (UsuarioController.java)
			- Recibe la petición HTTP y los datos (JSON).
			- Captura errores y devuelve las respuestas (200 OK / 400 Bad Request).
						│
						▼
		2. CAPA SERVICIO (UsuarioService.java)  <-- [El Cerebro]
			- Aplica la lógica de negocio.
			- Valida que no se repitan usuarios o emails.
			- Enlaza el perfil correspondiente al usuario.
						│
						├───► ¿Falta el Perfil? ──► Consulta la tabla paramétrica
						│                                    │
						▼                                    ▼
		3. CAPA REPOSITORIOS (JPA Data)                      │
			├─ UsuarioRepository.java                        │
			└─ PerfilUsuarioRepository.java ◄────────────────┘
						│
						▼
		4. BASE DE DATOS (PostgreSQL)
			├─ gpdd_usuarios          (Tabla Maestra)
			└─ gpdp_perfiles_usuarios (Tabla Paramétrica)

		=======================================================================

		=======================================================================
			ESTRUCTURA DE CARPETAS Y ROLES (Microservicio: auth_service)
		=======================================================================

		📂 auth_service
		│
		├── 📂 controllers
		│    └── 📄 UsuarioController.java    ◄── [EL PUNTO DE ENTRADA]
		│                                         Recibe el JSON desde fuera (Postman)
		│
		├── 📂 services
		│    └── 📄 UsuarioService.java       ◄── [EL CEREBRO / LA LÓGICA]
		│                                         Aplica las reglas (comprueba duplicados)
		│
		├── 📂 repositories
		│    ├─ 📄 UsuarioRepository.java     ◄── [EL TRADUCTOR SQL]
		│    └─ 📄 PerfilUsuarioRepository.java   Busca y guarda datos de forma automática
		│
		└── 📂 models
			├─ 📄 Usuario.java               ◄── [EL REFLEJO DE LA TABLA]
			└─ 📄 PerfilUsuario.java             Representa las tablas de PostgreSQL en Java

		=======================================================================

		NOTA:
		Como bien has intuido, este grupo de 4 piezas se repite para cada entidad principal de tu base de datos:
			Si mañana creas la gestión de "Equipos", tendrás: 
				
				Equipo.java (Modelo)
				EquipoRepository.java
				EquipoService.java
				EquipoController.java.

	Paso 12: Configurar la conexión en Java con la bbdd (application.properties)

	Paso 13: Cargar tabla parametrica.
		Como la tabla gpdp_perfiles_usuarios es paramétrica, no puede estar vacía. Si intentas registrar un usuario con el perfil DEPORTISTA y la tabla de perfiles está vacía, el servicio te lanzará el error de "El perfil no existe".
		Spring Boot tiene un truco genial: si creas un archivo llamado import.sql en la carpeta de recursos, ejecutará esas sentencias automáticamente justo después de crear las tablas.

			Paso 13.1 En esa misma carpeta resources (al lado del application.properties)
			Paso 13.2 Crea un archivo nuevo llamado exactamente import.sql.
			Psso 13.3 Pega estas líneas de inserción
			
			-- Insertar los perfiles por defecto en la tabla paramétrica
			INSERT INTO gpdp_perfiles_usuarios (perfil, descripcion) VALUES ('ADMIN', 'Administrador total de la plataforma deportiva');
			INSERT INTO gpdp_perfiles_usuarios (perfil, descripcion) VALUES ('DEPORTISTA', 'Usuario estándar que realiza actividades y reservas');
			INSERT INTO gpdp_perfiles_usuarios (perfil, descripcion) VALUES ('ENTRENADOR', 'Perfil para gestionar entrenamientos y alumnos');

	Paso 14: Cómo arrancar el microservicio
		Paso 14.1 Entra en la ruta donde está el proyecto de autenticación
			cd backend-services/auth-service

		Paso 14.2 Ahora sí, lanza el arranque de Spring Boot
			mvn spring-boot:run
			mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

	Paso 15: Falla pq hay que crear la clase SecurityConfig		

		package com.plataformadeportiva.auth_service.config;

		import org.springframework.context.annotation.Bean;
		import org.springframework.context.annotation.Configuration;
		import org.springframework.security.config.annotation.web.builders.HttpSecurity;
		import org.springframework.security.web.SecurityFilterChain;

		@Configuration
		public class SecurityConfig {

			@Bean
			public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
				http
					.csrf(csrf -> csrf.disable()) // Desactiva CSRF para que Postman pueda hacer POST sin problemas
					.authorizeHttpRequests(auth -> auth
						.anyRequest().permitAll() // Abre todas las URLs al público (¡adiós al 401!)
					);
				return http.build();
			}
		}

	Paso 16:
		Encriptar contraseña
		Necesitamos decirle a Spring que use BCryptPasswordEncoder como su herramienta oficial para empaquetar contraseñas.

			16.1 Abre tu archivo SecurityConfig.java
			16.2 Inyectar y usar el encriptador en UsuarioService.java
			16.3 Ahora que Spring ya sabe qué es el passwordEncoder, vamos a usarlo en tu UsuarioService.java para codificar la contraseña justo antes de hacer el .save().
				 @Autowired
				 private PasswordEncoder passwordEncoder; // <-- Inyectamos el encriptador que creamos en SecurityConfig
				 (fuera de la declaracion de la clase)


*******************************
CREANDO EL FRONT esto es VANILA JS
*******************************

Paso 1: Crear la estructura de carpetas.	

	Paso 1.1. El Punto de Entrada (index.html): Este archivo va en la raíz de tu carpeta auth_front_app, Es la estructura fija que nunca se recarga; solo muta su contenido central.
		frontend-apps/auth_front_app/index.html
	Paso 1.2. El Vestido Estético: css/styles.css: Este archivo va dentro de la carpeta css/. Define los colores oscuros estilo deportivo, las fuentes y el diseño modular de las tarjetas y botones.	
		frontend-apps/auth_front_app/css/styles.css
	Paso 3.2. El Motor SPA: js/app.js: Este archivo va dentro de la carpeta js/. Controla el enrutamiento interno de la página y dispara las peticiones asíncronas hacia el backend en Docker de forma transparente.
		frontend-apps/auth_front_app/js/app.js

		NOTA: La programación síncrona ejecuta las tareas una tras otra de forma secuencial; el programa se bloquea y debe esperar a que finalice un proceso para continuar con el siguiente.
			  La programación asíncrona permite ejecutar tareas en segundo plano. El programa continúa ejecutándose sin bloquearse y procesa el resultado de la tarea cuando esta termina.

	Paso 4.3. Creamos la subcarpeta de componentes

		frontend-apps
		└── auth_front_app
			├── componentes
			│   ├── home.html        <-- Solo el trozo de HTML de la Home
			│   ├── login.html       <-- Solo el trozo de HTML del Login
			│   └── registro.html    <-- Solo el trozo de HTML del Registro
			├── css
			├── js
			└── index.html

	Paso 4.4: 	
		Ahora que el HTML vive en archivos separados, podemos eliminar por completo el diccionario const vistas = { ... } que ocupaba medio archivo.
		En su lugar, modificamos la función cargarVista para que sea asíncrona, vaya a buscar el archivo correspondiente a la carpeta componentes/ y lo cargue dinámicamente:


Paso 5: Configurar el proyecto con Módulos de JavaScript:
	Tu monorepo va a quedar con una estructura idéntica a la que usan los desarrolladores senior en proyectos de gran envergadura.

	Vamos a romper el archivo único y repartir la lógica en cajas totalmente aisladas. Sigue estos pasos para reestructurar los ficheros en tu carpeta auth_front_app:

	frontend-apps
	└── auth_front_app
		├── componentes
		│   ├── home.html
		│   ├── login.html
		│   └── registro.html
		├── js
		│   ├── app.js            <-- Sigue siendo el motor central
		│   ├── login.js          <-- 🔄 Antes login-logic.js (Emparejado con login.html)
		│   └── registro.js       <-- 🔄 Antes reg-logic.js (Emparejado con registro.html)
		└── index.html

	Paso 5.1: 
		El punto de entrada único (index.html): Este archivo va en la raíz de la carpeta auth_front_app. Es la estructura fija y solo tiene una etiqueta de script de tipo módulo.

	Paso 5.2: Los estilos modernos (css/styles.css)
		Este archivo va dentro de la carpeta css/. Le da el toque oscuro y deportivo a toda la aplicación.

	Paso 5.3: Los fragmentos visuales (Carpeta componentes/)
		Crea estos tres archivos independientes con sus trozos limpios de HTML.	

	Paso 5.4: Los módulos lógicos (Carpeta js/)
		Aquí es donde repartimos la inteligencia del programa de manera emparejada.	


	Explicación del Flujo del Frontend (SPA Dinámica)
	Nuestra arquitectura está basada en una SPA (Single Page Application) con Carga Dinámica de Módulos. El viaje que hace tu aplicación funciona así:

		1. Arranque (index.html): El usuario entra a la web. El navegador lee el index.html (nuestro lienzo fijo) y carga exclusivamente el motor central: js/app.js.
		2. Enrutamiento inicial: Al cargar la página, js/app.js detecta el evento y dispara de forma automática la función cargarVista('home').
		3. Petición del componente: js/app.js hace una petición interna (fetch) a la carpeta componentes/home.html, se descarga ese trozo de código y lo inyecta dentro de la etiqueta <main id="main-content">.
		4. Interacción del Usuario: Cuando el usuario pincha en "Iniciar Sesión" en el Navbar, se ejecuta cargarVista('login').
		5. Carga Inteligente (Split-Coding):
			
			5.1 El motor descarga el archivo visual componentes/login.html.
			5.2 El motor evalúa que la vista es 'login' y, mediante un import() dinámico, descarga al vuelo el archivo de lógica js/login.js, colgando sus funciones 
				en el entorno global (window.ejecutarLogin).

		6. Comunicación con el Backend (Docker): Cuando el usuario rellena el formulario de login y le da a enviar (onsubmit), la función ejecutarLogin() toma el control, 
		   empaqueta los datos en un JSON y dispara un fetch() hacia el puerto de tu contenedor de Docker (http://localhost:8081/api/auth/login)		

		7. Respuesta y Almacenamiento: El Backend procesa la solicitud y devuelve un JSON con el token JWT. El script js/login.js recibe ese token, lo guarda de forma segura en el localStorage del navegador
		   y le ordena al motor central (window.cargarVista('home')) que limpie la pantalla y vuelva a la página de bienvenida.

		================================================================================
		FLUJO DE ARQUITECTURA: FRONTEND MODULAR (SPA) <--> BACKEND (DOCKER)
		================================================================================

		[ Navegador del Usuario ]
			│
			▼ (1. Abre la Web)
		┌────────────┐
		│ index.html │ <─── (Lienzo fijo con Navbar y contenedor <main>)
		└─────┬──────┘
			│
			▼ (2. Carga Inicial)
		┌────────────┐
		│  js/app.js │ <─── (Motor central / Enrutador "conserje")
		└─────┬──────┘
			│
			├─► (3. Al pinchar en un botón del Navbar: ej. 'login')
			│   │
			│   ├───► [ fetch local ] ──────► ┌────────────────────────┐
			│   │                             │ componentes/login.html │ (Solo HTML plano)
			│   │                             └──────────┬─────────────┘
			│   │                                        │ (Inyecta código)
			│   │                                        ▼
			│   │                             ┌────────────────────────┐
			│   │                             │ <main id="main-content">│ (Lienzo mutado)
			│   │                             └────────────────────────┘
			│   │
			│   └───► [ import dinámico ] ──► ┌────────────────────────┐
			│                                 │       js/login.js      │ (Descarga lógica al vuelo)
			│                                 └──────────┬─────────────┘
			│                                            │
			▼                                            ▼ (4. El usuario envía el formulario)
		┌─────────────────────────────────────────────────────────────────────────────┐
		│                      PETICIÓN HTTP ASÍNCRONA (Fetch API)                    │
		└──────────────────────────────────────┬──────────────────────────────────────┘
												│
												▼ (Envía JSON vía POST con usuario/clave)
								┌──────────────────────────────┐
								│   http://localhost:8081/api  │
								└──────────────┬─────────────── Legenda:
											│                =========
											▼                [ Front ] lives in Browser
								┌──────────────────────────────┐ [ Back  ] lives in Docker
								│    BACKEND: Spring Boot      │
								│    (Contenedor Docker)       │
								└──────────────┬───────────────┘
											│
											▼ (Valida en BD e imprime Token JWT)
								┌──────────────────────────────┐
								│    RESPUESTA: JSON + JWT     │
								└──────────────┬─────────────── Punch de vuelta al navegador
											│
											▼
		┌─────────────────────────────────────────────────────────────────────────────┐
		│       js/login.js procesa la respuesta de Spring Boot                       │
		├─────────────────────────────────────────────────────────────────────────────┤
		│  1. Guarda el JWT ───────► [ localStorage: 'token_deportivo' ] (Memoria)    │
		│  2. Ordena redirigir ────► window.cargarVista('home') (Limpia pantalla)     │
 		└─────────────────────────────────────────────────────────────────────────────┘		   


Paso 6: Configurar CORS para que al tener el back en una ruta y el fron en otra no de error el navegador.

	En SecurityConfig de cada backend hay que poner quien tiene permiso de comunicarse con el.
		
		// Para ser más explícitos y seguros, vamos a listar los orígenes permitidos en lugar de usar un comodín
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173", "http://localhost","http://localhost:5500"));

Paso 7: Control de errores del back y mostrar en el front.

		// 🚨 CAPTURAMOS EL ERROR Y DEVOLVEMOS UN JSON
        // Creamos un mapa que Spring convertirá automáticamente a {"message": "El nombre de..."}
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", e.getMessage());

		// Devolvemos un 401 (No autorizado) o 404 (No encontrado) con el JSON dentro
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);

*******************************
CREANDO EL FRONT con react
*******************************		
	Paso 1: Crear la estructura con Vite
		Primero, asegúrate de estar situado en la carpeta donde quieres que viva tu proyecto (por ejemplo, frontend-apps). Si estás en la raíz de tu proyecto, haz cd hacia ella.
		Una vez ahí, ejecuta este comando exacto para generar la plantilla limpia de React

			npm create vite@latest auth_service_front -- --template react

	Paso 2: Entrar e instalar las dependencias
		Vite habrá creado una carpeta llamada auth_service_front. Ahora tenemos que meternos dentro de esa carpeta e instalar los paquetes nativos de React 
		(como la propia librería de React y los motores de desarrollo).				

		cd auth_service_front
		npm install

	Paso 3: Encender el motor	
		npm run dev

	Paso 4: Creación de carpetas.

		src/
		├── assets/             # Archivos estáticos: imágenes, logos, vectores (SVG), fuentes.
		├── components/         # Componentes "globales" y reutilizables por TODA la app (Botones comunes, Inputs genéricos, Spinners de carga).
		├── config/             # Variables de entorno, constantes globales y configuraciones de clientes como  (ajax).
		├── context/            # Gestión del estado global (por ejemplo, los datos del usuario logueado o el token JWT accesibles desde cualquier sitio).
		├── features/           # EL CORAZÓN DE LA APP. Se divide por módulos o funcionalidades del negocio.
		│   ├── auth/           # Todo lo relacionado con Login, Registro, Recuperar Contraseña.
		│   │   ├── components/ # Componentes exclusivos de la autenticación (FormularioLogin, BotonGoogle).
		│   │   ├── services/   # Peticiones fetch/axios exclusivas de Auth (loginService.js).
		│   │   └── hooks/      # Lógica de React personalizada para Auth (useAuth.js).
		│   └── users/          # Siguiente módulo: Gestión de usuarios, listados, edición del perfil.
		├── hooks/              # Custom Hooks globales (reutilizables en toda la app, como usar el almacenamiento local).
		├── routes/             # Configuración del enrutador de la app (React Router) para movernos entre páginas de forma segura.
		├── services/           # Clientes de API globales o interceptores de llamadas HTTP.
		├── styles/             # Estilos CSS globales, temas de diseño o variables de color.
		├── App.jsx             # El componente raíz que envuelve las rutas y los estados globales.
		└── main.jsx            # El punto de entrada que engancha React al DOM del navegador. 	


	Paso 5: La Filosofía de React: ¿Cómo funciona por debajo?
		La Gran Diferencia: Renderizado en Servidor (Struts2/JSP) vs. Renderizado en Cliente (React):	
			En el stack moderno que estás montando, Java ya no pinta pantallas. Tu Backend de Java con Spring Boot se convierte única y exclusivamente en una API REST
			pura que solo escupe y recibe JSON. No hay JSPs, no hay Actions de Struts que devuelvan vistas. Todo el peso de la interfaz se traslada al navegador del usuario gracias a React.

		Paso 5.1. El Formulario es "Reactivo" (Se acabó buscar IDs)	
			En Struts2/JSP, para saber qué ha escrito el usuario, tu JavaScript tiene que ir al DOM a "extraer" el valor (document.getElementById('user').value).

			En React, el input y la variable de JavaScript están fusionados. Mira este fragmento del código que pusimos antes:

				const [usuario, setUsuario] = useState('');

				<input 
				type="text" 
				value={usuario} 
				onChange={(e) => setUsuario(e.target.value)} 
				/>

			En tu JS actual: El input tiene el dato y tú vas a buscarlo cuando hace falta.	
			En React: El dato vive en la variable usuario. Cada vez que el usuario pulsa una tecla, el evento onChange actualiza la variable. 
			El input simplemente "refleja" lo que hay en esa variable. Cuando vayas a enviar el formulario por AJAX (fetch), no tienes que leer el formulario; 
			los datos ya los tienes listos en tus variables usuario y password.

		Paso 5.2 Olvídate de ocultar y mostrar elementos a mano (.show() o .hide())	

			En JSP, o bien pintas el error desde el servidor usando etiquetas de Struts (<s:property value="errorMessage"/>), 
			o bien lo gestionas con JavaScript cambiando las clases CSS a mano para ocultar/mostrar el div.
			En React, usamos Renderizado Condicional. El HTML reacciona al valor de tus variables de estado:

				{ error && <div className="error-msg">{error}</div> }

			Si la variable error está vacía (''), React directamente no crea ese fragmento de HTML en el navegador. No es que lo oculte con un display: none; es que no existe.	

		Paso 5.3 Aplicación de Una Sola Página (SPA)	

			Esta es la mayor diferencia con Struts2. En Struts, cuando pasas del Login a la Gestión de Usuarios, el navegador web parpadea, va al servidor, 
			Struts procesa el nuevo JSP y te descarga una página entera nueva.

			En React, el navegador nunca se recarga. Todo el Frontend (Login, Tablas, Formularios de Usuarios) se descarga al principio. 
			Cuando el usuario se loguee con éxito, React simplemente "desmontará" el componente de Login de la pantalla y "montará" el componente 
			de Gestión de Usuarios en el mismo sitio, de forma instantánea y sin parpadeos.


	Paso 6: Para que entiendas la lógica de React al 100%, vamos a estructurar esto en 3 fases claras

		1. El Enrutador (routes/): El que decide si se muestra la pantalla de Login o la de Registro según la URL de la web (sin recargar la página, estilo SPA).
		2. Las Vistas/Componentes (features/auth/components/): Tus formularios oscuros con comentarios línea a línea.	
		3. El Estado Compartido (App.jsx): El director que conecta todo.
		4. En React, el componente funciona de manera "reactiva": los inputs no guardan la información en el HTML; la información vive en variables de JavaScript en tiempo real.
		   Para lograr esto, usamos el concepto más importante de React: el Estado (useState).

		Fase 1: Creación de los Formularios (Login y Registro)

			Cualquier componente de React (como tu Login.jsx) se divide siempre en tres zonas fijas, que siempre van en el mismo orden:

			1. ZONA DE IMPORTACIONES (Los "Imports")
			- Traes herramientas de React, estilos CSS u otros componentes.
			
			2. ZONA DE LÓGICA (El cuerpo de la función JavaScipt)
			- Creación de Estados (useState).
			- Funciones de control (handleSubmit, AJAX fetch, validaciones).
			
			3. ZONA VISUAL (El bloque RETURN)
			- El código HTML (JSX) que se va a pintar en el navegador.

			// =========================================================
			// 1. ZONA DE IMPORTACIONES (Igual que los "import" de Java o los tags de JSP)
			// =========================================================
			import React, { useState } from 'react';
			import '../../../styles/login.css';

			// Declaras la función principal (El componente)
			function Login({ onNavigate }) {

			// =======================================================
			// 2. ZONA DE LÓGICA (Tu código JavaScript puro y duro)
			// =======================================================
			const [usuario, setUsuario] = useState('');
			const [password, setPassword] = useState('');

			const handleSubmit = (e) => {
				e.preventDefault();
				// Validaciones, llamadas AJAX, etc.
			};

			// =======================================================
			// 3. ZONA VISUAL (El bloque RETURN)
			// =======================================================
			// El return marca el final de la lógica y el inicio de la pantalla.
			return (
				<div className="login-container">
				<h2>Iniciar Sesión</h2>
				<form onSubmit={handleSubmit}>
					{/* Tu HTML aquí... */}
				</form>
				</div>
			);
			}

			export default Login;			



	Paso 7: Resumen de como funciona.

		========================================================================
		ARQUITECTURA DE FRONTEND: ECOINTEGRACIÓN DE REACT (Mente Struts2 / Java)
		========================================================================

		1. ARCHIVO ÚNICO (SPA - Single Page Application)
		- index.html: El único HTML real del front de la aplicación/microservicio. 
			Contiene un <div id="root"></div> que actúa como escenario vacío. No se toca NUNCA.
		
		2. EL ENGANCHADOR (main.jsx)
		- Inyecta el componente principal (<App />) y los estilos CSS globales dentro 
			del escenario 'root' del index.html. Equivale al cargador inicial de la app web.

		3. EL CONTROLADOR CENTRAL (App.jsx -> "El struts-config.xml")
		- Orquesta y gestiona la navegación declarativa del Front sin peticiones al servidor.
		- Importa todos los componentes (Home, Login, Registro) y decide cuál se pinta.
		- Usa 'useState' para almacenar la variable en memoria de la pantalla visible.

		4. EL MOTOR DE REACTIVIDAD (useState)
		- const [pantallaActual, setPantallaActual] = useState('home');
		- pantallaActual: Variable de lectura del estado actual.
		- setPantallaActual: Función modificadora ("El interruptor"). Al invocarla, 
			React destruye el HTML viejo en memoria y renderiza el nuevo componente al instante.

		5. PASO DE PARÁMETROS (Props y Callbacks)
		- El padre (App.jsx) delega la función de navegación a sus componentes hijos 
			mediante propiedades (<Home onNavigate={navegarA} />).
		- El hijo ejecuta la función pasando el parámetro mediante eventos del cliente:
			onClick={() => onNavigate('login')}	

		======================================================================================
							FLUJO DE RENDERIZADO E INYECCIÓN DE COMPONENTES
		======================================================================================

		[ index.html ] ──(Contiene <div id="root">)
			│
			▼
		[ main.jsx ]   ──(Inyecta el arranque de React en el 'root')
			│
			▼
		[ App.jsx ]    ──(El "struts-config.xml" / Cerebro de la Aplicación)
			│
			├──► Guarda el Estado: const [pantallaActual] = useState('home')
			│
			└──► Renderizado Condicional (Evaluación de Estado en Cliente):
					│
					├──► Si 'home'     ──► Inyecta [ Home.jsx ] (Fuera de features por genérico)
					├──► Si 'login'    ──► Inyecta [ Login.jsx ] (Dentro de features/auth)
					└──► Si 'registro' ──► Inyecta [ Registro.jsx ] (Dentro de features/register)
			▲
			│ (onNavigate('login') / Evento onClick dispara la función modificadora del Padre)
			└──────────────────────────────────────────────────────────────────────────────┘